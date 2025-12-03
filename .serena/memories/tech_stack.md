# Technology Stack

## Core Technologies

- **Spring Boot**: 4.0.0
- **Kotlin**: 2.2.20
- **Java**: JVM 17 (toolchain)
- **Gradle**: Kotlin DSL build system

## Key Dependencies

### Spring Framework
- `spring-boot-starter-web` - Web MVC framework
- `spring-boot-starter-webflux` - Reactive HTTP client for API calls
- `spring-kafka` - Spring integration for Kafka

### Kafka
- `kafka-streams` - Stream processing framework
- `spring-kafka` - Spring Kafka support

### Kotlin & Jackson
- `kotlin-reflect` - Kotlin reflection
- `jackson-module-kotlin` - JSON serialization/deserialization
- `reactor-kotlin-extensions` - Kotlin extensions for Project Reactor
- `kotlinx-coroutines-reactor` - Coroutines support for Reactor

### Testing
- `spring-boot-starter-test` - Spring Boot testing utilities
- `spring-kafka-test` - Embedded Kafka for testing
- `kafka-streams-test-utils` - TopologyTestDriver for Kafka Streams testing
- JUnit 5 (JUnit Platform)

## Infrastructure

### Local Development
- **Apache Kafka**: 4.1.1 (KRaft mode, no ZooKeeper)
- **Kafka UI**: provectuslabs/kafka-ui (accessible on port 8090)
- **Docker Compose**: For local Kafka infrastructure

### Kafka Configuration
- 3 partitions per topic
- Replication factor: 1 (for local development)
- Auto-topic creation enabled
