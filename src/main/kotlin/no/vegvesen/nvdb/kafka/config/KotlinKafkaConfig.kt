package no.vegvesen.nvdb.kafka.config

import io.github.nomisRev.kafka.Acks
import io.github.nomisRev.kafka.imap
import io.github.nomisRev.kafka.publisher.KafkaPublisher
import io.github.nomisRev.kafka.publisher.PublisherSettings
import jakarta.annotation.PreDestroy
import no.vegvesen.nvdb.kafka.model.VegobjektDelta
import no.vegvesen.nvdb.kafka.serialization.KotlinxJsonSerializer
import org.apache.kafka.common.serialization.ByteArraySerializer
import org.apache.kafka.common.serialization.LongSerializer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KotlinKafkaConfig(
    @Value("\${spring.kafka.streams.bootstrap-servers}")
    private val bootstrapServers: String
) {
    private val logger = LoggerFactory.getLogger(KotlinKafkaConfig::class.java)

    @Bean
    fun publisherSettings(): PublisherSettings<Long, VegobjektDelta> {
        // Adapt existing KotlinxJsonSerializer to work with kotlin-kafka's imap pattern
        val jsonSerializer = KotlinxJsonSerializer<VegobjektDelta>()

        return PublisherSettings(
            bootstrapServers,
            LongSerializer().imap { key: Long -> key }, // identity for Long
            ByteArraySerializer().imap { delta: VegobjektDelta ->
                // Use existing serializer's logic
                jsonSerializer.serialize("", delta) ?: ByteArray(0)
            },
            Acks.All
        )
    }

    @Bean
    fun kafkaPublisher(settings: PublisherSettings<Long, VegobjektDelta>): KafkaPublisher<Long, VegobjektDelta> {
        logger.info("Creating KafkaPublisher with bootstrap servers: {}", bootstrapServers)
        return KafkaPublisher(settings)
    }

    @PreDestroy
    fun cleanup() {
        logger.info("Closing KafkaPublisher")
        // Spring will manage cleanup, but log for observability
    }
}
