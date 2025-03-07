# Tripr App

A Spring Boot application using Gradle and Kotlin that implements a hexagonal architecture for searching ads across multiple external APIs.

## Features

- **Hexagonal Architecture**: Clean separation of concerns with domain, application, and infrastructure layers
- **JWT Authentication**: Secure authentication with refresh tokens stored in HTTP-only cookies
- **Password Reset Workflow**: Complete password reset functionality with email notifications
- **External API Integration**: Adapter pattern for easy integration with multiple external ad sources
- **Swagger UI Documentation**: API documentation available in development mode
- **PostgreSQL Database**: Persistent storage with Liquibase for database migrations
- **Comprehensive Testing**: Unit and integration tests with TestContainers

## Project Structure

```
backend/
├── gradle/                  # Gradle configuration
├── src/
│   ├── main/
│   │   ├── kotlin/          # Kotlin source code
│   │   │   └── com/adsearch/
│   │   │       ├── application/    # Application layer (use cases)
│   │   │       ├── domain/         # Domain layer (core business logic)
│   │   │       └── infrastructure/ # Infrastructure layer (adapters)
│   │   └── resources/       # Application resources
│   │       ├── db/          # Database migrations
│   │       └── templates/   # Email templates
│   └── test/                # Test source code
└── build.gradle.kts         # Gradle build configuration
```

## Getting Started

### Prerequisites

- Java 21
- Docker and Docker Compose

### Running the Application

1. Start the required services (PostgreSQL and Mailpit):

```bash
docker-compose up -d
```

2. Run the application:

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

3. Access the application:
   - API: http://localhost:8080/api
   - Swagger UI: http://localhost:8080/swagger-ui.html (dev mode only)
   - Mailpit UI: http://localhost:8025 (for email testing)

## Development

### Building the Project

```bash
./gradlew clean build
```

### Running Tests

```bash
./gradlew test
```

### API Documentation

The API documentation is available via Swagger UI in development mode at:
http://localhost:8080/swagger-ui.html

## License

This project is licensed under the MIT License - see the LICENSE file for details.
