#!/bin/bash

# Start backend
cd ../backend
./gradlew bootRun --args='--spring.profiles.active=dev' &
BACKEND_PID=$!

# Wait for backend to start
echo "Waiting for backend to start..."
until curl -s http://localhost:8081/api/auth/login -o /dev/null -w "%{http_code}" | grep -q "4"; do
  echo "Backend not ready yet, waiting..."
  sleep 2
done
echo "Backend is ready!"

# Start frontend
cd ../frontend
npm start &
FRONTEND_PID=$!

# Wait for frontend to start
echo "Waiting for frontend to start..."
until curl -s http://localhost:4200 > /dev/null; do
  sleep 1
done

# Run Cypress tests
cd ../e2e
npm run cypress:run

# Cleanup
kill $FRONTEND_PID
kill $BACKEND_PID
