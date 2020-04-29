FROM openjdk:8-jdk-alpine

ADD build/libs/*.jar service3.jar

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "service3.jar"]