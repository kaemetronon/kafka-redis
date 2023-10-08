FROM adoptopenjdk:11-jre-hotspot
COPY target/kafka-consumer-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]