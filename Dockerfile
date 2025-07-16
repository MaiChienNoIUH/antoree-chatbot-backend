# Sử dụng JDK 17
FROM eclipse-temurin:17-jdk-alpine

# Đặt thư mục làm việc
WORKDIR /app

# Copy file jar đã build
COPY build/libs/*.jar app.jar

# Chạy ứng dụng
ENTRYPOINT ["java","-jar","/app/app.jar"]
