FROM openjdk:8-jdk-alpine
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} paas-ta-container-terraman-api.jar
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=dev","/paas-ta-container-terraman-api.jar"]
