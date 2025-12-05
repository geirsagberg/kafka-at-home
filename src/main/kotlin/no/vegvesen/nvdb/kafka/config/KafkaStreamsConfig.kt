package no.vegvesen.nvdb.kafka.config

import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafkaStreams
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration
import org.springframework.kafka.config.KafkaStreamsConfiguration
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@Configuration
@EnableKafkaStreams
class KafkaStreamsConfig {

    @Value("\${spring.kafka.streams.bootstrap-servers}")
    private lateinit var bootstrapServers: String

    @Value("\${spring.kafka.streams.application-id}")
    private lateinit var applicationId: String

    @Value("\${kafka.topics.partitions:100}")
    private var topicPartitions: Int = 100

    @Value("\${kafka.topics.replicas:1}")
    private var topicReplicas: Short = 1

    @Bean(name = [KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME])
    fun kafkaStreamsConfig(): KafkaStreamsConfiguration {
        val props = mutableMapOf<String, Any>()
        props[StreamsConfig.APPLICATION_ID_CONFIG] = applicationId
        props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        props[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name
        props[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name
        props[StreamsConfig.STATE_DIR_CONFIG] = "/tmp/kafka-streams"
        return KafkaStreamsConfiguration(props)
    }

    @Bean
    fun producerFactory(): ProducerFactory<String, String> {
        val configProps = mutableMapOf<String, Any>()
        configProps[org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        configProps[org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] =
            org.apache.kafka.common.serialization.StringSerializer::class.java
        configProps[org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] =
            org.apache.kafka.common.serialization.StringSerializer::class.java
        return DefaultKafkaProducerFactory(configProps)
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(producerFactory())
    }

    @Bean
    fun vegsystemTopic(): NewTopic {
        return TopicBuilder.name("nvdb-vegobjekter-915")
            .partitions(topicPartitions)
            .replicas(topicReplicas.toInt())
            .build()
    }

    @Bean
    fun strekningTopic(): NewTopic {
        return TopicBuilder.name("nvdb-vegobjekter-916")
            .partitions(topicPartitions)
            .replicas(topicReplicas.toInt())
            .build()
    }

    @Bean
    fun transformedTopic(): NewTopic {
        return TopicBuilder.name("nvdb-vegobjekter-transformed")
            .partitions(topicPartitions)
            .replicas(topicReplicas.toInt())
            .build()
    }
}
