FROM openjdk:8-alpine
ARG JAR_FILE=*.jar
RUN apk add --no-cache \
  openssh-client \
  ca-certificates \
  curl \
  sudo \
  bash
RUN curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl" \
    && sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl

COPY ${JAR_FILE} paas-ta-container-terraman-api.jar
ENTRYPOINT ["java","-jar","/paas-ta-container-terraman-api.jar"]