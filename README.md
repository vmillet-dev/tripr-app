# ✈️ Tripr

> **Modern SaaS Starter** combining the power of **Spring Boot 4 (Kotlin)** and the reactivity of **Angular 21 (Signals)**.

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)](https://github.com/example/tripr/actions)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.10-blue.svg)](https://kotlinlang.org/)
[![Angular](https://img.shields.io/badge/Angular-21-red.svg)](https://angular.dev/)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)

Tripr is a production-ready template designed for scalability, security, and a smooth developer experience. It features a **contract-first approach** using OpenAPI to bridge the gap between a strict Hexagonal backend and a reactive Zoneless frontend.

### 🏗️ Architecture Overview

```mermaid
graph TD
    %% Core Domain at the center
    subgraph Core ["&nbsp;&nbsp;&nbsp;&nbsp;Domain Core&nbsp;&nbsp;&nbsp;&nbsp;"]
        DOM{{"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Business Logic&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br/>(Services & Ports)"}}
    end

    %% Adapters surrounding the core
    subgraph Adapters ["&nbsp;&nbsp;&nbsp;&nbsp;Infrastructure Adapters (Hexagon)&nbsp;&nbsp;&nbsp;&nbsp;"]
        direction TB
        REST[REST API / Web]
        PERSIST[Persistence<br/>(PostgreSQL)]
        SEC[Security<br/>(JWT)]
        MAIL[Mail / SMTP]
    end

    %% Entry point orchestrating everything
    subgraph Shell ["Application Shell"]
        APP[Bootstrap / Config]
    end

    %% External entities
    subgraph Client ["Frontend (Angular)"]
        UI[UI Components]
        API_C[API Client]
    end

    %% Connections
    UI <--> API_C
    API_C -- "OpenAPI" --> REST
    
    APP --> Adapters
    APP --> Core
    
    REST <--> DOM
    DOM <--> PERSIST
    DOM <--> SEC
    DOM <--> MAIL

    %% Dark mode friendly styling
    classDef domain fill:#1a237e,stroke:#3f51b5,stroke-width:3px,color:#ffffff;
    classDef infra fill:#3e2723,stroke:#795548,stroke-width:2px,color:#ffffff;
    classDef app fill:#1b5e20,stroke:#4caf50,stroke-width:2px,color:#ffffff;
    classDef client fill:#4a148c,stroke:#9c27b0,stroke-width:2px,color:#ffffff;
    
    class DOM domain;
    class REST,PERSIST,SEC,MAIL infra;
    class APP app;
    class UI,API_C client;
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
