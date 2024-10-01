FROM openjdk:17-jdk-alpine
COPY target/notification_service-0.0.1-SNAPSHOT.jar notification.jar
ENTRYPOINT ["java", "-jar", "notification.jar"]