#!/bin/bash

echo "Testing Tripr App Monitoring Setup"
echo "=================================="

echo "1. Testing Spring Boot Actuator endpoints..."
curl -s http://localhost:8081/actuator/health | jq '.' || echo "Health endpoint not available"
curl -s http://localhost:8081/actuator/metrics | jq '.names[0:5]' || echo "Metrics endpoint not available"
curl -s http://localhost:8081/actuator/prometheus | head -10 || echo "Prometheus endpoint not available"

echo ""
echo "2. Testing Prometheus metrics collection..."
curl -s http://localhost:9090/api/v1/targets | jq '.data.activeTargets[] | select(.labels.job=="tripr-app")' || echo "Prometheus not collecting app metrics"

echo ""
echo "3. Testing Grafana dashboard..."
curl -s -u admin:admin http://localhost:3000/api/health || echo "Grafana not accessible"

echo ""
echo "4. Testing log correlation..."
echo "Check application logs for correlation IDs in recent requests"

echo ""
echo "Monitoring test completed!"
echo "Access URLs:"
echo "- Application: http://localhost:8081"
echo "- Actuator: http://localhost:8081/actuator"
echo "- Prometheus: http://localhost:9090"
echo "- Grafana: http://localhost:3000 (admin/admin)"
