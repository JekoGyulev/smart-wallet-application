
# 1 : Use Java (17,21..) as base image
FROM bellsoft/liberica-openjdk-alpine:17
# 2 : Copy the Spring Boot JAR file into the container
COPY target/smart-wallet-application-0.0.1-SNAPSHOT.jar app.jar
# 3 : Define how to run the application
ENTRYPOINT ["java", "-DSpring.profiles.active=prod", "-jar", "app.jar" ]