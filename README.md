# ✈️ Tripr

> **Modern SaaS Starter** combining the power of **Spring Boot 4 (Kotlin)** and the reactivity of **Angular 21 (Signals)**.

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)](https://github.com/example/tripr/actions)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.10-blue.svg)](https://kotlinlang.org/)
[![Angular](https://img.shields.io/badge/Angular-21-red.svg)](https://angular.dev/)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)

Tripr is a production-ready template designed for scalability, security, and a smooth developer experience. It features a **contract-first approach** using OpenAPI to bridge the gap between a strict Hexagonal backend and a reactive Zoneless frontend.

### 🏗️ Architecture Overview

```mermaid
graph TB
    subgraph Client ["Frontend (Angular 21)"]
        UI[Signals & Zoneless UI]
        API_C[Generated API Client]
    end

    subgraph Server ["Backend (Spring Boot 4)"]
        subgraph Hex ["Hexagonal Architecture"]
            APP[Application Shell]
            INF[Infrastructure Adapters]
            DOM[Domain Core]
        end
    end

    subgraph External ["External Services"]
        DB[(PostgreSQL)]
        SMTP[Maildev / SMTP]
    end

    UI <--> API_C
    API_C -- "OpenAPI Contract" --> INF
    INF <--> DOM
    APP --> INF
    INF <--> DB
    INF <--> SMTP

    classDef primary fill:#e1f5fe,stroke:#01579b,stroke-width:2px;
    classDef secondary fill:#f3e5f5,stroke:#4a148c,stroke-width:2px;
    class DOM,UI primary;
    class INF,APP,API_C secondary;
```

### 🛠️ Tech Stack

| Component    | Key Technologies                                                                     |
|:-------------|:-------------------------------------------------------------------------------------|
| **Backend**  | Kotlin, Spring Boot 4.+, Spring Security (JWT), Liquibase, MapStruct, Testcontainers |
| **Frontend** | Angular 21, Vite, Vitest, Bootstrap 5, Transloco (i18n), Signals                     |
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
- 📖 [**Frontend Documentation**](frontend/README.md) — *Signals, Standalone & Vite*
