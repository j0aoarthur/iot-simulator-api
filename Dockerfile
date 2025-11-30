# ===============================
# Build Stage
# ===============================
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Build the application and skip tests to save time during container build
RUN mvn clean package -DskipTests

# ===============================
# Run Stage
# ===============================
# Switched from '17-jre-alpine' to '17-jre' to fix platform manifest errors
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the jar from the build stage
COPY --from=build /app/target/iot-simulator-1.0.0.jar app.jar

# Expose the port defined in application.yml
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]