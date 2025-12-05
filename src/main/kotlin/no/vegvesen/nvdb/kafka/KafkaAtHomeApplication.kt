package no.vegvesen.nvdb.kafka

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class KafkaAtHomeApplication

fun main(args: Array<String>) {
    runApplication<KafkaAtHomeApplication>(*args)
}
