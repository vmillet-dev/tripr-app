# Tripr App Backend

[![Build Status](https://github.com/vmillet-dev/tripr-app/workflows/Build%20and%20Test/badge.svg)](https://github.com/vmillet-dev/tripr-app/actions)
[![Coverage](https://img.shields.io/badge/Coverage-85%25-green.svg)](https://github.com/vmillet-dev/tripr-app)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.0-blue.svg)](https://kotlinlang.org/)

Spring Boot backend application built with Kotlin, implementing hexagonal architecture and JWT authentication with refresh tokens.

## Table of Contents

- [Technical Details](#technical-details)
- [Project Structure](#project-structure)
- [Development Setup](#development-setup)
- [Authentication](#authentication)
- [Database](#database)
- [Testing](#testing)
- [API Documentation](#api-documentation)
- [Configuration](#configuration)
- [Troubleshooting](#troubleshooting)

## Technical Details

### Framework
- **Spring Boot 3.5.0** - Enterprise Java framework with auto-configuration
- **Gradle 8.14** - Build automation with Kotlin DSL
- **Kotlin 2.2.0** - Modern JVM language with null safety and conciseness
- **Java 24** - Runtime platform with latest performance improvements

### Architecture
The backend implements **Hexagonal Architecture** (ports and adapters pattern) ensuring:
- **Clean separation of concerns** between business logic and external dependencies
- **Testability** through dependency inversion and interface-based design
- **Flexibility** to swap implementations without affecting core business logic
- **Maintainability** with clear architectural boundaries

### Authentication
**JWT with Refresh Token Implementation:**
- **Access Tokens** - Short-lived (1 hour) for API authentication
- **Refresh Tokens** - Long-lived (7 days) stored as HTTP-only cookies
- **Automatic Token Refresh** - Seamless user experience without re-login
- **Secure Token Storage** - Prevents XSS attacks through HTTP-only cookies

### Database
- **PostgreSQL 17.3** - Robust relational database with ACID compliance
- **Spring Data JPA** - Object-relational mapping with Hibernate
- **Database Migrations** - Schema versioning and evolution management
- **Connection Pooling** - Optimized database connection management

### Testing
- **Unit Tests** - Business logic validation with JUnit 5 and MockK
- **Integration Tests** - Full application context testing with Testcontainers
- **ArchUnit Tests** - Architecture rule enforcement and validation

## Project Structure

### Multimodule Gradle Structure

The backend is organized as a multimodule Gradle project with clear separation of concerns:

```
backend/
├── build.gradle.kts                    # Root build configuration
├── gradle/
│   └── libs.versions.toml              # Centralized dependency versions
├── bootstrap/                          # Application entry point
│   ├── build.gradle.kts
│   └── src/main/kotlin/com/adsearch/
│       ├── Application.kt              # Spring Boot main class
├── domain/                             # Core business logic
│   ├── build.gradle.kts
│   └── src/main/kotlin/com/adsearch/domain/
│       ├── model/                      # Domain entities and value objects
│       ├── exception/                  # Business exception
│       ├── port/                       # Domain interfaces (ports)
│       └── command/                    # Command objects from 
│       └── auth/                       # Specific objects that return auth token to controller
├── application/                        # Use cases and application services
│   ├── build.gradle.kts
│   └── src/main/kotlin/com/adsearch/application/
│       ├── /                           # Use cases interface
│       ├── service/                    # Implementation of use case, 
│       └── annotation/                 # Custom annotation to load theses implementations as beans without spring annotation (framework free)
└── infrastructure/                     # External integrations
    ├── build.gradle.kts
    └── src/main/kotlin/com/adsearch/infrastructure/
        ├── adapter/
        │   ├── in/web/                 # REST controllers (driving adapters)
        │   └── out/
        │        └── persistence/       # Database adapters (driven adapters)
        │        └── email/             # Email adapters (driven adapters)
        ├── config/                     # Infrastructure configuration
        └── service/                    # Services classes that implement framework logic
```

### Hexagonal Architecture Layers

#### Domain Layer (Core)
**Purpose**: Contains pure business logic without external dependencies

**Components:**

- **Dom objects** - Core business objects with identity
- **Value Objects** - Immutable objects representing concepts
- **Ports** - Interfaces defining contracts with external systems

#### Application Layer
**Purpose**: Orchestrates domain objects to fulfill use cases

**Components:**
- **Use Cases** - Application-specific business rules

#### Infrastructure Layer
**Purpose**: Implements ports and provides technical capabilities

**Components:**
- **Web Controllers** - REST API endpoints (driving adapters)
- **Repository Implementations** - Database access (driven adapters)
- **Configuration** - Spring configuration and beans
- **Security** - Authentication and authorization

## Development Setup

### Prerequisites

Ensure you have the following tools installed:

| Tool               | Version | Purpose                       | Verification             |
|--------------------|---------|-------------------------------|--------------------------|
| **Java**           | 24+     | Runtime platform              | `java --version`         |
| **Docker**         | 20.10+  | Database and services         | `docker --version`       |
| **Docker Compose** | 2.0+    | Multi-container orchestration | `docker compose version` |

**Note**: Gradle wrapper is included (`./gradlew`), so no separate Gradle installation is required.

### Running the Application

#### 1. Run the Spring Boot Application

```bash
cd backend

# Build and run with development profile (default)
./gradlew bootRun
```

**Expected output:**
```
> Task :bootstrap:bootRun

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.5.0)

2024-01-15 10:30:00.000  INFO --- [           main] com.adsearch.Application                 : Started Application in 3.456 seconds
2024-01-15 10:30:00.000  INFO --- [           main] com.adsearch.Application                 : Application is running on http://localhost:8080
```

### Docker Compose Setup for Development Environment

The development environment uses Docker Compose to provide consistent infrastructure services:

**Services Provided:**
- **PostgreSQL Database** - Primary data storage
- **Mailpit** - Email testing and debugging

**Service URLs:**
- **Database**: `localhost:5433` (postgres/P4ssword!)
- **Email UI**: http://localhost:8026

Every mail sent will be available in this UI interface like when resetting a pass word. API version
is also available to get messages in json format (check MailPit doc).

### Profile Management

By default, the dev profile is used when running bootRun command, but you can also do that:

#### Development Profile (Default)
```bash
# Explicitly set development profile
./gradlew bootRun --args='--spring.profiles.active=dev'

# Or use environment variable
export SPRING_PROFILES_ACTIVE=dev
./gradlew bootRun
```

### Database Setup

#### Database Initialization

The application automatically creates and migrates the database schema on startup thanks to liquibase. If you need to do manual ops,
you can do it like this:

**Connect to Database:**
```bash
# Using Docker Compose
docker compose -f devops/compose-dev.yaml exec postgres psql -U postgres -d tripr

# Using local psql client
psql -h localhost -p 5433 -U postgres -d tripr
```

## Authentication

### JWT Implementation Details

The authentication system implements a secure JWT-based approach with refresh tokens:

#### Token Types

**Access Token:**
- **Lifetime**: 1 hour (configurable)
- **Storage**: Memory (JavaScript variable)
- **Purpose**: API authentication
- **Claims**: User ID, email, roles, expiration

**Refresh Token:**
- **Lifetime**: 7 days (configurable)
- **Storage**: HTTP-only cookie
- **Purpose**: Access token renewal
- **Security**: Prevents XSS attacks

#### Token Generation

```kotlin
@Service
class JwtTokenService(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.expiration}") private val accessTokenExpiration: Long,
    @Value("\${jwt.refresh-expiration}") private val refreshTokenExpiration: Long
) {

    private val algorithm: Algorithm = Algorithm.HMAC256(secret)
    private val verifier: JWTVerifier = JWT.require(algorithm).withIssuer(issuer).build()

    fun createAccessToken(user: UserDom): String = JWT.create()
        .withSubject(user.username)
        .withArrayClaim("roles", user.roles.toTypedArray())
        .withIssuedAt(Instant.now())
        .withExpiresAt(Instant.now().plusSeconds(jwtExpiration))
        .withIssuer(issuer)
        .sign(algorithm)
    }
}
```

## Testing

### Testing Strategy

The backend implements a comprehensive testing strategy covering all architectural layers:

#### Unit Tests
```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "UserServiceTest"
```

#### Integration Tests
```bash
# Run only integration tests
./gradlew test --tests "*IT"
```

#### ArchUnit Tests
```bash
# Run only architecture tests
./gradlew test --tests "**.architecture.*""
```

## API Documentation

### Swagger/OpenAPI Integration

The backend provides comprehensive API documentation using OpenAPI 3.0 specification:

**Access Points:**

- **Interactive UI**: http://localhost:8080/swagger-ui.html
- **JSON Spec**: http://localhost:8080/v3/api-docs
- **YAML Spec**: http://localhost:8080/v3/api-docs.yaml

### API Endpoints

#### Authentication Endpoints

**Register User:**
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "John_doe",
  "email": "user@example.com",
  "password": "SecurePassword123!"
}
```

**Login User:**
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePassword123!"
}
```

**Refresh Token:**
```http
POST /api/auth/refresh
Cookie: refreshToken=550e8400-e29b-41d4-a716-446655440000
```

Check the swagger to see others endpoints

## Configuration

**Main Configuration** (`bootstrap/src/main/resources/application.yml`):
```yaml
spring:
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}
  
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5433}/${DB_NAME:tripr}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:P4ssword!}
  
  jpa:
    hibernate:
      ddl-auto: ${DDL_AUTO:update}
    show-sql: ${SHOW_SQL:false}

jwt:
  secret: ${JWT_SECRET:development-secret-key-not-for-production-use-only}
  expiration: ${JWT_EXPIRATION:3600000}        # 1 hour in milliseconds
  refresh-expiration: ${JWT_REFRESH_EXPIRATION:604800000}  # 7 days in milliseconds

app:
  base-url: ${BASE_URL:http://localhost:8080}
```

## Troubleshooting

### Common Setup Issues

#### Database Connection Problems

**Problem**: Backend fails to connect to PostgreSQL

**Solution**:
```bash
# Check if PostgreSQL container is running
docker compose -f devops/compose-dev.yaml ps

# Verify database connectivity
docker compose -f devops/compose-dev.yaml exec postgres psql -U postgres -d tripr -c "SELECT 1;"

# Check backend logs for connection errors
cd backend && ./gradlew bootRun --debug
```

#### Java/Gradle Issues

**Problem**: Backend build fails with Java version errors

**Solution**:
```bash
# Check Java version
java --version  # Should be 24+

# Use JAVA_HOME if needed
export JAVA_HOME=/path/to/java24
cd backend && ./gradlew bootRun

# Clean and rebuild
./gradlew clean build
```

#### Performance Issues

**Problem**: Backend takes too long to start

**Solution**:
```bash
# Check available memory
free -h

# Increase JVM heap size
export JAVA_OPTS="-Xmx2g -Xms1g"
cd backend && ./gradlew bootRun

# Use development profile for faster startup
export SPRING_PROFILES_ACTIVE=dev
```

---

**Built with ❤️ using Spring Boot and Kotlin.**
