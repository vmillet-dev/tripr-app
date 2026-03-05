# ✈️ Tripr

> **Modern SaaS Starter** combining the power of **Spring Boot 4 (Kotlin)** and the reactivity of **Angular 21 (Signals)**.

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)](https://github.com/example/tripr/actions)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.10-blue.svg)](https://kotlinlang.org/)
[![Angular](https://img.shields.io/badge/Angular-21-red.svg)](https://angular.dev/)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)

Tripr is a production-ready template designed for scalability, security, and a smooth developer experience. It features a **contract-first approach** using OpenAPI to bridge the gap between a strict Hexagonal backend and a reactive Zoneless frontend.

### 🏗️ Architecture Overview

```mermaid
graph LR
    subgraph ANGULAR["🅰️ Angular Frontend"]
        direction TB
        A1["📦 Core Module\n─────────────\nServices\nGuards\nInterceptors"]
        A2["📦 Feature Modules\n─────────────\nComponents\nPages\nResolvers"]
        A3["📦 Shared Module\n─────────────\nUI Components\nPipes / Directives"]
        A1 --- A2 --- A3
    end

    subgraph OAG["⚙️ OpenAPI Generator"]
        direction TB
        O1["📄 openapi.yaml\n(contract)"]
        O2["🔄 Generated Client\n─────────────\nModels (TS)\nAPI Services (TS)"]
        O1 --> O2
    end

    subgraph SPRINGBOOT["🍃 Spring Boot — Hexagonal Architecture"]
        direction TB

        subgraph APP["📦 application"]
            AP1["🚀 Main Class\n─────────────\nSpringApplication\nGlobal Config\nBeans / Properties"]
        end

        subgraph IN_ADAPTERS["📦 adapters · in"]
            IN1["🌐 REST Controllers\n─────────────\nRequest / Response DTOs\nValidation\nOpenAPI annotations"]
        end

        subgraph DOMAIN["📦 domain"]
            direction TB
            DO1["🧩 Objects\n─────────────\nEntities\nValue Objects\nAggregates"]
            DO2["🔌 Ports · in\n─────────────\nUse Case Interfaces"]
            DO3["🔌 Ports · out\n─────────────\nRepository Interfaces\nNotification Interfaces"]
            DO4["⚙️ Services\n─────────────\nUse Case Implementations\nBusiness Logic"]
            DO2 --> DO4
            DO4 --> DO3
            DO4 --- DO1
        end

        subgraph OUT_ADAPTERS["📦 adapters · out"]
            direction TB
            OUT1["🗄️ Persistence\n─────────────\nJPA Repositories\nEntity Mappers\nDB Config"]
            OUT2["📧 Notification\n─────────────\nEmail Service\nSMTP / Templates"]
        end

        IN1 -->|"appelle"| DO2
        DO3 -->|"implémenté par"| OUT1
        DO3 -->|"implémenté par"| OUT2
        APP -.->|"configure"| IN1
        APP -.->|"configure"| OUT1
        APP -.->|"configure"| OUT2
    end

    A2 -->|"HTTP calls\nvia generated\nAPI services"| OAG
    OAG -->|"REST API\nJSON"| IN1

    style ANGULAR fill:#dd0031,color:#fff,stroke:#a50025
    style OAG fill:#f5a623,color:#000,stroke:#c47d00
    style SPRINGBOOT fill:#6db33f,color:#fff,stroke:#4a7c2a
    style APP fill:#2d5a1b,color:#fff,stroke:#1a3a0f
    style IN_ADAPTERS fill:#1e6e9f,color:#fff,stroke:#0d4a70
    style DOMAIN fill:#4a4a8a,color:#fff,stroke:#2d2d6b
    style OUT_ADAPTERS fill:#1e6e9f,color:#fff,stroke:#0d4a70
```

### 🛠️ Tech Stack

| Component    | Key Technologies                                                                     |
|:-------------|:-------------------------------------------------------------------------------------|
| **Backend**  | Kotlin, Spring Boot 4.+, Spring Security (JWT), Liquibase, MapStruct, Testcontainers |
| **Frontend** | Angular 21, Vite, Vitest, Bootstrap 5, Transloco (i18n), Signals, Zoneless           |
| **Bridge**   | **OpenAPI Generator** (Automatic Model & API Synchronization)                        |
| **DevOps**   | Docker, GitHub Actions, Ansible                                                      |

---

### 🚀 Quick Start

#### 1. Prerequisites

- **Java 25+**
- **Node.js 24+** (npm 11+)
- **Docker & Docker Compose**

#### 2. Run the project

Thanks to **Spring Boot Docker Compose** support, the PostgreSQL database is managed automatically when the backend starts.

```bash
# Terminal 1: Backend
cd backend && ./gradlew bootRun

# Terminal 2: Frontend
cd frontend && npm install && npm run dev
```

### 📁 Monorepo Structure

```text
.
├── api-spec/     # OpenAPI Specifications (API Contract)
├── backend/      # Spring Boot Kotlin API (Hexagonal Structure)
├── devops/       # Docker, Ansible & CI/CD configurations
├── e2e/          # End-to-end tests with Cypress
└── frontend/     # Angular Application (Vite & Standalone)
```

---

- 📖 [**Backend Documentation**](backend/README.md) — *Architecture, API & Tests*
- 📖 [**Frontend Documentation**](frontend/README.md) — *Modern Angular, Tooling & Vite*
