# 📌 DemoPrevired - API de Empleados

## 📖 Descripción

Proyecto backend desarrollado con **Spring Boot** que expone una API REST para la gestión de empleados.

Permite realizar operaciones Crear, Leer y Eliminar sobre empleados mediante el endpoint:

```
/api/empleados
```

---

## 🚀 Tecnologías utilizadas

* Java 17
* Spring Boot
* Spring Data JPA
* H2 Database (en memoria)
* Maven
* Lombok

---

## ⚙️ Configuración del proyecto

### 🔧 Requisitos

* Java 17+
* Maven 3+

---

### ▶️ Ejecutar la aplicación

```bash
mvn spring-boot:run
```

La aplicación se ejecutará en:

```
http://localhost:8080
```

---

## 🗄️ Base de datos H2

Accede a la consola H2:

```
http://localhost:8080/h2-console
```

### 🔑 Configuración:

* **JDBC URL:** `jdbc:h2:mem:testdb`
* **User:** `sa`
* **Password:** (vacío)

---

## 📌 Endpoints disponibles

### 📍 Obtener todos los empleados

```http
GET http://localhost:8080/api/empleados
```

---

### 📍 Crear empleado

```http
POST http://localhost:8080/api/empleados
```

#### Body ejemplo:

```json
{
  "nombre":"test",
  "apellido":"test2",
  "rut":"12345678-5",
  "cargo":"dev",
  "salario":300000,
  "bonos":200000,
  "descuentos":700000
}
```

---

### 📍 Eliminar empleado

```http
DELETE http://localhost:8080/api/empleados/{id}
```

---

## ✔️ Validaciones

El sistema valida:

* Campos obligatorios
* Formato de RUT
* Datos numéricos (salario)

---

## 🧪 Testing

Para ejecutar los tests:

```bash
mvn test
```

---

## 📂 Estructura del proyecto

```
src/
 ├── config/
 ├── domain/
 ├── entity/
 ├── exception/
 ├── mapper/
 ├── repository/
 ├── service/
 └── servlet/
```

---

## 📌 Notas

* La base de datos es en memoria, por lo que los datos se pierden al reiniciar la aplicación.
* Ideal para pruebas y desarrollo.

---

## 👨‍💻 Autor

Proyecto desarrollado como prueba técnica.
Rodrigo Sanchez
