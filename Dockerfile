# Stage 1: Build the JAR
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the JAR
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Render uses PORT environment variable (default 10000)
EXPOSE $PORT

# Use production profile and dynamic port
ENTRYPOINT ["java", "-Dspring.profiles.active=production", "-Dserver.port=${PORT:-10000}", "-jar", "/app/app.jar"] 