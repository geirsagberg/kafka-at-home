package no.vegvesen.nvdb.kafka.stream

import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.future.asDeferred
import kotlinx.coroutines.runBlocking
import no.vegvesen.nvdb.api.uberiket.model.EnumEgenskap
import no.vegvesen.nvdb.api.uberiket.model.HeltallEgenskap
import no.vegvesen.nvdb.api.uberiket.model.StedfestingLinjer
import no.vegvesen.nvdb.api.uberiket.model.TekstEgenskap
import no.vegvesen.nvdb.kafka.api.NvdbApiClient
import no.vegvesen.nvdb.kafka.extensions.associate
import no.vegvesen.nvdb.kafka.model.*
import no.vegvesen.nvdb.kafka.repository.ProducerProgressRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant.now
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Service that manages NVDB data ingestion with two operational modes:
 * - BACKFILL: Initial bulk fetch of all vegobjekter for a type
 * - UPDATES: Continuous polling of hendelser (events) for incremental changes
 */
@Service
@ConditionalOnProperty(name = ["nvdb.producer.enabled"], havingValue = "true")
class NvdbDataProducer(
    private val nvdbApiClient: NvdbApiClient,
    private val vegobjektDeltaKafkaTemplate: KafkaTemplate<Long, VegobjektDelta>,
    private val progressRepository: ProducerProgressRepository
) {
    private val logger = LoggerFactory.getLogger(NvdbDataProducer::class.java)

    @Value($$"${nvdb.producer.enabled:false}")
    private var producerEnabled: Boolean = false

    @Value($$"${nvdb.producer.backfill.batch-size:100}")
    private var backfillBatchSize: Int = 100

    @Value($$"${nvdb.producer.updates.batch-size:100}")
    private var updatesBatchSize: Int = 100

    private val isProcessingType915 = AtomicBoolean(false)
    private val isProcessingType916 = AtomicBoolean(false)

    @Scheduled(cron = "0/5 * * * * *")
    suspend fun processType915() {
        if (!producerEnabled) return
        if (!isProcessingType915.compareAndSet(false, true)) {
            logger.debug("Type 915 processing already in progress, skipping")
            return
        }

        try {
            processType(NvdbApiClient.TYPE_VEGSYSTEM)
        } finally {
            isProcessingType915.set(false)
        }
    }

    @Scheduled(cron = "0/5 * * * * *")
    suspend fun processType916() {
        if (!producerEnabled) return
        if (!isProcessingType916.compareAndSet(false, true)) {
            logger.debug("Type 916 processing already in progress, skipping")
            return
        }

        try {
            processType(NvdbApiClient.TYPE_STREKNING)
        } finally {
            isProcessingType916.set(false)
        }
    }

    /**
     * Main processing logic: delegates to backfill or updates based on current mode.
     */
    private suspend fun processType(typeId: Int) {
        val progress = progressRepository.findByTypeId(typeId)

        when (progress?.mode) {
            ProducerMode.BACKFILL -> {
                logger.info("Processing type {} in BACKFILL mode", typeId)
                runBackfillBatch(typeId, progress)
            }

            ProducerMode.UPDATES -> {
                logger.info("Processing type {} in UPDATES mode", typeId)
                runUpdatesCheck(typeId, progress)
            }

            null -> {
                logger.debug("Type {} not initialized, skipping", typeId)
            }
        }
    }

    /**
     * Backfill mode: Fetch vegobjekter using pagination.
     * Processes one batch per invocation, saving progress after each batch.
     */
    private suspend fun runBackfillBatch(typeId: Int, progress: ProducerProgress): ProducerProgress {
        try {
            val start = progress.lastProcessedId
            val futures = mutableListOf<Pair<Long, CompletableFuture<SendResult<Long, VegobjektDelta>>>>()

            nvdbApiClient.streamVegobjekter(typeId, backfillBatchSize, start)
                .collect { apiVegobjekt ->
                    val vegobjekt = toDomain(apiVegobjekt)
                    val future = produceToKafka(typeId, before = null, after = vegobjekt)
                    futures.add(vegobjekt.vegobjektId to future)
                }

            if (futures.isEmpty()) {
                val updatedProgress = progress.copy(
                    mode = ProducerMode.UPDATES,
                    backfillCompletionTime = now(),
                    updatedAt = now()
                )
                progressRepository.save(updatedProgress)
                logger.info(
                    "Backfill complete for type {}, transitioning to UPDATES mode from hendelse ID {}",
                    typeId,
                    progress.hendelseId
                )
                return updatedProgress
            }

            futures.map { it.second.asDeferred() }.awaitAll()

            val lastId = futures.last().first
            val newProgress = progress.copy(
                lastProcessedId = lastId,
                updatedAt = now()
            )
            progressRepository.save(newProgress)
            logger.info(
                "Backfill batch for type {}: processed {} items, last ID = {}",
                typeId, futures.size, lastId
            )
            return newProgress

        } catch (e: Exception) {
            logger.error("Error during backfill for type {}: {}", typeId, e.message, e)
            val errorProgress = progress.copy(
                lastError = e.message,
                updatedAt = now()
            )
            progressRepository.save(errorProgress)
            return errorProgress
        }
    }

    private fun toDomain(apiVegobjekt: no.vegvesen.nvdb.api.uberiket.model.Vegobjekt): Vegobjekt {
        val vegobjekt = Vegobjekt(
            vegobjektId = apiVegobjekt.id,
            vegobjektType = apiVegobjekt.typeId,
            egenskaper = apiVegobjekt.egenskaper!!.associate { (key, value) ->
                key.toInt() to when (value) {
                    is HeltallEgenskap -> value.verdi.toString()
                    is TekstEgenskap -> value.verdi
                    is EnumEgenskap -> value.verdi.toString()
                    else -> error("unexpected egenskap type: ${value::class.simpleName}")
                }
            },
            stedfestinger = apiVegobjekt.stedfesting!!.let {
                when (it) {
                    is StedfestingLinjer -> it.linjer.map {
                        Utstrekning(
                            veglenkesekvensId = it.id,
                            startposisjon = it.startposisjon,
                            sluttposisjon = it.sluttposisjon
                        )
                    }

                    else -> error("unexpected stedfesting type: ${it::class.simpleName}")
                }
            }
        )
        return vegobjekt
    }

    /**
     * Updates mode: Poll hendelser endpoint and fetch full vegobjekt details.
     */
    private fun runUpdatesCheck(typeId: Int, progress: ProducerProgress) = runBlocking {
        try {
            val startHendelseId = progress.hendelseId
            if (startHendelseId == null) {
                logger.error("No hendelse ID stored for type {} in UPDATES mode", typeId)
                return@runBlocking
            }

            val response = nvdbApiClient.fetchHendelser(typeId, startHendelseId, updatesBatchSize)
            val hendelser = response.hendelser

            if (hendelser.isEmpty()) {
                logger.debug("No new hendelser for type {}", typeId)
                return@runBlocking
            }

            logger.info("Fetched {} hendelser for type {}", hendelser.size, typeId)

            // Process each hendelse: fetch full vegobjekt and produce to Kafka
            var lastHendelseId = startHendelseId
            for (hendelse in hendelser) {
                try {
                    val vegobjekt = nvdbApiClient.getVegobjekt(typeId, hendelse.vegobjektId)
                    produceToKafka(typeId, vegobjekt)
                    lastHendelseId = hendelse.hendelseId
                } catch (e: Exception) {
                    logger.error(
                        "Error fetching vegobjekt {} for hendelse {}: {}",
                        hendelse.vegobjektId, hendelse.hendelseId, e.message
                    )
                    // Continue processing other hendelser
                }
            }

            // Save progress with last processed hendelse ID
            val newProgress = progress.copy(
                hendelseId = lastHendelseId,
                updatedAt = now()
            )
            progressRepository.save(newProgress)
            logger.info("Updates processed for type {}, last hendelse ID = {}", typeId, lastHendelseId)

        } catch (e: Exception) {
            logger.error("Error during updates check for type {}: {}", typeId, e.message, e)
            progressRepository.save(
                progress.copy(
                    lastError = e.message,
                    updatedAt = now()
                )
            )
        }
    }

    /**
     * Start backfill for a specific type.
     * Queries the latest hendelse ID and stores it before starting backfill.
     */
    suspend fun startBackfill(typeId: Int) {
        val existing = progressRepository.findByTypeId(typeId)
        if (existing != null && existing.mode == ProducerMode.BACKFILL) {
            logger.warn("Backfill already in progress for type {}", typeId)
            return
        }

        // Query latest hendelse ID before starting backfill
        val latestHendelseId = nvdbApiClient.getLatestVegobjektHendelseId(typeId)

        val now = now()

        val progress = ProducerProgress(
            typeId = typeId,
            mode = ProducerMode.BACKFILL,
            lastProcessedId = null,
            hendelseId = latestHendelseId,
            backfillStartTime = now,
            backfillCompletionTime = null,
            lastError = null,
            updatedAt = now
        )
        progressRepository.save(progress)
        logger.info("Started backfill for type {}, stored hendelse ID {} for later", typeId, latestHendelseId)
    }

    /**
     * Stop/pause backfill for a type by deleting progress record.
     */
    fun stopBackfill(typeId: Int) {
        progressRepository.deleteByTypeId(typeId)
        logger.info("Stopped backfill for type {}", typeId)
    }

    /**
     * Reset backfill: delete existing progress and restart.
     */
    suspend fun resetBackfill(typeId: Int) {
        progressRepository.deleteByTypeId(typeId)
        logger.info("Reset backfill for type {}", typeId)
        startBackfill(typeId)
    }

    /**
     * Get current status for a type.
     */
    fun getStatus(typeId: Int): ProducerProgress? {
        return progressRepository.findByTypeId(typeId)
    }

    /**
     * Produce a vegobjekt to its type-specific Kafka topic.
     */
    private fun produceToKafka(
        typeId: Int,
        before: Vegobjekt?,
        after: Vegobjekt?,
    ): CompletableFuture<SendResult<Long, VegobjektDelta>> {
        val vegobjektId = before?.vegobjektId ?: after?.vegobjektId ?: error("Missing vegobjekt ID")
        val topic = NvdbApiClient.getTopicNameForType(typeId)

        val delta = VegobjektDelta(
            before = before,
            after = after,
        )

        return vegobjektDeltaKafkaTemplate.send(topic, vegobjektId, delta)
    }
}
