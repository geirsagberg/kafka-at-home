package no.vegvesen.nvdb.kafka.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import no.vegvesen.nvdb.api.uberiket.model.Vegobjekt
import no.vegvesen.nvdb.api.uberiket.model.VegobjektDeltaHendelse
import no.vegvesen.nvdb.api.uberiket.model.VegobjektNotifikasjon
import no.vegvesen.nvdb.kafka.config.json
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

/**
 * Client for the NVDB Uberiket API.
 * Provides methods to fetch road network data including road link sequences
 * and road objects like speed limits.
 */
@Service
class NvdbApiClient(private val httpClient: HttpClient) {

    private val logger = LoggerFactory.getLogger(NvdbApiClient::class.java)


    suspend fun streamVegobjekter(
        typeId: Int,
        antall: Int = 500,
        start: Long?,
        ider: Collection<Long>? = null,
    ): Flow<Vegobjekt> = httpClient
        .prepareGet("vegobjekter/$typeId/stream") {
            parameter("start", start)
            parameter("antall", antall)
            parameter("ider", ider?.joinToString(","))
        }.executeAsNdjsonFlow<Vegobjekt>()

    suspend fun getLatestVegobjektHendelseId(typeId: Int): Long = httpClient
        .get("hendelser/vegobjekter/$typeId/siste")
        .body<VegobjektNotifikasjon>()
        .hendelseId

    suspend fun streamVegobjektHendelser(typeId: Int, antall: Int = 500, start: Long?): Flow<VegobjektDeltaHendelse> =
        httpClient
            .prepareGet("hendelser/vegobjekter/$typeId/stream") {
                parameter("start", start)
                parameter("antall", antall)
            }.executeAsNdjsonFlow<VegobjektDeltaHendelse>()

    companion object {
        // Common road object type IDs from NVDB
        const val TYPE_FARTSGRENSE = 105 // Speed limits
        const val TYPE_VEGBREDDE = 583 // Road width
        const val TYPE_VEGREFERANSE = 532 // Road reference
        const val TYPE_KJØREFELT = 616 // Driving lanes
        const val TYPE_FUNKSJONSKLASSE = 821 // Functional road class
        const val TYPE_VEGSYSTEM = 915 // Vegsystem
        const val TYPE_STREKNING = 916 // Strekning

        /**
         * Get the topic name for a specific type.
         */
        fun getTopicNameForType(typeId: Int): String {
            return "nvdb-vegobjekter-$typeId"
        }
    }
}

// Generic ndjson flow extension for HttpStatement
inline fun <reified T> HttpStatement.executeAsNdjsonFlow(): Flow<T> = flow {
    execute { response ->
        if (!response.status.isSuccess()) {
            throw ResponseStatusException(response.status.let { HttpStatusCode.valueOf(it.value) })
        }

        val channel = response.bodyAsChannel()
        channel.ndjsonFlow<T>().collect { emit(it) }
    }
}

// Generic ndjson flow extension for ByteReadChannel
inline fun <reified T> ByteReadChannel.ndjsonFlow(): Flow<T> = flow {
    while (!isClosedForRead) {
        val line = readUTF8Line() ?: break
        if (line.isBlank()) continue
        val item = json.decodeFromString<T>(line)
        emit(item)
    }
}
