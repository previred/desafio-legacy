# 🧩 Empleados App - Full Stack (Frontend + Backend)

## 📌 Descripción General

**Empleados App** es una aplicación full stack desarrollada como prueba técnica, cuyo objetivo es la gestión de empleados mediante una arquitectura separada en frontend y backend.

El sistema permite realizar operaciones CRUD (listar, crear y eliminar empleados), aplicando buenas prácticas de desarrollo, validaciones de negocio y consumo de API REST mediante AJAX.

---

## 🧱 Arquitectura del Sistema

### 🔹 Backend (Java 8 + Servlets + JDBC)

* API RESTful expuesta en `/api/empleados`
* Arquitectura en capas:

  * Controller
  * Service
  * Repository
  * Entity / DTO
  * Validator
  * Exception
* Base de datos en memoria H2
* Documentación con OpenAPI (Swagger)

### 🔹 Frontend (HTML + JavaScript + Fetch API)

* Interfaz web sin frameworks
* Consumo del backend mediante Fetch API (AJAX)
* Renderizado dinámico sin recarga de página
* Manejo de estado en el navegador

---

## 🚀 Tecnologías Utilizadas

### Backend

* Java 8
* Servlets
* JDBC
* H2 Database
* JSON manual mapping
* OpenAPI (Swagger)

### Frontend

* HTML5
* CSS3
* JavaScript (ES6+)
* Fetch API (AJAX)

---

## ⚙️ Funcionalidades Principales

* Listar empleados en tabla dinámica
* Crear nuevos empleados
* Eliminar empleados por ID
* Visualización de salario neto calculado

---

## 🔗 Endpoints del Backend

### 📍 Base URL

```
/api/empleados
```

### 📌 GET - Obtener empleados

```
GET /api/empleados
```

### 📌 POST - Crear empleado

```
POST /api/empleados
Content-Type: application/json
```

### 📌 DELETE - Eliminar empleado

```
DELETE /api/empleados?id=1
```

---

## 📦 Modelo de Datos

* id (autogenerado)
* nombre
* apellido
* rut / dni
* cargo
* salarioBase
* bono (opcional)
* descuentos (opcional)
* valorNeto (calculado)

---

## ⚠️ Validaciones

### 🔹 Frontend

* Campos obligatorios
* Validación de formato RUT/DNI
* Salario base mínimo: 400,000
* Mensajes de error dinámicos en UI

### 🔹 Backend

* RUT/DNI no duplicado
* Salario base >= 400,000
* Bono <= 50% del salario base
* Descuentos <= salario base

---

## 🧠 Cálculo del Valor Neto

```
Valor Neto = Salario Base + Bono - Descuentos
```

---

## ⚙️ INSTRUCCIONES DE INSTALACIÓN Y EJECUCIÓN (DETALLADO)

# 🖥️ 1. REQUISITOS PREVIOS

Antes de iniciar asegúrate de tener instalado:

* Java 8 o superior
* Maven
* Apache Tomcat 8 o 9
* Git
* Navegador web
* Visual Studio Code (para frontend)
* Extensión **Live Server** en VS Code

---

# 🧠 2. CLONAR EL PROYECTO

Abre una terminal y ejecuta:

```bash
git clone https://github.com/cpalacios1005900-sketch/desafio-legacy.git
```

Luego entra al proyecto:

```bash
cd desafio-legacy
```

---

# ⚙️ 3. EJECUTAR EL BACKEND (JAVA + TOMCAT)

## 📌 Opción: Desde IDE ()

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

# 🌐 4. EJECUTAR EL FRONTEND (LIVE SERVER)

## 📌 IMPORTANTE

El frontend **NO usa frameworks**, solo HTML + JS.

---

## 📁 Estructura del frontend

Ubica la carpeta:

```
/frontend
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
frontend
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

## ⚠️ IMPORTANTE (CONEXIÓN FRONT + BACK)

Verifica en el frontend que la URL del backend sea:

```javascript
http://localhost:8080/api/empleados
```

Si cambia el puerto de Tomcat, actualizar en JS.

---

# 📘 DOCUMENTACIÓN API (SWAGGER)

[https://editor.swagger.io/](https://editor.swagger.io/)

---

# 🎯 CONCLUSIÓN

Proyecto full stack con arquitectura limpia, validaciones robustas y consumo de API REST sin frameworks.

---

## 👨‍💻 Autor

* Nombre: Cristian Palacios
* Empresa: Tecmova
* Correo: [cristian.palacios08@hotmail.com](mailto:cristian.palacios08@hotmail.com)
* Cargo: Desarrollador Full Stack
