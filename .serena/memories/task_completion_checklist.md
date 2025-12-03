# Task Completion Checklist

When completing a task in this project, perform the following checks:

## 1. Code Quality

- [ ] Code follows Kotlin conventions and idioms
- [ ] Constructor injection is used for dependencies
- [ ] Public APIs have KDoc documentation
- [ ] Logging uses SLF4J

## 2. Build and Compilation

- [ ] Run `./gradlew build` to ensure project compiles successfully
- [ ] Check for any Kotlin compilation errors
- [ ] Verify IntelliJ IDEA shows no compilation errors (LSP check)

## 3. Testing

- [ ] Run `./gradlew test` to execute all tests
- [ ] All tests pass successfully
- [ ] New functionality has appropriate test coverage
- [ ] Tests follow Arrange-Act-Assert pattern
- [ ] Kafka Streams tests use TopologyTestDriver when applicable

## 4. Formatting and Style

- [ ] Code follows project style conventions
- [ ] No unnecessary comments (code should be self-documenting)
- [ ] Imports are organized properly

## 5. Configuration

- [ ] Environment variables have sensible defaults
- [ ] Configuration is externalized where appropriate
- [ ] Changes to application.yml are reflected in application-test.yml if needed

## 6. Runtime Verification (if applicable)

- [ ] Start Kafka: `docker compose up -d`
- [ ] Run application: `./gradlew bootRun`
- [ ] Test relevant endpoints manually if REST API changes were made
- [ ] Check Kafka UI at http://localhost:8090 if Kafka changes were made

## 7. Documentation

- [ ] Update README.md if public API or configuration changes
- [ ] Update .github/copilot-instructions.md if significant architectural changes

## Notes

- If Kotlin LSP is configured via opencode.json, always check for compilation errors using the LSP
- Never use wildcard (*) in `--tests` parameter
- If tests fail repeatedly without clear cause, report the issue rather than deleting tests
- IntelliJ might be out of sync occasionally - don't spend forever fixing it, but make note of it
