# NVDB Domain Knowledge

## About NVDB

**NVDB** (Norwegian Road Database / Nasjonal Vegdatabank) is the Norwegian national road database maintained by the Norwegian Public Roads Administration (Statens vegvesen).

## NVDB Uberiket API

- **Base URL**: https://nvdbapiles.atlas.vegvesen.no/uberiket/api/v1/
- **Purpose**: Public API for accessing Norwegian road data
- **Data Format**: JSON
- **Access**: Reactive HTTP client using Spring WebFlux

## Supported Road Object Types

| Type ID | Norwegian Name | English Name | Description |
|---------|---------------|--------------|-------------|
| 105 | Fartsgrense | Speed Limits | Speed limit information for road segments |
| 583 | Vegbredde | Road Width | Width measurements of road segments |
| 616 | Kjørefelt | Driving Lanes | Information about driving lanes |
| 821 | Funksjonsklasse | Functional Road Class | Classification of roads by function |

## Data Model

### Vegobjekt (Road Object)
Core data structure from NVDB API containing:
- Object type ID
- Spatial location/geometry
- Attributes specific to the object type
- Metadata (version, last modified, etc.)

## Application Integration

### API Client
- **NvdbApiClient**: Reactive client for fetching road objects
- Supports fetching by type ID with pagination
- Uses WebClient for non-blocking HTTP calls

### Data Flow
1. **Ingest**: Fetch road objects from NVDB API
2. **Produce**: Send raw data to `nvdb-vegobjekter-raw` Kafka topic
3. **Transform**: Process data through Kafka Streams topology
4. **Output**: Write transformed data to topic-specific outputs
   - General: `nvdb-vegobjekter-transformed`
   - Speed limits: `nvdb-fartsgrenser`

### Endpoints
- `POST /api/nvdb/fetch/speedlimits?count=100` - Fetch speed limits
- `POST /api/nvdb/fetch/vegobjekter/{typeId}?count=100` - Fetch by type ID
- `GET /api/nvdb/status` - Get available types and status

## Related Resources

- [NVDB Uberiket API Documentation](https://nvdbapiles.atlas.vegvesen.no/uberiket/api/v1/)
- [nvdb-tnits-public](https://github.com/nvdb-vegdata/nvdb-tnits-public) - Reference TN-ITS export project
- [nvdb-api-client](https://github.com/nvdb-vegdata/nvdb-api-client) - Java client library
