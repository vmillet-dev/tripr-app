#!/bin/bash

# Test script for verifying Prometheus and Grafana accessibility

echo "Starting Docker Compose services..."
cd ${BACKEND_DIR:-./}
docker compose up -d

echo "Waiting for services to start..."
sleep 15

echo "Testing Prometheus accessibility..."
curl -s http://localhost:9090/-/healthy
if [ $? -eq 0 ]; then
  echo "✅ Prometheus is accessible"
else
  echo "❌ Prometheus is not accessible"
fi

echo -e "\nTesting Grafana accessibility..."
curl -s http://localhost:3000/api/health
if [ $? -eq 0 ]; then
  echo "✅ Grafana is accessible"
else
  echo "❌ Grafana is not accessible"
fi

echo -e "\nTesting Prometheus metrics scraping..."
curl -s http://localhost:9090/api/v1/targets | grep -q "spring-boot"
if [ $? -eq 0 ]; then
  echo "✅ Prometheus is scraping Spring Boot metrics"
else
  echo "❌ Prometheus is not scraping Spring Boot metrics"
fi

echo -e "\nTesting Grafana datasource..."
curl -s -u admin:admin http://localhost:3000/api/datasources | grep -q "Prometheus"
if [ $? -eq 0 ]; then
  echo "✅ Grafana is connected to Prometheus"
else
  echo "❌ Grafana is not connected to Prometheus"
fi

echo -e "\nTesting Spring Boot Actuator metrics endpoint..."
curl -s http://localhost:8081/actuator/prometheus
if [ $? -eq 0 ]; then
  echo "✅ Spring Boot Actuator metrics endpoint is accessible"
else
  echo "❌ Spring Boot Actuator metrics endpoint is not accessible"
fi

echo -e "\nTests completed. Use docker compose down to stop services."
