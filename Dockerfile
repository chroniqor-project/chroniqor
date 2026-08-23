FROM eclipse-temurin:21-jdk@sha256:85f00967bcc624fc19fa9c2cf124ea426a5363898e267141726f31f358c2e14b AS build

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

FROM eclipse-temurin:21-jre@sha256:7a65df4b22d2de92d4e04056e884f3b9122d70b21e2847fd66084278bd0ce037

WORKDIR /app

COPY --from=build \
    /workspace/chroniqor-runtime/build/libs/*.jar \
    app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
