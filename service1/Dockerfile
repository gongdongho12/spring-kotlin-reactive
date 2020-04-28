FROM openjdk:8-jdk-alpine

ADD build/libs/*.jar service1.jar

ENTRYPOINT ["java", "-jar", "service1.jar"]