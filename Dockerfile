# ---- Build stage ----
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Copy wrapper and pom first so dependency downloads are cached
# separately from source changes (faster rebuilds)
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# ---- Runtime stage ----
FROM eclipse-temurin:25-jre AS runtime
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]