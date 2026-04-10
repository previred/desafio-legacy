# 👥 Sistema de Gestión de Empleados

> **Desafío Técnico** — Aplicación full-stack construida con **Java 8**, **Servlets nativos** y **JDBC puro**, utilizando Spring Boot **únicamente como runtime** (contenedor embebido).

![Java](https://img.shields.io/badge/Java-8-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.18-brightgreen?style=flat-square&logo=springboot)
![H2](https://img.shields.io/badge/Database-H2%20(In--Memory)-blue?style=flat-square)
![Docker](https://img.shields.io/badge/Docker-Multi--Stage-2496ED?style=flat-square&logo=docker)

---

## 📋 Descripción

Sistema CRUD para la gestión de empleados que demuestra dominio de los **fundamentos de Java** sin depender de abstracciones de alto nivel como Spring Web MVC o Spring Data JPA.

### ¿Qué lo hace diferente?

| Concepto                | Lo que **NO** se usa       | Lo que **SÍ** se usa                       |
|-------------------------|----------------------------|--------------------------------------------|
| Controladores           | `@RestController`          | `HttpServlet` + `ServletRegistrationBean`  |
| Acceso a datos          | `JpaRepository`            | `Connection` + `PreparedStatement` (JDBC)  |
| Serialización JSON      | `@ResponseBody` automático | `ObjectMapper` de Jackson (manual)         |
| Servidor                | Spring Web MVC             | Spring Boot Embedded Tomcat (solo runtime) |

---

## 🛠️ Requisitos Previos

| Herramienta | Versión Mínima | Enlace                                     |
|-------------|----------------|--------------------------------------------|
| Java JDK    | 8              | https://adoptium.net/                      |
| Maven       | 3.6+           | https://maven.apache.org/download.cgi      |
| Docker      | 20+            | https://www.docker.com/get-started (opcional) |

---

## 🚀 Instrucciones de Ejecución

### Opción 1: Ejecución Local (Maven)

```bash
# Clonar el repositorio
git clone <url-del-repositorio>
cd desafioTecnico

# Compilar el proyecto
mvn clean package -DskipTests

# Ejecutar la aplicación
java -jar target/gestion-empleados-1.0.0.jar
```

La aplicación estará disponible en: **http://localhost:8080**

### Opción 2: Ejecución con Docker

```bash
# Construir la imagen
docker build -t gestion-empleados .

# Ejecutar el contenedor
docker run -d -p 8080:8080 --name empleados-app gestion-empleados

# Ver logs
docker logs -f empleados-app
```

### Opción 3: Ejecución con Maven (modo desarrollo)

```bash
mvn spring-boot:run
```

---

## 🌐 Endpoints de la API

| Método   | Ruta                    | Descripción                     |
|----------|-------------------------|---------------------------------|
| `GET`    | `/api/empleados/`       | Lista todos los empleados       |
| `GET`    | `/api/empleados/{id}`   | Obtiene un empleado por ID      |
| `POST`   | `/api/empleados/`       | Crea un nuevo empleado          |
| `DELETE` | `/api/empleados/{id}`   | Elimina un empleado por ID      |

### Ejemplo de Solicitud POST

```json
{
  "nombre": "Juan",
  "apellido": "Pérez",
  "rut": "12.345.678-9",
  "cargo": "Desarrollador Senior",
  "salarioBase": 850000,
  "bonificaciones": 120000,
  "descuentos": 95000
}
```

### Ejemplo de Respuesta con Errores (HTTP 400)

```json
{
  "status": 400,
  "mensaje": "Errores de validación",
  "errores": [
    "El salario base debe ser al menos $400,000. Valor recibido: $300,000.",
    "Ya existe un empleado con el RUT/DNI '12.345.678-9'."
  ]
}
```

---

## 🏗️ Decisiones de Arquitectura

### Principios SOLID Aplicados

| Principio | Aplicación en el Proyecto |
|-----------|---------------------------|
| **S** — Single Responsibility | Cada clase tiene una única razón de cambio: `EmpleadoServlet` (HTTP), `EmpleadoService` (negocio), `EmpleadoRepository` (datos). |
| **O** — Open/Closed | El repositorio se puede extender para diferentes bases de datos sin modificar el servicio. |
| **L** — Liskov Substitution | Se usan DTOs como contratos; la capa de presentación no depende del modelo de persistencia. |
| **I** — Interface Segregation | Cada capa expone solo los métodos necesarios para su consumidor. |
| **D** — Dependency Inversion | El servicio depende de la abstracción del repositorio (inyección por constructor). |

### Patrón DAO (Data Access Object)

`EmpleadoRepository` implementa el patrón DAO para encapsular toda la lógica de acceso a datos:

- **`findAll()`** — Consulta todos los registros.
- **`findById(Long id)`** — Búsqueda por clave primaria con `Optional`.
- **`save(Empleado)`** — Inserción con recuperación de ID generado.
- **`deleteById(Long id)`** — Eliminación con verificación de éxito.
- **`existsByRut(String rut)`** — Validación de unicidad.

### Separación de Capas

```
┌─────────────────────────────────────────────────┐
│  Frontend (index.html)                          │
│  HTML + CSS + Fetch API                         │
├─────────────────────────────────────────────────┤
│  Controller (EmpleadoServlet)                   │
│  HttpServlet nativo — manejo de HTTP y JSON     │
├─────────────────────────────────────────────────┤
│  Service (EmpleadoService)                      │
│  Lógica de negocio + Validaciones               │
├─────────────────────────────────────────────────┤
│  Repository (EmpleadoRepository)                │
│  JDBC puro — Connection, PreparedStatement      │
├─────────────────────────────────────────────────┤
│  Database (H2 In-Memory)                        │
│  Tabla: empleados                               │
└─────────────────────────────────────────────────┘
```

---

## ✅ Reglas de Negocio Implementadas

| # | Regla | Detalle |
|---|-------|---------|
| 1 | **Salario mínimo** | El salario base debe ser ≥ $400,000 CLP |
| 2 | **Límite de bonificaciones** | Las bonificaciones deben ser < 50% del salario base |
| 3 | **Límite de descuentos** | Los descuentos deben ser < salario base |
| 4 | **RUT/DNI único** | No puede existir más de un empleado con el mismo RUT |
| 5 | **Campos obligatorios** | Nombre, Apellido, RUT, Cargo y Salario son requeridos |
| 6 | **Valores no negativos** | Bonificaciones y descuentos no pueden ser negativos |

Las validaciones se aplican en **doble capa**:
- **Frontend:** Validación inmediata en JavaScript antes de enviar la solicitud.
- **Backend:** Validación en `EmpleadoService` antes de persistir en base de datos.

---

## 📁 Estructura del Proyecto

```
desafioTecnico/
├── pom.xml                                    # Configuración Maven
├── Dockerfile                                 # Docker multi-stage (build + runtime)
├── .dockerignore                              # Archivos excluidos del build Docker
├── README.md                                  # Documentación del proyecto
└── src/main/
    ├── java/com/desafio/
    │   ├── EmpleadoApplication.java           # Punto de entrada Spring Boot
    │   ├── config/
    │   │   └── ServletConfig.java             # Registro del Servlet + ObjectMapper
    │   ├── controller/
    │   │   └── EmpleadoServlet.java           # HttpServlet nativo (GET/POST/DELETE)
    │   ├── service/
    │   │   └── EmpleadoService.java           # Lógica de negocio + validaciones
    │   ├── repository/
    │   │   └── EmpleadoRepository.java        # JDBC puro (DAO Pattern)
    │   ├── model/
    │   │   └── Empleado.java                  # Entidad de dominio
    │   └── dto/
    │       ├── EmpleadoDTO.java               # Data Transfer Object
    │       └── ErrorResponse.java             # Respuesta de error estandarizada
    └── resources/
        ├── application.properties             # Configuración (H2, puerto, etc.)
        ├── schema.sql                         # DDL de la tabla empleados
        ├── data.sql                           # Datos de prueba iniciales
        └── static/
            └── index.html                     # Frontend completo (HTML/CSS/JS)
```

---

## 🔧 Herramientas Adicionales

- **Consola H2:** http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:empleadosdb`
  - Usuario: `sa`
  - Contraseña: *(vacía)*

---

## 📝 Tecnologías Utilizadas

- **Java 8** — Streams, Lambdas, Optional, Try-with-resources
- **Spring Boot 2.7.18** — Solo como runtime (contenedor Tomcat embebido)
- **HttpServlet** — Controlador nativo de Jakarta/Javax Servlet API
- **JDBC** — Acceso a datos con Connection, PreparedStatement
- **H2 Database** — Base de datos relacional en memoria
- **Jackson** — Serialización/deserialización JSON
- **HTML5 + CSS3 + JavaScript** — Frontend con Fetch API nativa
- **Docker** — Contenedorización multi-stage

---

*Desarrollado como desafío técnico para demostrar dominio de los fundamentos de Java y arquitectura de software.*

