# Project Structure

## Repository Layout

```
kafka-at-home/
├── .github/
│   └── copilot-instructions.md    # GitHub Copilot instructions
├── .gradle/                        # Gradle build cache
├── .idea/                          # IntelliJ IDEA project files
├── .serena/                        # Serena MCP memories
├── gradle/                         # Gradle wrapper files
│   ├── wrapper/
│   └── gng.cfg
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   └── no/geirsagberg/kafkaathome/
│   │   │       ├── KafkaAtHomeApplication.kt
│   │   │       ├── api/
│   │   │       │   └── NvdbApiClient.kt
│   │   │       ├── config/
│   │   │       │   ├── KafkaStreamsConfig.kt
│   │   │       │   ├── NvdbApiProperties.kt
│   │   │       │   └── WebClientConfig.kt
│   │   │       ├── controller/
│   │   │       │   └── NvdbController.kt
│   │   │       ├── model/
│   │   │       │   └── NvdbModels.kt
│   │   │       └── stream/
│   │   │           ├── NvdbDataProducer.kt
│   │   │           └── NvdbStreamTopology.kt
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       ├── kotlin/
│       │   └── no/geirsagberg/kafkaathome/
│       │       ├── KafkaAtHomeApplicationTests.kt
│       │       └── stream/
│       │           └── NvdbStreamTopologyTest.kt
│       └── resources/
│           └── application-test.yml
├── build.gradle.kts                # Gradle build configuration
├── docker-compose.yml              # Docker Compose for Kafka
├── gradlew                         # Gradle wrapper script (Unix)
├── gradlew.bat                     # Gradle wrapper script (Windows)
├── settings.gradle.kts             # Gradle settings
├── LICENSE                         # MIT License
└── README.md                       # Project documentation
```

## Key Components

### Application Entry Point
- **KafkaAtHomeApplication.kt**: Main Spring Boot application class with @EnableScheduling

### API Layer
- **NvdbApiClient.kt**: HTTP client for consuming NVDB Uberiket API using Spring WebFlux

### Configuration
- **KafkaStreamsConfig.kt**: Kafka Streams bean configuration
- **NvdbApiProperties.kt**: Configuration properties for NVDB API
- **WebClientConfig.kt**: WebClient bean configuration for HTTP calls

### Controllers
- **NvdbController.kt**: REST API endpoints for triggering data fetching and status checks

### Models
- **NvdbModels.kt**: Data classes for NVDB domain objects and DTOs

### Stream Processing
- **NvdbDataProducer.kt**: Fetches data from NVDB API and produces to Kafka topics
- **NvdbStreamTopology.kt**: Kafka Streams topology for data transformation

### Configuration Files
- **application.yml**: Main application configuration
- **application-test.yml**: Test-specific configuration
- **docker-compose.yml**: Kafka cluster setup (KRaft mode with Kafka UI)

## Kafka Topics

| Topic | Description |
|-------|-------------|
| `nvdb-vegobjekter-raw` | Raw road object data from NVDB API |
| `nvdb-vegobjekter-transformed` | Transformed/enriched road object data |
| `nvdb-fartsgrenser` | Speed limit data (filtered) |
