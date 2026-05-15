FROM eclipse-temurin:17
WORKDIR /app
COPY target/kafka-demo-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]