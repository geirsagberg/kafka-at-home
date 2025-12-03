# Code Style and Conventions

## General Kotlin Conventions

- Use Kotlin idioms and conventions
- Follow Spring Boot best practices
- Use constructor injection for dependencies
- Prefer data classes for DTOs and domain models

## Code Organization

### Package Structure
```
no.geirsagberg.kafkaathome/
├── api/          - External API clients
├── config/       - Configuration classes
├── controller/   - REST API endpoints
├── model/        - Data models and DTOs
└── stream/       - Kafka Streams processing
```

## Naming Conventions

- **Classes**: PascalCase (e.g., `NvdbController`, `KafkaStreamsConfig`)
- **Functions/Methods**: camelCase (e.g., `fetchSpeedLimits`, `getStatus`)
- **Properties**: camelCase (e.g., `nvdbApiClient`, `batchSize`)
- **Constants**: UPPER_SNAKE_CASE (for companion objects)

## Documentation

- Use KDoc comments for public APIs
- Document REST endpoints with description of parameters
- Document class-level purpose with KDoc

Example:
```kotlin
/**
 * REST controller for managing NVDB data ingestion and Kafka operations.
 */
@RestController
@RequestMapping("/api/nvdb")
class NvdbController(...)
```

## Logging

- Use SLF4J for logging via `LoggerFactory`
- Log level hierarchy: DEBUG for application, WARN for Kafka, INFO for Spring Kafka

## Dependency Injection

- Use constructor injection (preferred in Spring)
- Mark optional dependencies with `@Autowired(required = false)` if needed
- Example:
```kotlin
class NvdbController(
    @Autowired(required = false) private val nvdbDataProducer: NvdbDataProducer?,
    private val nvdbApiClient: NvdbApiClient
)
```

## Configuration

- Use `@ConfigurationProperties` for externalized configuration
- Support environment variable overrides with sensible defaults
- Example: `${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}`

## Testing Practices

- Use JUnit 5 with Spring Boot Test
- Test Kafka Streams topologies using `TopologyTestDriver`
- Use embedded Kafka for integration tests
- Test configuration in `application-test.yml`
- Follow Arrange-Act-Assert pattern in tests
