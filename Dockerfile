# Stage 1: Build
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw

# Cache dependencies
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Cài đặt font cho môi trường Linux để hỗ trợ render đồ họa nếu cần
RUN apk add --no-cache fontconfig ttf-dejavu tzdata
ENV TZ=Asia/Ho_Chi_Minh

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8088

ENTRYPOINT ["java", "-Djava.awt.headless=true", "-jar", "app.jar"]

