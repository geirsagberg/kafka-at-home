# Development Environment

## System Information

- **Operating System**: Darwin (macOS)
- **Platform**: macOS 24.6.0
- **Java Version**: 17 (JVM toolchain)
- **Kotlin Version**: 2.2.20

## IDE Configuration

### IntelliJ IDEA
- Project uses IntelliJ IDEA (`.idea/` directory present)
- Kotlin plugin should be installed
- Check for compilation errors in IDE before declaring task complete

### Kotlin LSP (if using Opencode)
- Command: `kotlin-lsp --stdio`
- Extensions: `.kt`, `.kts`
- Always use LSP to check for compilation errors
- Don't rely solely on Gradle compilation

## Git Configuration

- **Current Branch**: main
- **Main Branch**: main (use for pull requests)
- **Repository**: Git-initialized at `/Users/geirsagberg/Projects/kafka-at-home`

## Docker Setup

### Kafka Infrastructure
Docker Compose provides:
- **Kafka Broker**: Apache Kafka 4.1.1 (KRaft mode, no ZooKeeper)
  - Exposed on port 9092 (PLAINTEXT_HOST)
  - Internal port 29092 (PLAINTEXT)
  - Controller port 9093
- **Kafka UI**: provectuslabs/kafka-ui
  - Accessible at http://localhost:8090
  - Provides topic browsing, message viewing, and cluster monitoring
- **Kafka Init**: Initializes topics on startup

### Docker Commands
```bash
# Start Kafka cluster
docker compose up -d

# Stop Kafka cluster
docker compose down

# View logs
docker compose logs -f kafka
```

## Environment Variables

Default values (can be overridden):
- `KAFKA_BOOTSTRAP_SERVERS`: localhost:9092
- `NVDB_API_BASE_URL`: https://nvdbapiles.atlas.vegvesen.no/uberiket/api/v1/
- `NVDB_PRODUCER_ENABLED`: false
- `NVDB_PRODUCER_BATCH_SIZE`: 100
- `NVDB_PRODUCER_INTERVAL_MS`: 3600000 (1 hour)
- `SERVER_PORT`: 8080

## Gradle Configuration

- **Build Tool**: Gradle (Kotlin DSL)
- **Wrapper Version**: Latest (gradle-wrapper.properties)
- **Plugins**:
  - `org.springframework.boot` (4.0.0)
  - `io.spring.dependency-management` (1.1.7)
  - `kotlin("jvm")` (2.2.20)
  - `kotlin("plugin.spring")` (2.2.20)

## Application Ports

- **Application Server**: 8080
- **Kafka Broker**: 9092
- **Kafka UI**: 8090
- **Management Endpoints**: 8080/actuator/*

## Testing Configuration

- Tests use `application-test.yml` for configuration
- Embedded Kafka broker for integration tests
- JUnit 5 platform
