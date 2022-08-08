FROM openjdk:8-alpine
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} paas-ta-container-terraman-api.jar

RUN apk update \
    && apk add --no-cache openssh-client \
    && apk add --no-cache ca-certificates \
    && apk add --no-cache curl \
    && apk add --no-cache sudo \
    && apk add --no-cache bash \

RUN curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl" \
    && sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=dev", "/paas-ta-container-terraman-api.jar"]