# --- Build stage ---
FROM gradle:8.4.0-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle clean build -x test

# --- Run stage ---
FROM openjdk:17-jdk-slim
WORKDIR /app

# Chỉ copy đúng file jar đã build (loại bỏ *-plain.jar)
COPY --from=builder /app/build/libs/*-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
