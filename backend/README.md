# 🏗️ Tripr API — Backend

Robust API built with **Spring Boot 4** and **Kotlin**, using a strict **Hexagonal Architecture** to ensure domain isolation and testability.

The project follows a **Contract-First** approach using **OpenAPI**. The server-side code is generated from the OpenAPI spec, ensuring a perfect sync between documentation and implementation.

🔗 **Swagger UI** (dev mode): [http://localhost:8080/api/swagger-ui](http://localhost:8080/api/swagger-ui)

---

### 🏛️ Hexagonal Architecture

The project follows the **Ports & Adapters** pattern to decouple business logic from technical details:

- **`domain/`**: **The Core**.
    - `model/`: Domain entities and value objects.
    - `port/`: Inbound (In) and Outbound (Out) interfaces.
    - `service/`: Business orchestration. Services implement *In-Ports* and use *Out-Ports* to execute business logic.
- **`infrastructure/`**: **The Adapters**.
    - Concrete implementations of *Out-Ports* (Persistence with JPA/PostgreSQL, Security with JWT, Email with SMTP).
    - Inbound adapters such as REST Controllers.
- **`application/`**: **The Shell**.
    - Entry point of the application (Main class).
    - Global configurations (Spring, Security, Docker Compose).
    - Integration tests and architectural validation (**ArchUnit**).

---

### ⚙️ Configuration & Environment

| Variable     | Description                    | Dev Value                      |
|:-------------|:-------------------------------|:-------------------------------|
| `DB_HOST`    | PostgreSQL Host                | `localhost`                    |
| `DB_PORT`    | PostgreSQL Port                | `5433`                         |
| `DB_NAME`    | Database Name                  | `tripr`                        |
| `JWT_SECRET` | JWT Signature Secret           | `very-secret-32-characters...` |
| `BASE_URL`   | Frontend URL (for email links) | `http://localhost:4200`        |

### 🚀 Local Execution

The backend automatically manages its infrastructure dependencies (PostgreSQL, Maildev) via `devops/compose-dev.yaml` using Spring Boot's Docker Compose support.

```bash
./gradlew bootRun
```

*Note: Docker Desktop must be running.*

### 🧪 Tests & Quality

The project enforces a **80%** test coverage (via Kover).

```bash
./gradlew test         # Run all tests (Unit, Integration, ArchUnit)
./gradlew koverHtmlReport # Generate coverage report (build/reports/kover)
```

### 🛠️ Troubleshooting

- **Port Conflict**: If `8080` is already in use, change `server.port` in `application-dev.yml`.
- **Docker Error**: Ensure Docker Desktop is started before running `bootRun`.
- **Liquibase Lock**: If a crash occurs, the `databasechangeloglock` table might stay locked. Manually delete the lock in the DB.
