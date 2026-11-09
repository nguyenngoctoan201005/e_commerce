FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built jar from target
COPY target/*.jar app.jar

EXPOSE 8080

# Run with container-aware JVM settings
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]

