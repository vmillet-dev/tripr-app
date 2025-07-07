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
- [Key Features](#key-features)
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
- **Test Coverage** - Comprehensive coverage reporting with JaCoCo

## Project Structure

### Multimodule Gradle Structure

The backend is organized as a multimodule Gradle project with clear separation of concerns:

```
backend/
├── build.gradle.kts                    # Root build configuration
├── gradle/
│   └── libs.versions.toml             # Centralized dependency versions
├── bootstrap/                         # Application entry point
│   ├── build.gradle.kts
│   └── src/main/kotlin/com/adsearch/
│       ├── Application.kt             # Spring Boot main class
│       └── config/                    # Application configuration
├── domain/                            # Core business logic
│   ├── build.gradle.kts
│   └── src/main/kotlin/com/adsearch/domain/
│       ├── model/                     # Domain entities and value objects
│       ├── port/                      # Domain interfaces (ports)
│       └── service/                   # Domain services
├── application/                       # Use cases and application services
│   ├── build.gradle.kts
│   └── src/main/kotlin/com/adsearch/application/
│       ├── usecase/                   # Application use cases
│       ├── service/                   # Application services
│       └── port/                      # Application ports
└── infrastructure/                    # External integrations
    ├── build.gradle.kts
    └── src/main/kotlin/com/adsearch/infrastructure/
        ├── adapter/
        │   ├── in/web/                # REST controllers (driving adapters)
        │   └── out/persistence/       # Database adapters (driven adapters)
        ├── config/                    # Infrastructure configuration
        └── security/                  # Security configuration
```

### Hexagonal Architecture Layers

#### Domain Layer (Core)
**Purpose**: Contains pure business logic without external dependencies

**Components:**
- **Entities** - Core business objects with identity
- **Value Objects** - Immutable objects representing concepts
- **Domain Services** - Business logic that doesn't belong to entities
- **Ports** - Interfaces defining contracts with external systems

#### Application Layer
**Purpose**: Orchestrates domain objects to fulfill use cases

**Components:**
- **Use Cases** - Application-specific business rules
- **Application Services** - Coordinate domain objects
- **DTOs** - Data transfer objects for application boundaries
- **Mappers** - Convert between domain and application models

#### Infrastructure Layer
**Purpose**: Implements ports and provides technical capabilities

**Components:**
- **Web Controllers** - REST API endpoints (driving adapters)
- **Repository Implementations** - Database access (driven adapters)
- **Configuration** - Spring configuration and beans
- **Security** - Authentication and authorization

### Package Organization and Naming Conventions

**Package Structure:**
```
com.adsearch
├── domain
│   ├── model.user          # User-related domain objects
│   ├── model.auth          # Authentication domain objects
│   ├── port.out            # Outbound ports (driven)
│   └── service             # Domain services
├── application
│   ├── usecase.user        # User-related use cases
│   ├── usecase.auth        # Authentication use cases
│   ├── port.in             # Inbound ports (driving)
│   └── service             # Application services
└── infrastructure
    ├── adapter.in.web      # REST controllers
    ├── adapter.out.persistence  # Database repositories
    ├── config              # Configuration classes
    └── security            # Security configuration
```

**Naming Conventions:**
- **Classes** - PascalCase (e.g., `UserService`, `AuthController`)
- **Functions** - camelCase (e.g., `findByEmail`, `registerUser`)
- **Constants** - UPPER_SNAKE_CASE (e.g., `MAX_LOGIN_ATTEMPTS`)
- **Packages** - lowercase with dots (e.g., `com.adsearch.domain.model`)

## Development Setup

### Prerequisites

Ensure you have the following tools installed:

| Tool | Version | Purpose | Verification |
|------|---------|---------|--------------|
| **Java** | 24+ | Runtime platform | `java --version` |
| **Docker** | 20.10+ | Database and services | `docker --version` |
| **Docker Compose** | 2.0+ | Multi-container orchestration | `docker compose version` |

**Note**: Gradle wrapper is included (`./gradlew`), so no separate Gradle installation is required.

### Running the Application

#### 1. Start Infrastructure Services

```bash
# Start PostgreSQL and Mailpit
docker compose -f devops/compose-dev.yaml up -d

# Verify services are running
docker compose -f devops/compose-dev.yaml ps
```

**Expected output:**
```
NAME                     IMAGE               STATUS
devops-postgres-1        postgres:17.3       Up 30 seconds
devops-mailpit-1         axllent/mailpit     Up 30 seconds
```

#### 2. Run the Spring Boot Application

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
2024-01-15 10:30:00.000  INFO --- [           main] com.adsearch.Application                 : Application is running on http://localhost:8081
```

#### 3. Verify Application Health

```bash
# Check application health
curl http://localhost:8081/actuator/health

# Expected response
{"status":"UP","groups":["liveness","readiness"]}
```

### Docker Compose Setup for Development Environment

The development environment uses Docker Compose to provide consistent infrastructure services:

**Services Provided:**
- **PostgreSQL Database** - Primary data storage
- **Mailpit** - Email testing and debugging

**Service URLs:**
- **Database**: `localhost:5433` (postgres/P4ssword!)
- **Email UI**: http://localhost:8026

### Profile Management

The application supports multiple Spring profiles for different environments:

#### Development Profile (Default)
```bash
# Explicitly set development profile
./gradlew bootRun --args='--spring.profiles.active=dev'

# Or use environment variable
export SPRING_PROFILES_ACTIVE=dev
./gradlew bootRun
```

**Development Profile Features:**
- **Database**: Local PostgreSQL via Docker Compose
- **Email**: Mailpit for email testing
- **Logging**: Debug level for application packages
- **Security**: Relaxed CORS settings for frontend development

### Database Setup

#### Database Initialization

The application automatically creates and migrates the database schema on startup:

```bash
# Start PostgreSQL
docker compose -f devops/compose-dev.yaml up -d postgres

# Run application (will create schema automatically)
cd backend && ./gradlew bootRun
```

#### Manual Database Operations

**Connect to Database:**
```bash
# Using Docker Compose
docker compose -f devops/compose-dev.yaml exec postgres psql -U postgres -d tripr

# Using local psql client
psql -h localhost -p 5433 -U postgres -d tripr
```

### Configuration

#### Environment Variables

**Required Environment Variables:**

```bash
# Database Configuration
DB_HOST=localhost                    # Database host
DB_PORT=5433                        # Database port
DB_NAME=tripr                       # Database name
DB_USERNAME=postgres                # Database username
DB_PASSWORD=P4ssword!               # Database password

# JWT Configuration
JWT_SECRET=your-256-bit-secret-key-minimum-32-characters  # JWT signing secret
BASE_URL=http://localhost:8081      # Application base URL

# Email Configuration (optional for development)
MAIL_HOST=localhost                 # SMTP server host
MAIL_PORT=1026                      # SMTP server port
```

## Key Features

### MapStruct for Object Mapping

**Purpose**: Efficient and type-safe mapping between domain objects and DTOs

**Configuration** (`build.gradle.kts`):
```kotlin
dependencies {
    implementation("org.mapstruct:mapstruct:1.6.3")
    kapt("org.mapstruct:mapstruct-processor:1.6.3")
}
```

**Benefits:**
- **Compile-time Safety** - Mapping errors caught at build time
- **Performance** - No reflection, generates efficient code
- **Maintainability** - Clear mapping definitions
- **IDE Support** - Full IntelliJ IDEA integration

### Integration Testing Setup

**Testcontainers Integration:**
```kotlin
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class UserControllerIntegrationTest {
    
    companion object {
        @Container
        @JvmStatic
        val postgres = PostgreSQLContainer<Nothing>("postgres:17.3").apply {
            withDatabaseName("tripr_test")
            withUsername("test")
            withPassword("test")
        }
    }
    
    @Autowired
    lateinit var testRestTemplate: TestRestTemplate
    
    @Test
    fun `should register new user successfully`() {
        // Given
        val request = RegisterRequest(
            email = "test@example.com",
            password = "SecurePassword123!",
            firstName = "John",
            lastName = "Doe"
        )
        
        // When
        val response = testRestTemplate.postForEntity(
            "/api/auth/register",
            request,
            UserResponse::class.java
        )
        
        // Then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body?.email).isEqualTo("test@example.com")
    }
}
```

### API Documentation

**Swagger/OpenAPI Integration:**

**Access Documentation:**
- **Development**: http://localhost:8081/swagger-ui.html
- **API Spec**: http://localhost:8081/v3/api-docs

### Security Configuration Details

**JWT Security Configuration:**
```kotlin
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {
    
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            .sessionManagement { 
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) 
            }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers("/actuator/health").permitAll()
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }
}
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
    
    fun generateAccessToken(user: User): String {
        val claims = mapOf(
            "sub" to user.id.value,
            "email" to user.email.value,
            "roles" to user.roles.map { it.name }
        )
        
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + accessTokenExpiration))
            .signWith(SignatureAlgorithm.HS256, secret)
            .compact()
    }
}
```

## Database

### Database Technology and ORM

**PostgreSQL 17.3:**
- **ACID Compliance** - Ensures data consistency and reliability
- **Advanced Features** - JSON support, full-text search, advanced indexing
- **Performance** - Optimized for concurrent read/write operations
- **Scalability** - Supports horizontal scaling with read replicas

**Spring Data JPA with Hibernate:**
- **Object-Relational Mapping** - Seamless Java/Kotlin object persistence
- **Query Generation** - Automatic query generation from method names
- **Custom Queries** - Support for JPQL and native SQL queries
- **Transaction Management** - Declarative transaction handling

## Testing

### Testing Strategy

The backend implements a comprehensive testing strategy covering all architectural layers:

#### Unit Tests
```bash
# Run all tests
./gradlew test

