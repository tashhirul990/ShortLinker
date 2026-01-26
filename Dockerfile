# ----------- Build Stage (Java 21) -----------
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /app

COPY ./pom.xml .
COPY ./src ./src

RUN mvn clean package -DskipTests


# ----------- Run Stage (Java 21) -----------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
