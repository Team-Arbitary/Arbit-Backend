# Multi-stage Dockerfile for Spring Boot Application
# Stage 1: Build the application
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /build

# Copy pom.xml and download dependencies (for better layer caching)
COPY app/pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY app/src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create a non-root user to run the application
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the built jar from the build stage
COPY --from=build /build/target/*.jar app.jar

# Expose the application port
EXPOSE 5509

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:5509/transformer-thermal-inspection/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

# Optional: Add JVM options for production
# ENTRYPOINT ["java", "-Xms512m", "-Xmx2g", "-XX:+UseG1GC", "-jar", "app.jar"]
