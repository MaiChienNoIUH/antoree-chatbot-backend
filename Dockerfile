# Sử dụng JDK để build
FROM gradle:7.6.0-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle clean build -x test

# Chạy ứng dụng
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
