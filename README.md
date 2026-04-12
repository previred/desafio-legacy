# 🧩 Empleados App - Full Stack (Frontend + Backend)

## 📌 Descripción General

**Empleados App** es una aplicación full stack desarrollada como prueba técnica, cuyo objetivo es la gestión de empleados mediante una arquitectura separada en frontend y backend.

El sistema permite realizar operaciones CRUD (listar, crear y eliminar empleados), aplicando buenas prácticas de desarrollo, validaciones de negocio y consumo de API REST mediante AJAX.

---

## 🧱 Arquitectura del Sistema

### 🔹 Backend (Java 8 + Servlets + JDBC)

- API RESTful expuesta en `/api/empleados`
- Arquitectura en capas:
  - Controller
  - Service
  - Repository
  - Entity / DTO
  - Validator
  - Exception
- Base de datos en memoria H2
- Documentación con OpenAPI (Swagger)

### 🔹 Frontend (HTML + JavaScript + Fetch API)

- Interfaz web sin frameworks
- Consumo del backend mediante Fetch API (AJAX)
- Renderizado dinámico sin recarga de página
- Manejo de estado en el navegador

---

## 🚀 Tecnologías Utilizadas

### Backend
- Java 8
- Servlets
- JDBC
- H2 Database
- JSON manual mapping
- OpenAPI (Swagger)

### Frontend
- HTML5
- CSS3
- JavaScript (ES6+)
- Fetch API (AJAX)

---

## ⚙️ Funcionalidades Principales

### 📋 Empleados

- Listar empleados en tabla dinámica
- Crear nuevos empleados
- Eliminar empleados por ID
- Visualización de salario neto calculado

---

## 🔗 Endpoints del Backend

### 📍 Base URL

/api/empleados

---

### 📌 GET - Obtener empleados

GET /api/empleados

**Respuesta:**
``` json
[
  {
    "id": 1,
    "nombre": "Juan",
    "apellido": "Pérez",
    "rut": "12345678-9",
    "cargo": "Developer",
    "salarioBase": 800000,
    "bono": 100000,
    "descuentos": 50000,
    "valorNeto": 850000
  }
]

```
---

### 📌 POST - Crear empleado

POST /api/empleados
Content-Type: application/json

**Request:**
``` json
{
  "nombre": "Ana",
  "apellido": "Gómez",
  "rut": "98765432-1",
  "cargo": "QA",
  "salarioBase": 700000,
  "bono": 50000,
  "descuentos": 20000
}

```
**Respuesta:**
``` json
{
  "mensaje": "Empleado creado correctamente"
}
```

---

### 📌 DELETE - Eliminar empleado

DELETE /api/empleados?id=1

**Respuesta:**
``` json
{
  "mensaje": "Empleado eliminado correctamente"
}
```

---

## 📦 Modelo de Datos

### 🔹 Campos del empleado

- id (autogenerado)
- nombre
- apellido
- rut / dni
- cargo
- salarioBase
- bono (opcional)
- descuentos (opcional)
- valorNeto (calculado)

---

## ⚠️ Validaciones

### 🔹 Frontend

- Campos obligatorios
- Validación de formato RUT/DNI
- Salario base mínimo: 400,000
- Mensajes de error dinámicos en UI

### 🔹 Backend

- RUT/DNI no duplicado
- Salario base >= 400,000
- Bono <= 50% del salario base
- Descuentos <= salario base

📌 En caso de error:
- HTTP 400 (Bad Request)
- JSON con detalle del error

---

## 🧠 Decisiones Técnicas

### 🔹 Arquitectura en capas

- Controller → manejo HTTP
- Service → lógica de negocio
- Repository → acceso a datos
- DTO vs Entity → separación de modelo interno y externo

### 🔹 Frontend sin frameworks

- JavaScript puro
- Fetch API para comunicación HTTP
- Render dinámico sin recarga

### 🔹 Campos adicionales

- bono
- descuentos

✔ Justificación:
Permiten enriquecer la lógica de negocio.

---

## 💡 Cálculo del Valor Neto

Valor Neto = Salario Base + Bono - Descuentos

✔ Importancia:
- Representa ingreso real
- Centraliza lógica en backend
- Evita inconsistencias

---

## 📘 Documentación API (OpenAPI)

https://editor.swagger.io/

---

## ⚙️ Instrucciones de Ejecución

### Backend
git clone https://github.com/cpalacios1005900-sketch/desafio-legacy.git

Deploy en Tomcat:
http://localhost:8080/api/empleados

### Frontend
Live Server:
http://127.0.0.1:5500

---

## 🎯 Conclusión

Proyecto full stack con buenas prácticas, arquitectura limpia y consumo de API REST.

nombre: Cristian Palacios
empresa:Tecmova
correo : cristian.palacios08@hotmail.com
cargo : Desarrollador full stack