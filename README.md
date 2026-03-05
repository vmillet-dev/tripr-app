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
    %% Client Layer (Left)
    subgraph Client ["&nbsp;&nbsp;&nbsp;Frontend (Angular)&nbsp;&nbsp;&nbsp;"]
        direction TB
        UI["UI Components<br/>(Signals / Zoneless)"]
        API_C["API Client<br/>(Generated)"]
    end

    %% Middle Layer (Bridge)
    OC[["&nbsp;&nbsp;&nbsp;OpenAPI Generator&nbsp;&nbsp;&nbsp;"]]

    %% Backend Layer (Right)
    subgraph Backend ["&nbsp;&nbsp;&nbsp;Backend (Spring Boot 4)&nbsp;&nbsp;&nbsp;"]
        direction TB
        
        subgraph Shell ["Application Shell"]
            APP["Bootstrap / Config"]
        end

        subgraph Hexagon ["Hexagonal Core"]
            direction TB
            INF["Infrastructure Adapters<br/>(REST, Persistence, Security)"]
            DOM{{"&nbsp;&nbsp;&nbsp;Domain Core&nbsp;&nbsp;&nbsp;<br/>(Services & Ports)"}}
        end
    end

    %% Connections
    UI <--> API_C
    API_C <--> OC
    OC <--> INF
    
    APP -.-> INF
    APP -.-> DOM
    
    INF <--> DOM

    %% Styling
    classDef domain fill:#1a237e,stroke:#3f51b5,stroke-width:3px,color:#ffffff;
    classDef infra fill:#3e2723,stroke:#795548,stroke-width:2px,color:#ffffff;
    classDef app fill:#1b5e20,stroke:#4caf50,stroke-width:2px,color:#ffffff;
    classDef client fill:#4a148c,stroke:#9c27b0,stroke-width:2px,color:#ffffff;
    classDef contract fill:#f57f17,stroke:#fbc02d,stroke-width:2px,color:#ffffff;
    
    class DOM domain;
    class INF infra;
    class APP app;
    class UI,API_C client;
    class OC contract;
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
