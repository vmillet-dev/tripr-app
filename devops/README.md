# Tripr App DevOps

[![Build Status](https://github.com/vmillet-dev/tripr-app/workflows/Build%20and%20Test/badge.svg)](https://github.com/vmillet-dev/tripr-app/actions)
[![Docker](https://img.shields.io/badge/Docker-Multi--Stage-blue.svg)](https://www.docker.com/)
[![Kubernetes](https://img.shields.io/badge/Kubernetes-K3s-326CE5.svg)](https://k3s.io/)
[![Ansible](https://img.shields.io/badge/Ansible-Automation-red.svg)](https://www.ansible.com/)

Infrastructure as Code and deployment automation for the Tripr App using Docker, Kubernetes (K3s), and Ansible.

## Table of Contents

- [Infrastructure Overview](#infrastructure-overview)
- [Docker Setup](#docker-setup)
- [Kubernetes Deployment](#kubernetes-deployment)
- [Ansible Configuration](#ansible-configuration)
- [CI/CD Pipeline](#cicd-pipeline)
- [Environment Variables](#environment-variables)
- [Monitoring and Logging](#monitoring-and-logging)
- [Security Considerations](#security-considerations)
- [Troubleshooting](#troubleshooting)

## Infrastructure Overview

### Containerization
**Docker Images and Multi-Stage Builds:**
- **Multi-Stage Builds** - Optimized images with separate build and runtime stages
- **Multi-Architecture Support** - ARM64 and AMD64 compatibility for diverse deployment targets
- **Layer Caching** - Efficient build times through strategic layer ordering
- **Security Scanning** - Automated vulnerability scanning in CI/CD pipeline

### Orchestration
**Kubernetes Configuration (K3s):**
- **Lightweight Kubernetes** - K3s for resource-efficient container orchestration
- **Declarative Configuration** - YAML manifests for reproducible deployments
- **Service Discovery** - Built-in DNS and service mesh capabilities
- **Auto-scaling** - Horizontal Pod Autoscaler for dynamic resource allocation

### Configuration Management
**Ansible Playbooks:**
- **Infrastructure Provisioning** - Automated server setup and configuration
- **Application Deployment** - Consistent deployment across environments
- **Configuration Management** - Centralized configuration with environment-specific variables
- **Idempotent Operations** - Safe to run multiple times without side effects

### CI/CD
**GitHub Actions Workflows:**
- **Automated Testing** - Unit, integration, and E2E tests on every commit
- **Multi-Stage Builds** - Parallel building of frontend and backend containers
- **Security Scanning** - Container vulnerability scanning and dependency checks
- **Deployment Automation** - Automated deployment to staging and production environments

## Docker Setup

### Multi-Stage Build Process

The application uses sophisticated multi-stage Docker builds for optimal image size and security:

#### Backend Dockerfile

**Location**: `devops/Dockerfile`

```dockerfile
# Multi-stage build for Spring Boot application
FROM gradle:8.14-jdk24 AS builder

WORKDIR /app

# Copy Gradle configuration files
COPY backend/build.gradle.kts backend/settings.gradle.kts ./
COPY backend/gradle/ gradle/

# Copy source code
COPY backend/ .

# Build application
RUN gradle clean bootJar --no-daemon

# Runtime stage
FROM openjdk:24-jre-slim

# Create non-root user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser

WORKDIR /app

# Copy built JAR from builder stage
COPY --from=builder /app/bootstrap/build/libs/*.jar app.jar

# Change ownership to non-root user
RUN chown -R appuser:appuser /app
USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8081/actuator/health || exit 1

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### Frontend Dockerfile

**Location**: `devops/frontend.Dockerfile`

```dockerfile
# Multi-stage build for Angular application
FROM node:22-alpine AS builder

WORKDIR /app

# Copy package files for dependency caching
COPY frontend/package*.json ./
RUN npm ci --only=production --silent

# Copy source code
COPY frontend/ .

# Build application for production
RUN npm run build

# Runtime stage with Nginx
FROM nginx:alpine

# Copy built application
COPY --from=builder /app/dist/tripr-frontend /usr/share/nginx/html

# Copy custom Nginx configuration
COPY devops/nginx.conf /etc/nginx/nginx.conf

# Security: run as non-root user
RUN addgroup -g 1001 -S nginx && \
    adduser -S -D -H -u 1001 -h /var/cache/nginx -s /sbin/nologin -G nginx -g nginx nginx

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:80/ || exit 1

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
```

### Building and Running Docker Images

#### Local Development

```bash
# Build backend image
docker build -f devops/Dockerfile -t tripr-backend:dev .

# Build frontend image
docker build -f devops/frontend.Dockerfile -t tripr-frontend:dev .

# Run with Docker Compose
docker compose -f devops/compose-dev.yaml up -d
```

#### Production Build

```bash
# Build multi-architecture images
docker buildx build --platform linux/amd64,linux/arm64 \
  -f devops/Dockerfile \
  -t vmilletdev/tripr-backend:latest \
  --push .

docker buildx build --platform linux/amd64,linux/arm64 \
  -f devops/frontend.Dockerfile \
  -t vmilletdev/tripr-frontend:latest \
  --push .
```

### Environment-Specific Configurations

#### Development Environment

**File**: `devops/compose-dev.yaml`

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:17.3
    environment:
      POSTGRES_DB: tripr
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: P4ssword!
    ports:
      - "5433:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5

  mailpit:
    image: axllent/mailpit:latest
    ports:
      - "8026:8025"  # Web UI
      - "1026:1025"  # SMTP
    environment:
      MP_SMTP_AUTH_ACCEPT_ANY: 1
      MP_SMTP_AUTH_ALLOW_INSECURE: 1

volumes:
  postgres_data:
```

#### Production Environment

**File**: `devops/compose-prod.yaml`

```yaml
version: '3.8'

services:
  backend:
    image: vmilletdev/tripr-backend:${VERSION:-latest}
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DB_HOST: postgres
      DB_PORT: 5432
      DB_NAME: ${DB_NAME}
      DB_USERNAME: ${DB_USERNAME}
      DB_PASSWORD: ${DB_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
      BASE_URL: ${BASE_URL}
    depends_on:
      postgres:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8081/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s

  frontend:
    image: vmilletdev/tripr-frontend:${VERSION:-latest}
    ports:
      - "80:80"
      - "443:443"
    depends_on:
      - backend
    healthcheck:
      test: ["CMD", "wget", "--spider", "-q", "http://localhost:80/"]
      interval: 30s
      timeout: 10s
      retries: 3

  postgres:
    image: postgres:17.3
    environment:
      POSTGRES_DB: ${DB_NAME}
      POSTGRES_USER: ${DB_USERNAME}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./backups:/backups
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${DB_USERNAME}"]
      interval: 10s
      timeout: 5s
      retries: 5

volumes:
  postgres_data:
```

### Docker Hub Integration

**Automated Image Publishing:**

```bash
# Login to Docker Hub
echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin

# Build and push images
docker buildx build --platform linux/amd64,linux/arm64 \
  -f devops/Dockerfile \
  -t vmilletdev/tripr-backend:$VERSION \
  -t vmilletdev/tripr-backend:latest \
  --push .
```

## Kubernetes Deployment

### Cluster Requirements

**Minimum System Requirements:**
- **CPU**: 2 cores minimum, 4 cores recommended
- **Memory**: 4GB minimum, 8GB recommended
- **Storage**: 20GB minimum, 50GB recommended
- **Network**: Stable internet connection for image pulls

**K3s Installation:**
```bash
# Install K3s on master node
curl -sfL https://get.k3s.io | sh -

# Get node token for worker nodes
sudo cat /var/lib/rancher/k3s/server/node-token

# Install K3s on worker nodes
curl -sfL https://get.k3s.io | K3S_URL=https://master-ip:6443 K3S_TOKEN=node-token sh -
```

### Configuration Files Explanation

#### Namespace Configuration

**File**: `devops/k8s/namespace.yaml`

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: tripr-app
  labels:
    name: tripr-app
    environment: production
```

#### Database Deployment

**File**: `devops/k8s/postgres.yaml`

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: postgres
  namespace: tripr-app
spec:
  replicas: 1
  selector:
    matchLabels:
      app: postgres
  template:
    metadata:
      labels:
        app: postgres
    spec:
      containers:
      - name: postgres
        image: postgres:17.3
        env:
        - name: POSTGRES_DB
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: database
        - name: POSTGRES_USER
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: username
        - name: POSTGRES_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: password
        ports:
        - containerPort: 5432
        volumeMounts:
        - name: postgres-storage
          mountPath: /var/lib/postgresql/data
        livenessProbe:
          exec:
            command:
            - pg_isready
            - -U
            - $(POSTGRES_USER)
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          exec:
            command:
            - pg_isready
            - -U
            - $(POSTGRES_USER)
          initialDelaySeconds: 5
          periodSeconds: 5
      volumes:
      - name: postgres-storage
        persistentVolumeClaim:
          claimName: postgres-pvc
---
apiVersion: v1
kind: Service
metadata:
  name: postgres-service
  namespace: tripr-app
spec:
  selector:
    app: postgres
  ports:
  - port: 5432
    targetPort: 5432
  type: ClusterIP
```

#### Backend Deployment

**File**: `devops/k8s/backend.yaml`

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: backend
  namespace: tripr-app
spec:
  replicas: 2
  selector:
    matchLabels:
      app: backend
  template:
    metadata:
      labels:
        app: backend
    spec:
      containers:
      - name: backend
        image: vmilletdev/tripr-backend:latest
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: DB_HOST
          value: "postgres-service"
        - name: DB_PORT
          value: "5432"
        - name: DB_NAME
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: database
        - name: DB_USERNAME
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: username
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: password
        - name: JWT_SECRET
          valueFrom:
            secretKeyRef:
              name: app-secret
              key: jwt-secret
        - name: BASE_URL
          valueFrom:
            configMapKeyRef:
              name: app-config
              key: base-url
        ports:
        - containerPort: 8081
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8081
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8081
          initialDelaySeconds: 30
          periodSeconds: 10
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
---
apiVersion: v1
kind: Service
metadata:
  name: backend-service
  namespace: tripr-app
spec:
  selector:
    app: backend
  ports:
  - port: 8081
    targetPort: 8081
  type: ClusterIP
```

#### Frontend Deployment

**File**: `devops/k8s/frontend.yaml`

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: frontend
  namespace: tripr-app
spec:
  replicas: 2
  selector:
    matchLabels:
      app: frontend
  template:
    metadata:
      labels:
        app: frontend
    spec:
      containers:
      - name: frontend
        image: vmilletdev/tripr-frontend:latest
        ports:
        - containerPort: 80
        livenessProbe:
          httpGet:
            path: /
            port: 80
          initialDelaySeconds: 10
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /
            port: 80
          initialDelaySeconds: 5
          periodSeconds: 10
        resources:
          requests:
            memory: "64Mi"
            cpu: "50m"
          limits:
            memory: "128Mi"
            cpu: "100m"
---
apiVersion: v1
kind: Service
metadata:
  name: frontend-service
  namespace: tripr-app
spec:
  selector:
    app: frontend
  ports:
  - port: 80
    targetPort: 80
  type: LoadBalancer
```

### Deployment Strategies

#### Rolling Updates

```bash
# Update backend deployment
kubectl set image deployment/backend backend=vmilletdev/tripr-backend:v1.2.0 -n tripr-app

# Monitor rollout status
kubectl rollout status deployment/backend -n tripr-app

# Rollback if needed
kubectl rollout undo deployment/backend -n tripr-app
```

#### Blue-Green Deployment

```bash
# Deploy to green environment
kubectl apply -f devops/k8s/green/ -n tripr-app

# Test green environment
kubectl port-forward service/frontend-service-green 8080:80 -n tripr-app

# Switch traffic to green
kubectl patch service frontend-service -p '{"spec":{"selector":{"version":"green"}}}' -n tripr-app

# Clean up blue environment
kubectl delete -f devops/k8s/blue/ -n tripr-app
```

## Ansible Configuration

### Playbook Structure

The Ansible configuration is organized into roles and playbooks for different deployment scenarios:

```
devops/ansible/
├── playbook/
│   ├── deploy_webapp.yml          # Main deployment playbook
│   ├── setup_k3s.yml             # K3s cluster setup
│   └── backup_database.yml       # Database backup automation
├── roles/
│   ├── common/                   # Common system configuration
│   ├── docker/                   # Docker installation and configuration
│   ├── k3s/                      # K3s installation and configuration
│   └── webapp/                   # Application deployment
├── inventory/
│   ├── production.yml            # Production servers inventory
│   ├── staging.yml               # Staging servers inventory
│   └── group_vars/               # Group-specific variables
└── ansible.cfg                   # Ansible configuration
```

### Main Deployment Playbook

**File**: `devops/ansible/playbook/deploy_webapp.yml`

```yaml
---
- name: Deploy Tripr Web Application
  hosts: webapp_servers
  become: yes
  vars:
    app_version: "{{ version | default('latest') }}"
    deployment_environment: "{{ env | default('production') }}"
    
  pre_tasks:
    - name: Update system packages
      apt:
        update_cache: yes
        upgrade: dist
      when: ansible_os_family == "Debian"
    
    - name: Ensure required directories exist
      file:
        path: "{{ item }}"
        state: directory
        mode: '0755'
      loop:
        - /opt/tripr
        - /opt/tripr/config
        - /opt/tripr/data
        - /opt/tripr/logs

  roles:
    - role: common
      tags: ['common']
    - role: docker
      tags: ['docker']
    - role: k3s
      tags: ['k3s']
      when: setup_k3s | default(false)
    - role: webapp
      tags: ['webapp']

  post_tasks:
    - name: Verify application health
      uri:
        url: "http://{{ ansible_default_ipv4.address }}/actuator/health"
        method: GET
        status_code: 200
      retries: 5
      delay: 10
      register: health_check
      
    - name: Display deployment status
      debug:
        msg: "Deployment completed successfully. Application is healthy."
      when: health_check.status == 200
```

### Inventory Management

#### Production Inventory

**File**: `devops/ansible/inventory/production.yml`

```yaml
all:
  children:
    webapp_servers:
      hosts:
        prod-server-01:
          ansible_host: 192.168.1.100
          ansible_user: ubuntu
          ansible_ssh_private_key_file: ~/.ssh/prod_key
        prod-server-02:
          ansible_host: 192.168.1.101
          ansible_user: ubuntu
          ansible_ssh_private_key_file: ~/.ssh/prod_key
      vars:
        environment: production
        app_replicas: 2
        db_backup_enabled: true
        monitoring_enabled: true
        
    database_servers:
      hosts:
        db-server-01:
          ansible_host: 192.168.1.110
          ansible_user: ubuntu
          ansible_ssh_private_key_file: ~/.ssh/prod_key
      vars:
        postgres_version: "17.3"
        backup_retention_days: 30
```

### Variable Configuration

#### Group Variables

**File**: `devops/ansible/inventory/group_vars/webapp_servers.yml`

```yaml
# Application Configuration
app_name: tripr-app
app_version: "{{ version | default('latest') }}"
app_port: 8081
app_user: tripr
app_group: tripr

# Docker Configuration
docker_compose_version: "2.24.0"
docker_networks:
  - name: tripr-network
    driver: bridge

# Database Configuration
db_host: "{{ hostvars[groups['database_servers'][0]]['ansible_default_ipv4']['address'] }}"
db_port: 5432
db_name: tripr
db_user: tripr_user

# Security Configuration
firewall_enabled: true
fail2ban_enabled: true
ssl_enabled: true
ssl_cert_path: /etc/ssl/certs/tripr.crt
ssl_key_path: /etc/ssl/private/tripr.key

# Monitoring Configuration
prometheus_enabled: true
grafana_enabled: true
log_aggregation_enabled: true

# Backup Configuration
backup_enabled: true
backup_schedule: "0 2 * * *"  # Daily at 2 AM
backup_retention_days: 30
```

### Deployment Automation

#### Running Deployments

```bash
# Deploy to production
ansible-playbook -i inventory/production.yml playbook/deploy_webapp.yml \
  --extra-vars "version=v1.2.0 env=production"

# Deploy to staging
ansible-playbook -i inventory/staging.yml playbook/deploy_webapp.yml \
  --extra-vars "version=latest env=staging"

# Setup new K3s cluster
ansible-playbook -i inventory/production.yml playbook/setup_k3s.yml \
  --extra-vars "setup_k3s=true"

# Run database backup
ansible-playbook -i inventory/production.yml playbook/backup_database.yml
```

#### Deployment with Tags

```bash
# Only update application (skip system setup)
ansible-playbook -i inventory/production.yml playbook/deploy_webapp.yml \
  --tags webapp --extra-vars "version=v1.2.0"

# Only setup Docker
ansible-playbook -i inventory/production.yml playbook/deploy_webapp.yml \
  --tags docker

# Skip database tasks
ansible-playbook -i inventory/production.yml playbook/deploy_webapp.yml \
  --skip-tags database
```

## CI/CD Pipeline

### GitHub Actions Workflow

The CI/CD pipeline is implemented using GitHub Actions with multiple workflows for different purposes:

#### Main CI/CD Workflow

**File**: `.github/workflows/cicd.yml`

```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

env:
  REGISTRY: docker.io
  BACKEND_IMAGE: vmilletdev/tripr-backend
  FRONTEND_IMAGE: vmilletdev/tripr-frontend

jobs:
  test-backend:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v4
    
    - name: Set up JDK 24
      uses: actions/setup-java@v4
      with:
        java-version: '24'
        distribution: 'temurin'
    
    - name: Cache Gradle packages
      uses: actions/cache@v4
      with:
        path: |
          ~/.gradle/caches
          ~/.gradle/wrapper
        key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
        restore-keys: |
          ${{ runner.os }}-gradle-
    
    - name: Run backend tests
      run: |
        cd backend
        ./gradlew test jacocoTestReport
    
    - name: Upload coverage reports
      uses: codecov/codecov-action@v4
      with:
        file: backend/build/reports/jacoco/test/jacocoTestReport.xml
        flags: backend

  test-frontend:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v4
    
    - name: Set up Node.js
      uses: actions/setup-node@v4
      with:
        node-version: '22'
        cache: 'npm'
        cache-dependency-path: frontend/package-lock.json
    
    - name: Install dependencies
      run: |
        cd frontend
        npm ci
    
    - name: Run frontend tests
      run: |
        cd frontend
        npm run test:ci
    
    - name: Upload coverage reports
      uses: codecov/codecov-action@v4
      with:
        file: frontend/coverage/lcov.info
        flags: frontend

  e2e-tests:
    runs-on: ubuntu-latest
    needs: [test-backend, test-frontend]
    steps:
    - uses: actions/checkout@v4
    
    - name: Start application stack
      run: |
        docker compose -f devops/compose-dev.yaml up -d
        sleep 30
    
    - name: Run E2E tests
      run: |
        cd e2e
        npm ci
        npm run test:ci
    
    - name: Upload E2E test results
      uses: actions/upload-artifact@v4
      if: always()
      with:
        name: e2e-results
        path: e2e/cypress/screenshots/

  build-and-push:
    runs-on: ubuntu-latest
    needs: [test-backend, test-frontend, e2e-tests]
    if: github.ref == 'refs/heads/main'
    steps:
    - uses: actions/checkout@v4
    
    - name: Set up Docker Buildx
      uses: docker/setup-buildx-action@v3
    
    - name: Login to Docker Hub
      uses: docker/login-action@v3
      with:
        username: ${{ secrets.DOCKER_USERNAME }}
        password: ${{ secrets.DOCKER_PASSWORD }}
    
    - name: Extract metadata
      id: meta
      uses: docker/metadata-action@v5
      with:
        images: |
          ${{ env.BACKEND_IMAGE }}
          ${{ env.FRONTEND_IMAGE }}
        tags: |
          type=ref,event=branch
          type=sha,prefix={{branch}}-
          type=raw,value=latest,enable={{is_default_branch}}
    
    - name: Build and push backend image
      uses: docker/build-push-action@v5
      with:
        context: .
        file: devops/Dockerfile
        platforms: linux/amd64,linux/arm64
        push: true
        tags: ${{ env.BACKEND_IMAGE }}:${{ steps.meta.outputs.tags }}
        cache-from: type=gha
        cache-to: type=gha,mode=max
    
    - name: Build and push frontend image
      uses: docker/build-push-action@v5
      with:
        context: .
        file: devops/frontend.Dockerfile
        platforms: linux/amd64,linux/arm64
        push: true
        tags: ${{ env.FRONTEND_IMAGE }}:${{ steps.meta.outputs.tags }}
        cache-from: type=gha
        cache-to: type=gha,mode=max

  deploy-staging:
    runs-on: ubuntu-latest
    needs: build-and-push
    if: github.ref == 'refs/heads/develop'
    environment: staging
    steps:
    - uses: actions/checkout@v4
    
    - name: Deploy to staging
      run: |
        # Deploy using Ansible
        ansible-playbook -i devops/ansible/inventory/staging.yml \
          devops/ansible/playbook/deploy_webapp.yml \
          --extra-vars "version=${{ github.sha }} env=staging"

  deploy-production:
    runs-on: ubuntu-latest
    needs: build-and-push
    if: github.ref == 'refs/heads/main'
    environment: production
    steps:
    - uses: actions/checkout@v4
    
    - name: Deploy to production
      run: |
        # Deploy using Ansible
        ansible-playbook -i devops/ansible/inventory/production.yml \
          devops/ansible/playbook/deploy_webapp.yml \
          --extra-vars "version=${{ github.sha }} env=production"
```

### Deployment to Raspberry Pi Setup

#### Raspberry Pi Configuration

**Prerequisites:**
- Raspberry Pi 4 (4GB RAM minimum)
- Ubuntu Server 22.04 LTS (64-bit)
- Docker and K3s installed

**Setup Script** (`devops/scripts/setup-rpi.sh`):

```bash
#!/bin/bash
set -e

echo "Setting up Raspberry Pi for Tripr App deployment..."

# Update system
sudo apt update && sudo apt upgrade -y

# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh
sudo usermod -aG docker $USER

# Install K3s
curl -sfL https://get.k3s.io | sh -

# Configure firewall
sudo ufw allow 22/tcp
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw allow 6443/tcp
sudo ufw --force enable

# Create application directories
sudo mkdir -p /opt/tripr/{config,data,logs}
sudo chown -R $USER:$USER /opt/tripr

echo "Raspberry Pi setup completed!"
echo "Node token: $(sudo cat /var/lib/rancher/k3s/server/node-token)"
```

### Required Secrets and Variables

#### GitHub Secrets

Configure the following secrets in your GitHub repository:

```bash
# Docker Hub credentials
DOCKER_USERNAME=your-dockerhub-username
DOCKER_PASSWORD=your-dockerhub-password

# Database credentials
DB_NAME=tripr
DB_USERNAME=tripr_user
DB_PASSWORD=secure-database-password

# JWT configuration
JWT_SECRET=your-256-bit-secret-key-minimum-32-characters

# Ansible SSH keys
ANSIBLE_SSH_PRIVATE_KEY=your-private-ssh-key
ANSIBLE_VAULT_PASSWORD=your-ansible-vault-password

# Application configuration
BASE_URL=https://your-domain.com
MAIL_HOST=smtp.your-provider.com
MAIL_PORT=587
MAIL_USERNAME=your-email@domain.com
MAIL_PASSWORD=your-email-password
```

#### Environment Variables

**Production Environment Variables:**

```bash
# Application
SPRING_PROFILES_ACTIVE=prod
BASE_URL=https://tripr.example.com

# Database
DB_HOST=postgres-service
DB_PORT=5432
DB_NAME=tripr
DB_USERNAME=tripr_user
DB_PASSWORD=${DB_PASSWORD}

# Security
JWT_SECRET=${JWT_SECRET}
JWT_EXPIRATION=3600000
JWT_REFRESH_EXPIRATION=604800000

# Email
MAIL_HOST=${MAIL_HOST}
MAIL_PORT=${MAIL_PORT}
MAIL_USERNAME=${MAIL_USERNAME}
MAIL_PASSWORD=${MAIL_PASSWORD}

# Monitoring
PROMETHEUS_ENABLED=true
GRAFANA_ENABLED=true
```

### Monitoring and Rollback Procedures

#### Health Monitoring

```bash
# Check application health
kubectl get pods -n tripr-app
kubectl logs -f deployment/backend -n tripr-app

# Monitor resource usage
kubectl top pods -n tripr-app
kubectl top nodes
```

#### Rollback Procedures

```bash
# Rollback to previous version
kubectl rollout undo deployment/backend -n tripr-app
kubectl rollout undo deployment/frontend -n tripr-app

# Rollback to specific revision
kubectl rollout history deployment/backend -n tripr-app
kubectl rollout undo deployment/backend --to-revision=2 -n tripr-app

# Emergency rollback using Ansible
ansible-playbook -i inventory/production.yml playbook/rollback.yml \
  --extra-vars "rollback_version=v1.1.0"
```

## Environment Variables

### Complete List of Required Environment Variables

#### Application Configuration

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `SPRING_PROFILES_ACTIVE` | Spring Boot profile | `dev` | Yes |
| `BASE_URL` | Application base URL | `http://localhost:8081` | Yes |
| `SERVER_PORT` | Application port | `8081` | No |

#### Database Configuration

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `DB_HOST` | Database host | `localhost` | Yes |
| `DB_PORT` | Database port | `5433` | Yes |
| `DB_NAME` | Database name | `tripr` | Yes |
| `DB_USERNAME` | Database username | `postgres` | Yes |
| `DB_PASSWORD` | Database password | - | Yes |
| `DDL_AUTO` | Hibernate DDL mode | `update` | No |
| `SHOW_SQL` | Show SQL queries | `false` | No |

#### Security Configuration

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `JWT_SECRET` | JWT signing secret | - | Yes |
| `JWT_EXPIRATION` | Access token expiration (ms) | `3600000` | No |
| `JWT_REFRESH_EXPIRATION` | Refresh token expiration (ms) | `604800000` | No |

#### Email Configuration

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `MAIL_HOST` | SMTP server host | `localhost` | Yes |
| `MAIL_PORT` | SMTP server port | `1026` | Yes |
| `MAIL_USERNAME` | SMTP username | - | No |
| `MAIL_PASSWORD` | SMTP password | - | No |
| `MAIL_FROM` | From email address | `noreply@tripr.com` | No |

### Environment-Specific Configuration

#### Development Environment

```bash
# .env.dev
SPRING_PROFILES_ACTIVE=dev
BASE_URL=http://localhost:8081
DB_HOST=localhost
DB_PORT=5433
DB_NAME=tripr
DB_USERNAME=postgres
DB_PASSWORD=P4ssword!
JWT_SECRET=development-secret-key-not-for-production-use-only
MAIL_HOST=localhost
MAIL_PORT=1026
DDL_AUTO=update
SHOW_SQL=true
```

#### Staging Environment

```bash
# .env.staging
SPRING_PROFILES_ACTIVE=staging
BASE_URL=https://staging.tripr.example.com
DB_HOST=staging-db.internal
DB_PORT=5432
DB_NAME=tripr_staging
DB_USERNAME=tripr_staging
DB_PASSWORD=${STAGING_DB_PASSWORD}
JWT_SECRET=${STAGING_JWT_SECRET}
MAIL_HOST=smtp.staging.example.com
MAIL_PORT=587
MAIL_USERNAME=${STAGING_MAIL_USERNAME}
MAIL_PASSWORD=${STAGING_MAIL_PASSWORD}
DDL_AUTO=validate
SHOW_SQL=false
```

#### Production Environment

```bash
# .env.prod
SPRING_PROFILES_ACTIVE=prod
BASE_URL=https://tripr.example.com
DB_HOST=prod-db.internal
DB_PORT=5432
DB_NAME=tripr
DB_USERNAME=tripr_prod
DB_PASSWORD=${PROD_DB_PASSWORD}
JWT_SECRET=${PROD_JWT_SECRET}
MAIL_HOST=smtp.example.com
MAIL_PORT=587
MAIL_USERNAME=${PROD_MAIL_USERNAME}
MAIL_PASSWORD=${PROD_MAIL_PASSWORD}
DDL_AUTO=validate
SHOW_SQL=false
```

### Security Considerations

#### Secret Management

**Kubernetes Secrets:**
```bash
# Create database secret
kubectl create secret generic db-secret \
  --from-literal=database=tripr \
  --from-literal=username=tripr_user \
  --from-literal=password=secure-password \
  -n tripr-app

# Create application secret
kubectl create secret generic app-secret \
  --from-literal=jwt-secret=your-256-bit-secret \
  --from-literal=mail-password=mail-password \
  -n tripr-app
```

**Ansible Vault:**
```bash
# Encrypt sensitive variables
ansible-vault encrypt_string 'secure-password' --name 'db_password'

# Edit encrypted file
ansible-vault edit inventory/group_vars/production/vault.yml
```

#### Environment Variable Validation

**Startup Validation Script** (`devops/scripts/validate-env.sh`):

```bash
#!/bin/bash
set -e

echo "Validating environment variables..."

# Required variables
REQUIRED_VARS=(
  "DB_HOST"
  "DB_NAME"
  "DB_USERNAME"
  "DB_PASSWORD"
  "JWT_SECRET"
  "BASE_URL"
)

# Check each required variable
for var in "${REQUIRED_VARS[@]}"; do
  if [ -z "${!var}" ]; then
    echo "ERROR: Required environment variable $var is not set"
    exit 1
  fi
done

# Validate JWT secret length
if [ ${#JWT_SECRET} -lt 32 ]; then
  echo "ERROR: JWT_SECRET must be at least 32 characters long"
  exit 1
fi

# Validate database connection
if ! pg_isready -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USERNAME"; then
  echo "ERROR: Cannot connect to database"
  exit 1
fi

echo "Environment validation completed successfully!"
```

## Monitoring and Logging

### Prometheus Configuration

**File**: `devops/monitoring/prometheus.yml`

```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

rule_files:
  - "alert_rules.yml"

scrape_configs:
  - job_name: 'tripr-backend'
    static_configs:
      - targets: ['backend-service:8081']
    metrics_path: '/actuator/prometheus'
    scrape_interval: 30s
    
  - job_name: 'tripr-frontend'
    static_configs:
      - targets: ['frontend-service:80']
    metrics_path: '/metrics'
    scrape_interval: 30s
    
  - job_name: 'postgres'
    static_configs:
      - targets: ['postgres-service:5432']
    scrape_interval: 30s

alerting:
  alertmanagers:
    - static_configs:
        - targets:
          - alertmanager:9093
```

### Grafana Dashboards

**Application Dashboard Configuration:**
```json
{
  "dashboard": {
    "title": "Tripr App Monitoring",
    "panels": [
      {
        "title": "Application Health",
        "type": "stat",
        "targets": [
          {
            "expr": "up{job=\"tripr-backend\"}",
            "legendFormat": "Backend Status"
          }
        ]
      },
      {
        "title": "Request Rate",
        "type": "graph",
        "targets": [
          {
            "expr": "rate(http_requests_total[5m])",
            "legendFormat": "Requests/sec"
          }
        ]
      }
    ]
  }
}
```

### Centralized Logging

**ELK Stack Configuration:**
```yaml
# docker-compose.logging.yml
version: '3.8'

services:
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.11.0
    environment:
      - discovery.type=single-node
      - xpack.security.enabled=false
    volumes:
      - elasticsearch_data:/usr/share/elasticsearch/data
    
  logstash:
    image: docker.elastic.co/logstash/logstash:8.11.0
    volumes:
      - ./logstash.conf:/usr/share/logstash/pipeline/logstash.conf
    depends_on:
      - elasticsearch
    
  kibana:
    image: docker.elastic.co/kibana/kibana:8.11.0
    ports:
      - "5601:5601"
    environment:
      - ELASTICSEARCH_HOSTS=http://elasticsearch:9200
    depends_on:
      - elasticsearch

volumes:
  elasticsearch_data:
```

## Security Considerations

### Container Security

**Security Best Practices:**
- **Non-root Users** - All containers run as non-root users
- **Minimal Base Images** - Use Alpine Linux for smaller attack surface
- **Security Scanning** - Automated vulnerability scanning in CI/CD
- **Read-only Filesystems** - Containers use read-only root filesystems where possible

**Security Scanning Integration:**
```yaml
# .github/workflows/security.yml
- name: Run Trivy vulnerability scanner
  uses: aquasecurity/trivy-action@master
  with:
    image-ref: 'vmilletdev/tripr-backend:latest'
    format: 'sarif'
    output: 'trivy-results.sarif'

- name: Upload Trivy scan results
  uses: github/codeql-action/upload-sarif@v3
  with:
    sarif_file: 'trivy-results.sarif'
```

### Network Security

**Kubernetes Network Policies:**
```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: tripr-network-policy
  namespace: tripr-app
spec:
  podSelector: {}
  policyTypes:
  - Ingress
  - Egress
  ingress:
  - from:
    - podSelector:
        matchLabels:
          app: frontend
    ports:
    - protocol: TCP
      port: 8081
  egress:
  - to:
    - podSelector:
        matchLabels:
          app: postgres
    ports:
    - protocol: TCP
      port: 5432
```

### Secrets Management

**HashiCorp Vault Integration:**
```bash
# Install Vault
helm repo add hashicorp https://helm.releases.hashicorp.com
helm install vault hashicorp/vault

# Configure Vault for Kubernetes
vault auth enable kubernetes
vault write auth/kubernetes/config \
  token_reviewer_jwt="$(cat /var/run/secrets/kubernetes.io/serviceaccount/token)" \
  kubernetes_host="https://$KUBERNETES_PORT_443_TCP_ADDR:443" \
  kubernetes_ca_cert=@/var/run/secrets/kubernetes.io/serviceaccount/ca.crt
```

## Troubleshooting

### Common Infrastructure Issues

#### Docker Build Problems

**Problem**: Multi-stage build fails with dependency errors

**Solution**:
```bash
# Clear Docker build cache
docker builder prune -a

# Build with no cache
docker build --no-cache -f devops/Dockerfile -t tripr-backend:debug .

# Check build context size
docker build --progress=plain -f devops/Dockerfile .
```

#### Kubernetes Deployment Issues

**Problem**: Pods stuck in Pending state

**Solution**:
```bash
# Check node resources
kubectl describe nodes
kubectl top nodes

# Check pod events
kubectl describe pod <pod-name> -n tripr-app

# Check resource quotas
kubectl describe resourcequota -n tripr-app
```

**Problem**: Service not accessible

**Solution**:
```bash
# Check service endpoints
kubectl get endpoints -n tripr-app

# Test service connectivity
kubectl run debug --image=busybox -it --rm --restart=Never -- sh
# Inside the pod:
nslookup backend-service.tripr-app.svc.cluster.local
wget -qO- http://backend-service.tripr-app.svc.cluster.local:8081/actuator/health
```

#### Ansible Deployment Problems

**Problem**: Ansible playbook fails with SSH connection errors

**Solution**:
```bash
# Test SSH connectivity
ansible all -i inventory/production.yml -m ping

# Check SSH key permissions
chmod 600 ~/.ssh/prod_key

# Use verbose mode for debugging
ansible-playbook -vvv -i inventory/production.yml playbook/deploy_webapp.yml
```

**Problem**: Docker service not starting on target hosts

**Solution**:
```bash
# Check Docker service status
ansible webapp_servers -i inventory/production.yml -m shell -a "systemctl status docker"

# Restart Docker service
ansible webapp_servers -i inventory/production.yml -m shell -a "systemctl restart docker" --become
```

### Performance Issues

#### Container Resource Problems

**Problem**: Application containers consuming too much memory

**Solution**:
```bash
# Check container resource usage
kubectl top pods -n tripr-app

# Update resource limits
kubectl patch deployment backend -n tripr-app -p '{"spec":{"template":{"spec":{"containers":[{"name":"backend","resources":{"limits":{"memory":"2Gi"}}}]}}}}'

# Monitor resource usage over time
kubectl logs -f deployment/backend -n tripr-app | grep -i memory
```

#### Database Performance Issues

**Problem**: Database queries running slowly

**Solution**:
```bash
# Check database connections
kubectl exec -it postgres-pod -n tripr-app -- psql -U tripr_user -d tripr -c "SELECT * FROM pg_stat_activity;"

# Analyze slow queries
kubectl exec -it postgres-pod -n tripr-app -- psql -U tripr_user -d tripr -c "SELECT query, mean_time, calls FROM pg_stat_statements ORDER BY mean_time DESC LIMIT 10;"

# Check database size and indexes
kubectl exec -it postgres-pod -n tripr-app -- psql -U tripr_user -d tripr -c "\dt+"
```

### CI/CD Pipeline Issues

#### GitHub Actions Failures

**Problem**: Docker build fails in CI/CD

**Solution**:
```bash
# Check GitHub Actions logs
# Navigate to Actions tab in GitHub repository

# Test build locally
docker build -f devops/Dockerfile -t test-build .

# Check for platform-specific issues
docker buildx build --platform linux/amd64,linux/arm64 -f devops/Dockerfile .
```

**Problem**: Deployment fails due to missing secrets

**Solution**:
```bash
# Verify GitHub secrets are configured
# Go to Settings > Secrets and variables > Actions

# Test secret access in workflow
echo "Testing secret access..."
echo ${{ secrets.DOCKER_USERNAME }} | wc -c
```

### Getting Help

**Infrastructure Resources:**
- [Docker Documentation](https://docs.docker.com/)
- [Kubernetes Documentation](https://kubernetes.io/docs/)
- [Ansible Documentation](https://docs.ansible.com/)
- [K3s Documentation](https://docs.k3s.io/)

**Community Support:**
- [Docker Community](https://www.docker.com/community/)
- [Kubernetes Slack](https://kubernetes.slack.com/)
- [Ansible Community](https://www.ansible.com/community)

**Monitoring and Troubleshooting:**
- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Documentation](https://grafana.com/docs/)
- [ELK Stack Documentation](https://www.elastic.co/guide/)

---

**Built with ❤️ using Docker, Kubernetes, and Ansible. Happy deploying! 🚀**
