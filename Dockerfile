FROM openjdk:8-jre-alpine

WORKDIR /app

COPY target/worker.jar /app

ENTRYPOINT java -jar -DurlSolicitacao=${URL_SOLICITACAO} worker.jar