# ✈️ Tripr

> **fast SaaS Starter** combining the power of **Spring Boot 3 (Kotlin)** and the reactivity of **Angular 21 (Signals)**.

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)](https://github.com/example/tripr/actions)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.10-blue.svg)](https://kotlinlang.org/)
[![Angular](https://img.shields.io/badge/Angular-21-red.svg)](https://angular.dev/)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)

Tripr is a production-ready template designed for scalability, security, and a smooth developer experience. It integrates a strict hexagonal architecture on the backend and a reactive zoneless approach on the frontend.

### 🏗️ Architecture Overview

```mermaid
graph LR
    User((User)) <--> Front[Angular App <br/><i>Signals & Zoneless</i>]
    Front <--> Back[Spring Boot API <br/><i>Hexagonal Architecture</i>]
    Back <--> DB[(PostgreSQL)]
    Back <--> SMTP[SMTP Service]
```

### 🛠️ Tech Stack

| Component    | Key Technologies                                                                     |
|:-------------|:-------------------------------------------------------------------------------------|
| **Backend**  | Kotlin, Spring Boot 4.+, Spring Security (JWT), Liquibase, MapStruct, Testcontainers |
| **Frontend** | Angular 21, Vite, Vitest, Bootstrap 5, Transloco (i18n), Signals                     |
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
