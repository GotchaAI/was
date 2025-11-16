FROM eclipse-temurin:17 AS builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY gotcha gotcha
COPY gotcha-socket gotcha-socket
COPY gotcha-auth gotcha-auth
COPY gotcha-user gotcha-user
COPY gotcha-common gotcha-common
COPY gotcha-domain gotcha-domain

RUN chmod +x ./gradlew
RUN apt-get update && apt-get install -y findutils
RUN ./gradlew :gotcha:bootJar -x test


FROM eclipse-temurin:17
RUN mkdir /opt/app
COPY --from=builder /app/gotcha/build/libs/*.jar /opt/app/spring-boot-application.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/opt/app/spring-boot-application.jar"]
