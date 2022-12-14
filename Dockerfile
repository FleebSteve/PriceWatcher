FROM openjdk:17-jdk-slim-buster
WORKDIR /app

COPY backend/Application/target/Application-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT java $JAVA_OPTS -jar app.jar
