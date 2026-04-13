# Desafío Técnico: Gestión de Empleados (Servlets + AJAX)

## 📌 Descripción

Aplicación web para la gestión de empleados que permite:

- Listar empleados
- Crear nuevos empleados
- Eliminar empleados

La aplicación está construida utilizando:

- Java 17
- Spring Boot (como runtime)
- Servlets (sin usar @RestController)
- JDBC puro
- Base de datos en memoria H2
- Frontend con HTML, CSS y JavaScript (Fetch API - AJAX)

---

## ⚙️ Tecnologías utilizadas

- Java 17
- Spring Boot
- Servlets (Jakarta)
- JDBC
- H2 Database (in-memory)
- Maven
- HTML + CSS + JavaScript
- Fetch API (AJAX)

---

## 🚀 Cómo ejecutar la aplicación

### 1. Clonar el repositorio

```bash
git clone https://github.com/rodrisepulveda/desafio-legacy.git

cd desafio-legacy

git checkout solucion-rodrigo
```

---

### 2. Ejecutar la aplicación

Linux / Mac:

```bash
./mvnw spring-boot:run
```

Windows:

```bash
mvnw.cmd spring-boot:run
```

---

### 3. Acceder a la aplicación

Frontend:

http://localhost:8080/

---

## 🗄️ Consola H2 (opcional)

Para inspeccionar la base de datos:

http://localhost:8080/h2-console

Configuración:

- JDBC URL: jdbc:h2:mem:employeesdb
- User: sa
- Password: (vacío)

---

## 📡 API

### 🔹 POST /api/empleados

Crea un nuevo empleado.

CURL:

```bash
curl -X POST http://localhost:8080/api/empleados \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Pedro",
    "lastName": "Rojas",
    "documentNumber": "12345678-9",
    "position": "Desarrollador",
    "compensation": {
      "baseSalary": 700000,
      "bonus": 50000,
      "discounts": 10000
    }
  }'
```

---

### 🔹 GET /api/empleados

Obtiene la lista de empleados.

CURL:

```bash
curl -X GET http://localhost:8080/api/empleados
```

---

### 🔹 DELETE /api/empleados

Elimina un empleado por su ID.

CURL:

```bash
curl -X DELETE "http://localhost:8080/api/empleados?id=1"
```
---

## ⚠️ Validaciones

### 🔹 Backend

- RUT/DNI único
- Salario base ≥ 400.000
- Bono ≤ 50% del salario base
- Descuentos ≤ salario base

En caso de error, se retorna:

```bash
{
  "errors": [
    {
      "field": "campo",
      "message": "mensaje de error"
    }
  ]
}
```

---

### 🔹 Frontend

- Campos obligatorios
- Validación de formato RUT/DNI
- Salario base mínimo

---

## 🧠 Decisiones de diseño

- Separación en capas: Servlet → Service → Repository
- Uso de JDBC directo para cumplir requerimiento del desafío
- Uso de Fetch API para comunicación asincrónica (AJAX)

---

## 📁 Estructura del proyecto

```text
src/main/java
├── config
├── model
├── repository
├── service
├── servlet
└── validation

src/main/resources
├── application.yaml
├── schema.sql
└── static
    ├── index.html
    ├── css/
    └── js/
```

---

## 👤 Información del postulante

- Nombre: Rodrigo Sepúlveda
- Cargo: Desarrollador Backend Java
- Email: rodrisepulveda@gmail.com

---

## 📝 Nota

- La base de datos es en memoria, por lo que se reinicia en cada ejecución