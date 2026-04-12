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
existen algunas validaciones con estos campos; se decidió agregarlos de manera no obligatoria para cumplir con la validación. Permiten enriquecer la lógica de negocio.

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

## ⚙️ Instrucciones De Instalación Y Ejecución

## 🖥️ 1. Requisitos Previos

Antes de iniciar asegúrate de tener instalado:

* Java 8 o superior
* Maven
* Apache Tomcat 8 o 9
* Git
* Navegador web
* Visual Studio Code (para frontend)
* Extensión **Live Server** en VS Code

---

## 🧠 2. Clonar El Proyecto

Abre una terminal y ejecuta:

```bash
git clone https://github.com/cpalacios1005900-sketch/desafio-legacy.git
```

Luego entra al proyecto:

```bash
cd desafio-legacy
```

---

## ⚙️ 3. Ejecutar El Backend (Java + Tomcat)

## 📌 Opción: Desde IDE

1. Abre el proyecto en IntelliJ IDEA o Eclipse
2. Importa como proyecto Maven o Dynamic Web Project
3. Verifica Java 8 en configuración del proyecto
4. Ejecuta el servidor Tomcat desde el IDE

---


5. Verifica backend:

```
http://localhost:8080/api/empleados
```

---

## 🌐 4.Ejecutar El Frontend (Live Server)

## 📌 IMPORTANTE

El frontend **NO usa frameworks**, solo HTML + JS.

---

## 📁 Estructura del frontend

Ubica la carpeta:

```
/empleados-app-front
```

Dentro debe existir:

```
index.html
js/
css/
```

---

## 🚀 EJECUCIÓN CON LIVE SERVER

### 🔹 Paso 1

Abre VS Code

### 🔹 Paso 2

Abre la carpeta:

```
empleados-app-front
```

### 🔹 Paso 3

Abre el archivo:

```
index.html
```

### 🔹 Paso 4

Click derecho sobre `index.html`

Selecciona:

```
👉 Open with Live Server
```

---

## 🌍 Resultado esperado

El navegador abrirá algo como:

```
http://127.0.0.1:5500/index.html
```

---

## ⚠️ Importante (Conexión Front + Back)

Verifica en el frontend que la URL del backend sea:

```javascript
http://localhost:8080/api/empleados
```

Si cambia el puerto de Tomcat, actualizar en JS.

---

# 🎯 CONCLUSIÓN

Proyecto full stack con arquitectura limpia, validaciones robustas y consumo de API REST sin frameworks.

---

## 👨‍💻 Autor

* Nombre: Cristian Palacios
* Empresa: Tecmova
* Correo: [cristian.palacios08@hotmail.com](mailto:cristian.palacios08@hotmail.com)
* Cargo: Desarrollador Full Stack
