
# Build the Spring Boot application using the Maven installation in this image
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src src

RUN mvn clean package -DskipTests

# Run the application with Java 21
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/banking-application-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
