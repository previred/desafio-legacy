# 🧩 Empleados App - Backend API

## 📌 Descripción General

Aplicación backend desarrollada como parte de una prueba técnica, cuyo objetivo es exponer un API RESTful para la gestión de empleados.

El sistema permite realizar operaciones básicas como listar, crear y eliminar empleados, aplicando validaciones de negocio, buenas prácticas de arquitectura en capas y manejo adecuado de errores.

La solución fue construida utilizando tecnologías base del ecosistema Java sin el uso de frameworks externos, priorizando el control manual del flujo de la aplicación y la claridad en la implementación.

---

## 🚀 Tecnologías Utilizadas

- Java 8
- Servlets (API REST)
- JDBC
- Base de datos en memoria H2
- JSON (manejo manual / mapeo de propiedades)
- AJAX (consumo desde frontend)
- OpenAPI (Swagger) para documentación

---

## 🧱 Arquitectura

El proyecto sigue una arquitectura en capas bien definida:

controller/
service/
repository/
model/
entity/
exception/
validator/
config/

### 🔹 Capas

- **Controller**: Manejo de solicitudes HTTP y respuestas JSON.
- **Service**: Contiene la lógica de negocio.
- **Repository**: Acceso a datos mediante JDBC.
- **Entity**: Representación de la base de datos.
- **Model (DTO)**: Objeto de transferencia expuesto en el API.
- **Validator**: Validaciones de negocio.
- **Exception**: Manejo centralizado de errores.

### 🔹 Separación Entity vs Model

Se implementa una separación clara entre:

- **Entity** → Representa la estructura en base de datos.
- **Model (DTO)** → Representa la respuesta al cliente.

Esto permite desacoplar la persistencia de la representación externa.

---

## 🔗 Endpoints

### 📍 Base URL

/api/empleados

### 📌 GET - Obtener empleados

GET /api/empleados

**Respuesta:**

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

---

### 📌 POST - Crear empleado

POST /api/empleados
Content-Type: application/json

**Request:**

{
  "nombre": "Ana",
  "apellido": "Gómez",
  "rut": "98765432-1",
  "cargo": "QA",
  "salarioBase": 700000,
  "bono": 50000,
  "descuentos": 20000
}

**Respuesta:**

{
  "mensaje": "Empleado creado correctamente"
}

---

### 📌 DELETE - Eliminar empleado

DELETE /api/empleados?id=1

**Respuesta:**

{
  "mensaje": "Empleado eliminado correctamente"
}

---

## 📦 Modelo de Datos

### 🔹 Campos del empleado

- id (autogenerado)
- nombre
- apellido
- rut
- cargo
- salarioBase
- bono (opcional)
- descuentos (opcional)
- valorNeto (calculado)

---

## ⚠️ Validaciones de Negocio

- No permitir RUT/DNI duplicado
- Salario base menor a 400000
- Bono mayor al 50% del salario base
- Descuentos mayores al salario base

### 📌 Manejo de errores

HTTP 400 con detalle:

{
  "errors": [
    "El salario base debe ser mayor o igual a 400000"
  ]
}

---

## 🧠 Decisiones Técnicas

### 🔹 Campos adicionales

- bono
- descuentos

### 🔹 Cálculo

Valor Neto = Salario Base + Bono - Descuentos

---

## 📘 Documentación API (OpenAPI)

Acceso:
/src/main/resources/openapi.yaml

---

## ⚙️ Instrucciones de Ejecución

git clone https://github.com/TU-USUARIO/desafio-legacy.git
cd desafio-legacy

Levantar servidor (Tomcat)

http://localhost:8080/api/empleados

---

## 🎯 Conclusión

Backend robusto, limpio y alineado a buenas prácticas.
