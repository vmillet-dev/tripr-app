# Tripr-App Monitoring Stack

## Overview
Complete monitoring solution with metrics, logs, and Kubernetes dashboard.

## Components

### 1. Metrics Monitoring (Prometheus + Grafana)
```bash
cd devops/monitoring
docker-compose -f docker-compose.monitoring.yml up -d
```
- **Grafana**: http://localhost:3000 (admin/admin)
- **Prometheus**: http://localhost:9090

### 2. Log Aggregation (ELK Stack)
```bash
cd devops/monitoring
docker-compose -f docker-compose.logs.yml up -d
```
- **Kibana**: http://localhost:5601
- **Elasticsearch**: http://localhost:9200

### 3. Complete Stack (All Services)
```bash
cd devops/monitoring
docker-compose -f docker-compose.monitoring.yml -f docker-compose.logs.yml up -d
```

## Dashboards

### Grafana Dashboards
- **Spring Boot Application**: Pre-configured with JVM metrics, API performance, custom business metrics
- **Infrastructure**: System metrics and health checks

### Kibana Log Dashboard
- **Search Logs**: Full-text search across all application logs
- **Filter by Level**: ERROR, WARN, INFO, DEBUG
- **Access Logs**: Track API requests with IP addresses and user agents
- **Correlation ID**: Trace requests across services

## Kubernetes Pod Monitoring

### View Pods
```bash
# List all pods
kubectl get pods -n default

# List pods with more details
kubectl get pods -o wide -n default

# Watch pods in real-time
kubectl get pods -w -n default
```

### View Logs
```bash
# View logs from deployment
kubectl logs -f deployment/tripr-app -n default

# View logs from specific pod
kubectl logs -f pod/tripr-app-xxx-yyy -n default

# View logs with timestamps
kubectl logs --timestamps=true deployment/tripr-app -n default

# View previous container logs (if pod restarted)
kubectl logs --previous pod/tripr-app-xxx-yyy -n default
```

### Pod Details
```bash
# Describe pod (events, status, resources)
kubectl describe pod tripr-app-xxx-yyy -n default

# Get pod resource usage
kubectl top pod tripr-app-xxx-yyy -n default

# Execute commands in pod
kubectl exec -it tripr-app-xxx-yyy -n default -- /bin/bash
```

### Kubernetes Dashboard Access
```bash
# Get dashboard token
kubectl -n kubernetes-dashboard create token admin-user

# Access dashboard (after deployment)
https://dashboard.{your-domain}
```

## Log Search Examples

### Kibana Queries
```
# Search for errors
level:ERROR

# Search by correlation ID
correlation_id:"abc123-def456"

# Search access logs from specific IP
client_ip:"192.168.1.100" AND tags:access_log

# Search API endpoints
endpoint:"/api/users" AND http_method:"POST"

# Search by time range and status
status_code:500 AND @timestamp:[now-1h TO now]
```

## Metrics Available

### Application Metrics
- `user_login_attempts_total` - User login attempts
- `user_login_failures_total` - Failed login attempts  
- `password_reset_requests_total` - Password reset requests
- `password_reset_success_total` - Successful password resets
- `api_request_duration_seconds` - API response times

### JVM Metrics
- `jvm_memory_used_bytes` - Memory usage
- `jvm_gc_collection_seconds` - Garbage collection
- `jvm_threads_current` - Thread count

### System Metrics
- `process_cpu_usage` - CPU usage
- `system_load_average_1m` - System load

## Troubleshooting

### Check Service Status
```bash
# Check if services are running
docker-compose ps

# View service logs
docker-compose logs grafana
docker-compose logs prometheus
docker-compose logs kibana
```

### Common Issues
1. **Empty Grafana Dashboard**: Wait 30 seconds for Prometheus to scrape metrics
2. **No Logs in Kibana**: Ensure application is running and generating logs
3. **Connection Issues**: Check if all services are in the same Docker network
