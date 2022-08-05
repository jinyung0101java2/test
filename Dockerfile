FROM ubuntu:22.04
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} paas-ta-container-terraman-api.jar
ENTRYPOINT ["pwd"]
