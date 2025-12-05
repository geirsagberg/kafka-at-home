package no.vegvesen.nvdb.kafka.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@EnableConfigurationProperties(NvdbApiProperties::class)
class WebClientConfig(private val nvdbApiProperties: NvdbApiProperties) {

    @Bean
    fun nvdbWebClient(): WebClient {
        return WebClient.builder()
            .baseUrl(nvdbApiProperties.baseUrl)
            .defaultHeader("Accept", "application/json")
            .build()
    }

    @Bean
    fun objectMapper(): ObjectMapper {
        return jacksonObjectMapper()
    }
}
