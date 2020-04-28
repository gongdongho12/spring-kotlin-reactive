FROM openjdk:8-jdk-alpine

ADD build/libs/*.jar service2.jar

ENTRYPOINT ["java", "-jar", "service2.jar"]