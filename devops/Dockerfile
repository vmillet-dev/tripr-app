# Step 1: Build Angular frontend
FROM node:20 AS frontend-build
WORKDIR /app
COPY ./frontend/package*.json ./
RUN npm install
COPY ./frontend .
RUN npm run build

# Step 2: Build Spring Boot backend with Angular files included
FROM gradle:8-jdk21 AS backend-build
WORKDIR /app
COPY ./backend .
COPY --from=frontend-build /app/dist/browser/ src/main/resources/static/
COPY --from=frontend-build /app/public/ src/main/resources/static/
RUN gradle bootJar

# Step 3: Create the final image with the fat JAR
FROM alpine/java:21-jre
WORKDIR /app
COPY --from=backend-build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