# Run tests with coverage report
./gradlew test jacocoTestReport

# Run specific test class
./gradlew test --tests "UserServiceTest"
```

#### Integration Tests
```bash
# Run only integration tests
./gradlew test --tests "*IntegrationTest"
```

#### ArchUnit Tests
```bash
# Run only architecture tests
./gradlew test --tests "*ArchTest"
```

### Test Coverage and Reporting

**Generate Coverage Report:**
```bash
# Generate coverage report
./gradlew jacocoTestReport

# View HTML report
open backend/build/reports/jacoco/test/html/index.html
```

**Coverage Targets:**
- **Overall Coverage**: 80% minimum
- **Domain Layer**: 90% minimum (critical business logic)
- **Application Layer**: 85% minimum
- **Infrastructure Layer**: 70% minimum (external dependencies)

## API Documentation

### Swagger/OpenAPI Integration

The backend provides comprehensive API documentation using OpenAPI 3.0 specification:

**Access Points:**
- **Interactive UI**: http://localhost:8081/swagger-ui.html
- **JSON Spec**: http://localhost:8081/v3/api-docs
- **YAML Spec**: http://localhost:8081/v3/api-docs.yaml

### API Endpoints

#### Authentication Endpoints

**Register User:**
```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePassword123!",
  "firstName": "John",
  "lastName": "Doe"
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
  base-url: ${BASE_URL:http://localhost:8081}
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

### Getting Help

**Development Resources:**
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Kotlin Documentation](https://kotlinlang.org/docs/)
- [Gradle Documentation](https://docs.gradle.org/)

---

**Built with ❤️ using Spring Boot and Kotlin. Happy coding! 🚀**
