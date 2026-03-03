# Build
FROM maven:4.0.0-rc-5-eclipse-temurin-21-noble as build
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests

# Runtime
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build ./build/target/*.jar ./products.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "products.jar"]