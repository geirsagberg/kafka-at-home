# Suggested Commands

## Build and Run

### Build the project
```bash
./gradlew build
```

### Run tests
```bash
./gradlew test
```

### Run the application locally
```bash
./gradlew bootRun
```

### Clean build
```bash
./gradlew clean build
```

## Docker and Infrastructure

### Start local Kafka infrastructure
```bash
docker compose up -d
```

### Stop local Kafka infrastructure
```bash
docker compose down
```

### View Kafka logs
```bash
docker compose logs -f kafka
```

### Access Kafka UI
Open browser at: http://localhost:8090

## Kafka Topic Management

Topics are auto-created by docker-compose on startup:
- `nvdb-vegobjekter-raw` (3 partitions)
- `nvdb-vegobjekter-transformed` (3 partitions)
- `nvdb-fartsgrenser` (3 partitions)

## Application Endpoints

### Health check
```bash
curl http://localhost:8080/actuator/health
```

### Get NVDB status
```bash
curl http://localhost:8080/api/nvdb/status
```

### Fetch speed limits (100 records)
```bash
curl -X POST "http://localhost:8080/api/nvdb/fetch/speedlimits?count=100"
```

### Fetch road objects by type
```bash
curl -X POST "http://localhost:8080/api/nvdb/fetch/vegobjekter/583?count=100"
```

## Git Commands (macOS/Darwin)

### Standard git operations
```bash
git status
git add .
git commit -m "message"
git push
git pull
```

### View recent commits
```bash
git log --oneline -10
```

## System Utilities (Darwin/macOS)

- `ls` - List directory contents
- `cd` - Change directory
- `grep` - Search text patterns
- `find` - Find files
- `cat` - Display file contents
- `pwd` - Print working directory

Note: macOS uses BSD versions of these utilities, which may have slightly different options than GNU versions.
