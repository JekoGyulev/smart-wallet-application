
# Stage 1: Build the JAR
FROM maven:3.9.0-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the JAR
FROM bellsoft/liberica-openjdk-alpine:21
WORKDIR /app
COPY --from=build /app/target/smart-wallet-application-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
