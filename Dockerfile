# Sử dụng image JDK 21 (LTS) để build ứng dụng (tương thích tốt với Spring Boot 3.2.x và Java 19)
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copy các file build Maven trước để tận dụng Docker Cache cho các dependency
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

# Copy source code và build file jar
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Stage cuối để chạy ứng dụng (sử dụng JRE cho nhẹ)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy file jar từ stage build qua (sử dụng wildcard để tự động lấy file jar duy nhất)
COPY --from=build /app/target/*.jar app.jar

# Khai báo port mặc định của Spring Boot
EXPOSE 8080

# Chạy ứng dụng
# Lưu ý: Khi chạy container, bạn có thể cần truyền thêm biến môi trường nếu DB không ở localhost
# Ví dụ: -e SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/thegioididong2
ENTRYPOINT ["java", "-jar", "app.jar"]
