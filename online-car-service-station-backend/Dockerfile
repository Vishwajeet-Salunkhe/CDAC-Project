# Stage 1: Build the application using a full JDK
FROM eclipse-temurin:17-jdk as builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

# Install Maven and build the app
RUN apt-get update && apt-get install -y maven && mvn clean package -DskipTests

# Stage 2: Create a lightweight runtime image
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
