FROM maven:3.9.5-eclipse-temurin-17 AS builder
WORKDIR /app

# 1. Cache dependencies
COPY chat-service/pom.xml chat-service/pom.xml
WORKDIR /app/chat-service
RUN mvn dependency:go-offline

# 2. Copy source last to maximize caching
COPY chat-service/src ./src
COPY schemas ../schemas 
# Generate Avro classes
RUN mvn generate-sources
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/chat-service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]