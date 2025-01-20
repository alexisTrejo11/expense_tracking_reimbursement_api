FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle build -x test

FROM openjdk:17-slim

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar
COPY ./publicKey.pem /app/config/publicKey.pem
COPY ./privateKey.pem /app/config/privateKey.pem

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]