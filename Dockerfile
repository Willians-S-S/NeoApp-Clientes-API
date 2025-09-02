FROM maven:3.9.8-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn

RUN mvn dependency:go-offline
RUN apt-get update && apt-get install -y openssl
COPY src src

RUN openssl genpkey -algorithm RSA -out src/main/resources/app.key -pkeyopt rsa_keygen_bits:2048

RUN openssl pkey -in src/main/resources/app.key -pubout -out src/main/resources/app.pub

RUN mvn package -DskipTests


FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]