# FROM eclipse-temurin:21-jdk AS builder
# WORKDIR /app
# COPY geospatial-service/ ./geospatial-service
# COPY schemas/ ./schemas

# WORKDIR /app/geospatial-service
# # Generate Avro classes
# RUN ./mvnw generate-sources
# RUN ./mvnw clean package -DskipTests

# FROM eclipse-temurin:21-jre
# WORKDIR /app/geospatial-service
# COPY --from=builder /app/geospatial-service/target/*.jar app.jar
# ENTRYPOINT ["java", "-jar", "app.jar"]
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY /geospatial-service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
