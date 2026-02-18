# Step 1: Build Angular frontend
FROM timbru31/java-node:25-jdk-24 AS frontend-build
WORKDIR /app
COPY ./frontend/package*.json ./
COPY ./api-spec/src/main/openapi/ ../api-spec/src/main/openapi/
RUN npm install
COPY ./frontend .
RUN npm run build

# Step 2: Build Spring Boot backend with Angular files included
FROM gradle:9.3.1-jdk-25-and-25 AS backend-build
WORKDIR /app
COPY . .
COPY --from=frontend-build /app/dist/ application/src/main/resources/static/
COPY --from=frontend-build /app/public/ application/src/main/resources/static/
RUN gradle :backend:application:bootJar

# Step 3: Create the final image with the fat JAR (use multiarch image)
FROM eclipse-temurin:25.0.2_10-jre-alpine
WORKDIR /app
COPY --from=backend-build /app/backend/application/build/libs/app.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
