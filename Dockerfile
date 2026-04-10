# ============================================================
# Dockerfile Multi-Stage - Sistema de Gestión de Empleados
# Stage 1: Compilación con Maven + JDK 8
# Stage 2: Ejecución con JRE 8 (imagen liviana)
# ============================================================

# --- STAGE 1: BUILD ---
FROM maven:3.8.6-openjdk-8-slim AS build

WORKDIR /app

# Copiar archivos de configuración Maven primero (cacheo de dependencias)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests -B

# --- STAGE 2: RUNTIME ---
FROM openjdk:8-jre-alpine

WORKDIR /app

# Copiar JAR compilado desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Puerto de la aplicación
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=15s \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/api/empleados/ || exit 1

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]

