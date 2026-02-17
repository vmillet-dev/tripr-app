# Step 1: Build Angular frontend
FROM node:22.22.0 AS frontend-build
WORKDIR /app
COPY ./frontend/package*.json ./
RUN npm install
COPY ./frontend .
RUN npm run build

# Step 2: Build Spring Boot backend with Angular files included
FROM gradle:9.3.1-jdk-25-and-25 AS backend-build
WORKDIR /app
COPY ./backend .
COPY --from=frontend-build /app/dist/browser/ application/src/main/resources/static/
COPY --from=frontend-build /app/public/ application/src/main/resources/static/
RUN gradle :application:bootJar

# Step 3: Create the final image with the fat JAR (use multiarch image)
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=backend-build /app/application/build/libs/app.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
