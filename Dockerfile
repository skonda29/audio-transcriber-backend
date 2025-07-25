# Use an official OpenJDK 17 runtime as base
FROM openjdk:17-jdk-slim

# Set working directory inside container
WORKDIR /app

# Copy JAR file from target directory
COPY target/audio-transcribe-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
