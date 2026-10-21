# syntax=docker/dockerfile:1
# Imagen del sync-service (Feature 4). Se construye en dos etapas:
#   1. compilacion: Maven y JDK 21 compilan el proyecto (no hace falta tenerlos instalados).
#   2. final: solo el JRE 21 y el .jar, con un usuario sin privilegios y sin credenciales.
# La usa docker-compose.yml (servicio sync-service). Las pruebas se corren aparte con "mvn package".

FROM maven:3.9.9-eclipse-temurin-21 AS compilacion
WORKDIR /build
COPY pom.xml .
COPY openapi ./openapi
COPY src ./src
# La caché de ~/.m2 evita descargar las dependencias en cada construcción.
RUN --mount=type=cache,target=/root/.m2 mvn -B -q -DskipTests package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S app && adduser -S app -G app
COPY --from=compilacion /build/target/sync-service-*.jar app.jar
USER app
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
