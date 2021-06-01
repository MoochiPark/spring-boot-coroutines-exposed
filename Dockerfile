FROM gradle:7.0.2-jdk11 AS build
COPY ./ ./
RUN gradle clean bootJar --no-daemon

FROM openjdk:11-jre-slim
COPY --from=build /home/gradle/build/libs/*.jar /spring-exposed.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/spring-exposed.jar"]

