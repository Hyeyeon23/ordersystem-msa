FROM eclipse-temurin:17-jdk as stage1

WORKDIR /app
COPY gradle gradle
COPY src src
COPY build.gradle .
COPY settings.gradle .
COPY gradlew .

RUN chmod +x gradlew
RUN ./gradlew bootjar

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=stage1 /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]