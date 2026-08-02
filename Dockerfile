FROM eclipse-temurin:21-jdk AS build

WORKDIR /workspace

COPY gradlew .
COPY gradle gradle
COPY settings.gradle.kts .
COPY build.gradle.kts .
COPY chroniqor-core chroniqor-core
COPY chroniqor-runtime chroniqor-runtime

RUN chmod +x gradlew
RUN ./gradlew :chroniqor-runtime:bootJar --no-daemon
RUN find chroniqor-runtime/build/libs -name "*-plain.jar" -delete

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build \
    /workspace/chroniqor-runtime/build/libs/*.jar \
    app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]