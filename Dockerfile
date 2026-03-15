# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and frontend first
COPY backend/pom.xml ./backend/
COPY frontend/ ./frontend/

# Cache dependencies
WORKDIR /app/backend
RUN mvn dependency:go-offline

# Copy backend source
COPY backend/src ./src

# Create the static resource directory if it doesn't exist (though it should)
RUN mkdir -p src/main/resources/static

# Copy frontend to backend static resources
RUN cp -r ../frontend/* src/main/resources/static/

RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/backend/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
