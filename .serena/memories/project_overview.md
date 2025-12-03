# Project Overview

**kafka-at-home** is a Spring Boot 3 application using Kotlin and Kafka Streams to consume and transform road data from the NVDB (Norwegian Road Database) Uberiket API.

## Purpose

The application:
- **Consumes** road data from the [NVDB Uberiket API](https://nvdbapiles.atlas.vegvesen.no/uberiket/api/v1/)
- **Transforms** the data using Kafka Streams
- **Produces** enriched data to output Kafka topics

## Supported Data Types

The application supports fetching various road object types from NVDB:
- **Fartsgrense (105)**: Speed limits
- **Vegbredde (583)**: Road width
- **Kjørefelt (616)**: Driving lanes
- **Funksjonsklasse (821)**: Functional road class

## Key Features

- REST API for triggering data fetching from NVDB
- Kafka Streams topology for data transformation
- Scheduled/manual data ingestion from Norwegian road database
- Spring WebFlux for reactive HTTP client
- Docker Compose setup for local Kafka cluster with Kafka UI
