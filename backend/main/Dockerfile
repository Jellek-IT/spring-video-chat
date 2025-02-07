FROM gradle:8.12.1-jdk21-alpine as build
RUN echo "test"
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build -x test --no-daemon

FROM eclipse-temurin:21-jdk-alpine-3.21
WORKDIR /opt/app
COPY --from=build /home/gradle/src/build/libs/*.jar /opt/app/app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/opt/app/app.jar"]
