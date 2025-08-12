FROM eclipse-temurin:21-jdk as build
WORKDIR /app
COPY . .
RUN ./gradlew build --no-daemon -x test

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/.env .env

COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8087
ENTRYPOINT ["java", "-jar", "app.jar"]
