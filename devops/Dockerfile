# Step 1: Build Angular frontend
FROM node:22 AS frontend-build
WORKDIR /app
COPY ./frontend/package*.json ./
RUN npm install
COPY ./frontend .
RUN npm run build

# Step 2: Build Spring Boot backend with Angular files included
FROM gradle:8-jdk21 AS backend-build
WORKDIR /app
COPY ./backend .
COPY --from=frontend-build /app/dist/browser/ bootstrap/src/main/resources/static/
COPY --from=frontend-build /app/public/ bootstrap/src/main/resources/static/
RUN gradle :bootstrap:bootJar

# Step 3: Create the final image with the fat JAR
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=backend-build /app/bootstrap/build/libs/app.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
