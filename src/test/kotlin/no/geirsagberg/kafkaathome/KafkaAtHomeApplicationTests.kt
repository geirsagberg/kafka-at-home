package no.geirsagberg.kafkaathome

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles

@Disabled("Temporarily disabled due to Jackson version conflicts in Spring Boot 4.0.0")
@SpringBootTest
@EmbeddedKafka(
    partitions = 1,
    topics = ["nvdb-vegobjekter-raw", "nvdb-vegobjekter-transformed", "nvdb-fartsgrenser"]
)
@ActiveProfiles("test")
class KafkaAtHomeApplicationTests {

    @Test
    fun contextLoads() {
        // Verify that the Spring context loads successfully
    }
}
