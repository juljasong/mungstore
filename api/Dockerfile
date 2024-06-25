FROM openjdk:17
RUN mkdir -p deploy
WORKDIR /deploy
COPY ./build/libs/api-0.0.1-SNAPSHOT.jar api.jar
ENTRYPOINT ["java", "-jar", "/deploy/api.jar"]