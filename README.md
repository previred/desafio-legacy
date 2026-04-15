# Desafío Técnico - API de Empleados

## Descripción

Aplicación web desarrollada en Java que permite gestionar empleados mediante un API REST implementada con **Servlets**, utilizando **Spring Boot como runtime** y **Apache Tomcat embebido**.

Incluye operaciones CRUD básicas, validaciones de negocio y una interfaz web simple que consume el API mediante **AJAX (Fetch API)** sin recargar la página.

---

## Tecnologías utilizadas

* Java 17 (compatible con Java 8+)
* Spring Boot (solo como runtime)
* Apache Tomcat embebido
* JDBC
* H2 Database (en memoria)
* Maven
* HTML + JavaScript (Fetch API)

---

## Estructura del proyecto

```
com.desafio
├── servlet        # Controladores HTTP (Servlets)
├── service        # Lógica de negocio
├── repository     # Acceso a datos (JDBC)
├── model          # Entidades
├── util           # Utilidades (DB, configuración)
```

---

## Cómo ejecutar el proyecto

### 1. Ejecutar la aplicación

Desde IntelliJ o con Maven:

```bash
mvn spring-boot:run
```

### 2. Acceder a la aplicación

* API:

  ```
  http://localhost:8080/api/empleados
  ```

* Interfaz web:

  ```
  http://localhost:8080/index.html
  ```

* Consola H2 (opcional):

  ```
  http://localhost:8080/h2-console
  ```

Configuración H2:

* JDBC URL: `jdbc:h2:mem:testdb`
* User: `sa`
* Password: (vacío)

---

## Endpoints disponibles

### GET /api/empleados

Obtiene la lista de empleados.

**Respuesta:**

```json
[ 
  { 
    "id": 1, 
    "nombre": "Juan", 
    "apellido": "Perez", 
    "dni": "12345678", 
    "cargo": "Dev", 
    "salario": 500000, 
    "bono": 50000, 
    "descuentos": 20000
  } 
]
```

---

### POST /api/empleados

Crea un nuevo empleado.

**Request:**

```json
{
  "nombre": "Juan",
  "apellido": "Perez",
  "dni": "12345678",
  "cargo": "Dev",
  "salario": 500000,
  "bono": 50000, 
  "descuentos": 20000
}
```

---

### DELETE /api/empleados?id=1

Elimina un empleado por ID.

---

## Validaciones de negocio (Backend)

* No se permite DNI duplicado
* Salario mínimo: $400,000
* El bono no puede superar el 50% del salario base
* El total de descuentos no puede ser mayor al salario base
* Se retorna HTTP 400 en caso de error

**Ejemplo de error:**

```json
{
  "error": "DNI duplicado"
}
```

---

## Validaciones (Frontend)

* Campos obligatorios
* Validación de formato DNI (8 dígitos)
* Validación de salario mínimo
* Visualización de errores en pantalla (sin alert)

---

## Autor

* Nombre: Rodolfo Crisanto
* Empresa: Sermaluc
* Cargo al que postula: Desarrolador Backend Java

---
