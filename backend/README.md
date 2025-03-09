# Tripr App Backend

Spring Boot backend for the Tripr travel planning application, built with Kotlin and implementing a hexagonal architecture.

## Architecture

The backend follows a hexagonal (ports and adapters) architecture:

```
src/main/kotlin/com/adsearch/
├── application/           # Application layer (use cases)
│   ├── port/              # Inbound ports (interfaces for use cases)
│   └── service/           # Use case implementations
├── domain/                # Domain layer (core business logic)
│   ├── model/             # Domain entities
│   └── port/              # Outbound ports (interfaces for repositories)
└── infrastructure/        # Infrastructure layer (adapters)
    ├── config/            # Configuration classes
    ├── persistence/       # Database adapters
    ├── security/          # Security configuration
    └── web/               # Web controllers and DTOs
```

### Key Features

- **Hexagonal Architecture**: Clean separation of concerns
- **JWT Authentication**: Secure authentication with refresh tokens
- **Password Reset Workflow**: Email-based password reset functionality
- **Database Migrations**: Liquibase for database schema management
- **API Documentation**: Swagger UI for interactive API documentation
- **Monitoring**: Prometheus and Grafana integration

## Development

### Prerequisites

- Java 21
- Docker and Docker Compose

### Running the Application

1. Start the required services:

```bash
docker compose up -d
```

This will start:
- PostgreSQL database
- Mailpit for email testing
- Prometheus for metrics collection
- Grafana for metrics visualization

2. Run the application:

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

The application will be available at http://localhost:8081/api

### Building the Project

```bash
./gradlew clean build
```

### Running Tests

```bash
./gradlew test
```

## Service URLs

| Service | URL | Description |
|---------|-----|-------------|
| API | http://localhost:8081/api | REST API endpoints |
| Swagger UI | http://localhost:8081/swagger-ui.html | API documentation (dev mode only) |
| Prometheus | http://localhost:9090 | Metrics monitoring |
| Grafana | http://localhost:3000 | Dashboards and visualizations (admin/admin) |
| Mailpit | http://localhost:8026 | Email testing interface |
| Actuator | http://localhost:8082/actuator | Spring Boot Actuator endpoints |

## Database

The application uses PostgreSQL with the following default configuration:

- **Host**: localhost
- **Port**: 5432
- **Database**: tripr
- **Username**: postgres
- **Password**: (configured in compose.yaml)

Database migrations are managed with Liquibase and located in `src/main/resources/db/changelog/`.

## Monitoring

### Prometheus

Prometheus is configured to scrape metrics from the Spring Boot Actuator endpoint. The configuration is located in `monitoring/prometheus/prometheus.yml`.

### Grafana

Grafana is preconfigured with Prometheus as a data source. The default credentials are:
- **Username**: admin
- **Password**: admin

### Testing Monitoring Setup

You can test the monitoring setup using the provided script:

```bash
./test-monitoring.sh
```

## API Documentation

Swagger UI is available in development mode at http://localhost:8081/swagger-ui.html
