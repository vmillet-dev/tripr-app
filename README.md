# Tripr App

A modern travel planning application with a Spring Boot backend and Angular frontend.

## Overview

Tripr App is a comprehensive travel planning platform built with a robust backend written in Kotlin using Spring Boot and a responsive frontend developed with Angular. The application implements a hexagonal architecture for clean separation of concerns and follows best practices for security, testing, and monitoring.

## Components

### Backend
- **Spring Boot** application using Kotlin and Gradle
- **Hexagonal Architecture** with domain, application, and infrastructure layers
- **JWT Authentication** with refresh tokens
- **PostgreSQL Database** with Liquibase migrations
- **Monitoring** with Prometheus and Grafana
- **Email Integration** with Mailpit for testing

### Frontend
- **Angular 19** application with standalone components
- **Responsive UI** for desktop and mobile devices
- **Internationalization** with Transloco
- **Authentication Workflows** including login, registration, and password reset

### E2E Testing
- **Cypress** for end-to-end testing
- **Comprehensive Test Coverage** for critical user flows
- **CI/CD Integration** with GitHub Actions

## Quick Start

### Prerequisites
- Java 21
- Node.js 18+
- Docker and Docker Compose

### Running the Application

1. Start the required services:
```bash
cd backend
docker compose up -d
```

2. Start the backend:
```bash
cd backend
./gradlew bootRun --args='--spring.profiles.active=dev'
```

3. Start the frontend:
```bash
cd frontend
npm install
ng serve
```

4. Access the application at http://localhost:4200

## Service URLs

| Service | URL | Description |
|---------|-----|-------------|
| Frontend | http://localhost:4200 | Angular application |
| Backend API | http://localhost:8081/api | REST API endpoints |
| Swagger UI | http://localhost:8081/swagger-ui.html | API documentation (dev mode only) |
| Prometheus | http://localhost:9090 | Metrics monitoring |
| Grafana | http://localhost:3000 | Dashboards and visualizations (admin/admin) |
| Mailpit | http://localhost:8026 | Email testing interface |
| Actuator | http://localhost:8082/actuator | Spring Boot Actuator endpoints |

## Documentation

- [Backend Documentation](./backend/README.md)
- [Frontend Documentation](./frontend/README.md)
- [E2E Testing Documentation](./e2e/README.md)

## License

This project is licensed under the MIT License - see the LICENSE file for details.
