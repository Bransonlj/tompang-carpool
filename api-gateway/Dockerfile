FROM maven:3.9.5-eclipse-temurin-17 AS builder
WORKDIR /app

# 1. Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# 2. Copy source last to maximize caching
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]