# 🧩 Desafío Técnico: Servlets + AJAX

Aplicación web desarrollada en Java 8 que implementa un servicio de gestión de empleados utilizando **Servlets**, **JDBC** y **AJAX nativo**, cumpliendo con los requisitos del desafío.

---

## 🚀 Tecnologías utilizadas

- Java 8
- Spring Boot 2.7 (runtime)
- Apache Tomcat embebido
- Servlets (HttpServlet)
- JDBC (sin ORM)
- Base de datos en memoria H2
- HTML + CSS + JavaScript (Fetch API)
- Maven

---

## 📦 Cómo ejecutar la aplicación

### 1. Requisitos

- Java 8 instalado
- Maven instalado

### 2. Ejecutar el proyecto

Desde consola:

```bash
mvn spring-boot:run
```

O desde IntelliJ:

Ejecutar la clase principal:
DesafioServletsAjaxApplication

---

## 🌐 Acceso a la aplicación

Frontend:
http://localhost:8080/index.html

Consola H2:
http://localhost:8080/h2-console

### Configuración H2

- JDBC URL: jdbc:h2:mem:empleadosdb
- User: sa
- Password: (vacío)

---

## 🔌 Endpoints disponibles

### 🔹 GET /api/empleados
Obtiene la lista de empleados.

### 🔹 POST /api/empleados
Crea un nuevo empleado.

Ejemplo request:

```json
{
  "nombre": "Juan",
  "apellido": "Perez",
  "rutDni": "12345678-5",
  "cargo": "Backend",
  "salarioBase": 700000,
  "bono": 100000,
  "descuentos": 50000
}
```

### 🔹 DELETE /api/empleados/{id}

Ejemplo:

```text
DELETE http://localhost:8080/api/empleados/1
```

---

## 🧪 Cómo probar la aplicación

### ✔ Opción 1: Desde la interfaz web

1. Abrir:
   http://localhost:8080/index.html

2. Probar:
- Crear empleado
- Validaciones del formulario
- Listar empleados
- Eliminar empleados

### ✔ Opción 2: Usando Postman

#### Crear empleado (POST)

```text
POST http://localhost:8080/api/empleados
```

#### Listar empleados (GET)

```text
GET http://localhost:8080/api/empleados
```

#### Eliminar empleado (DELETE)

```text
DELETE http://localhost:8080/api/empleados/1
```

---

## ⚠️ Validaciones implementadas

### 🔹 Backend

- RUT/DNI duplicado
- Salario base mínimo configurable
- Bono máximo (50% del salario base)
- Descuentos no pueden superar el salario
- Validación real de RUT (módulo 11)
- Sugerencia del RUT correcto cuando el DV es inválido

> Para cubrir las validaciones de la segunda parte del desafío, se incorporaron los campos **bono** y **descuentos** dentro de la carga de empleados.

### 🔹 Frontend

- Campos obligatorios
- Validación de formato de RUT/DNI
- Validación de salario mínimo
- Validación de bono y descuentos
- Mensajes dinámicos sin recargar la página

---

## 🧠 Consideraciones técnicas

- Arquitectura por capas (DAO / Service / Servlet)
- Manejo de errores con HTTP 400 + JSON estructurado
- Uso de @ConfigurationProperties para reglas de negocio configurables
- Uso de JDBC con PreparedStatement para prevenir SQL Injection
- Obtención del ID generado en el INSERT
- Uso de AJAX con Fetch API sin frameworks frontend

---

## 👤 Autor

- Nombre: Jonnathan Toro Jara
- Email: jonnathan.toro@live.com
- Cargo postulante: Desarrollador Java
- Empresa reclutadora: Tecnova
