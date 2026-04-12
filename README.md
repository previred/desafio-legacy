# Sistema de Gestion de Empleados

Aplicacion web CRUD para gestion de empleados desarrollada con **Java 8+**, **Spring Boot 2.7**, **Servlets**, **JDBC (JdbcTemplate)**, **H2 en memoria** y frontend en **HTML + JavaScript Vanilla** con **Fetch API (AJAX)**.

Arquitectura de 3 capas (Controller / Service / Repository), patron DTO con modelos generados desde contrato OpenAPI (contract-first), validaciones en backend y frontend, y documentacion Swagger UI.

---

## Requisitos Previos

| Herramienta | Version minima | Verificar instalacion        |
|-------------|----------------|------------------------------|
| **JDK**     | 8+             | `java -version`              |
| **Maven**   | 3.6+           | `mvn -version`               |

> **Nota:** El proyecto se compila con target Java 8 pero es compatible con JDK 11, 17 y 21.

---

## Inicio Rapido

### 1. Clonar el repositorio

```bash
git clone <url-del-repositorio>
cd empleados-app
```

### 2. Compilar y generar modelos OpenAPI

```bash
mvn clean package -DskipTests
```

> **IMPORTANTE:** Este paso es obligatorio antes de ejecutar la aplicacion o importar el proyecto en un IDE. El plugin `openapi-generator-maven-plugin` genera las clases de modelos y la interfaz API en `target/generated-sources/openapi/` durante la fase `generate-sources`. Sin este paso, las clases `EmpleadoResponse`, `ErrorResponse`, `MensajeResponse` y `EmpleadosApi` no existiran y la compilacion fallara.

### 3. Ejecutar la aplicacion

```bash
mvn spring-boot:run
```

La aplicacion estara disponible en: **http://localhost:8080**

### 4. URLs disponibles

| Recurso                | URL                                        |
|------------------------|--------------------------------------------|
| **Frontend**           | http://localhost:8080                       |
| **API REST**           | http://localhost:8080/api/empleados         |
| **Swagger UI**         | http://localhost:8080/swagger-ui/index.html |
| **Contrato OpenAPI**   | http://localhost:8080/openapi/empleados-api.yaml |



---

## Ejecutar Tests

```bash
# Todos los tests (18 total: 8 unitarios + 10 integracion)
mvn test

# Solo tests unitarios del servicio
mvn test -Dtest=EmpleadoServiceTest

# Solo tests de integracion del servlet
mvn test -Dtest=EmpleadoServletTest
```

| Suite                    | Cantidad | Tipo        | Descripcion                                      |
|--------------------------|----------|-------------|--------------------------------------------------|
| `EmpleadoServiceTest`    | 8        | Unitario    | Mockito: logica de negocio aislada del repositorio |
| `EmpleadoServletTest`    | 10       | Integracion | TestRestTemplate: HTTP real contra servidor embebido |

---

## Stack Tecnologico

| Componente               | Tecnologia              | Version   | Proposito                                    |
|--------------------------|-------------------------|-----------|----------------------------------------------|
| Lenguaje                 | Java                    | 8+        | Codigo fuente                                |
| Runtime                  | Spring Boot             | 2.7.18    | Contenedor Tomcat embebido + auto-configuracion |
| Capa Web                 | HttpServlet             | javax     | Endpoints REST (no @RestController)          |
| Acceso a Datos           | JdbcTemplate            | Spring 5  | Consultas SQL directas (no JPA)              |
| Base de Datos            | H2                      | 2.2.220   | BD relacional en memoria                     |
| Validacion               | Bean Validation (JSR-380) | javax   | Validaciones via anotaciones                 |
| Serializacion JSON       | Jackson                 | 2.13.x    | ObjectMapper para request/response           |
| Boilerplate              | Lombok                  | 1.18.30   | @Data, @Builder, @Slf4j, @RequiredArgsConstructor |
| Contrato API             | OpenAPI Generator       | 6.6.0     | Genera interfaces y modelos desde YAML       |
| Documentacion API        | springdoc-openapi-ui    | 1.8.0     | Swagger UI sirviendo spec YAML estatico      |
| Anotaciones Swagger      | swagger-annotations     | 2.2.9     | @Schema, @Operation en codigo generado       |
| YAML Parser              | SnakeYAML               | 2.2       | Override para mitigar CVE-2022-1471          |
| Testing                  | JUnit 5 + Mockito       | 5.8.x     | Tests unitarios y de integracion             |
| Frontend                 | HTML5 + CSS3 + JS Vanilla | ES5     | Sin frameworks externos                      |
| Alertas UI               | SweetAlert2             | 11.x      | Dialogos de confirmacion y notificaciones    |

---

## Arquitectura

### Patron: 3 Capas + Contract-First API

```
                    +-------------------+
                    |   OpenAPI YAML    |  <-- Fuente de verdad del contrato
                    |  (empleados-api)  |
                    +--------+----------+
                             |
                    openapi-generator-maven-plugin
                             |
                             v
    +--------------------------------------------------------+
    |              GENERATED (target/)                       |
    |  EmpleadosApi.java  (interfaz con anotaciones)         |
    |  EmpleadoResponse / ErrorResponse / MensajeResponse    |
    +--------------------------------------------------------+
                             |
                             | usa modelos generados
                             v
    +--------------------------------------------------------+
    |  CONTROLLER (Servlet)                                  |
    |  EmpleadoServlet.java                                  |
    |  - doGet / doPost / doDelete                           |
    |  - Bean Validation con javax.validation.Validator      |
    |  - Serializa/deserializa con ObjectMapper              |
    +------------------------+-------------------------------+
                             |
                             v
    +--------------------------------------------------------+
    |  SERVICE (Logica de negocio)                           |
    |  EmpleadoService.java                                  |
    |  - Orquesta repository + mapper                        |
    |  - Valida RUT unico                                    |
    |  - Retorna DTOs generados, nunca entidades             |
    +------------------------+-------------------------------+
                             |
                             v
    +--------------------------------------------------------+
    |  REPOSITORY (Acceso a datos)                           |
    |  EmpleadoRepository.java                               |
    |  - JdbcTemplate (SQL directo)                          |
    |  - RowMapper manual                                    |
    |  - PreparedStatement + GeneratedKeyHolder              |
    +------------------------+-------------------------------+
                             |
                             v
                    +-------------------+
                    |   H2 en memoria   |
                    |  (empleadosdb)    |
                    +-------------------+
```

### Flujo de una peticion POST

```
Browser (Fetch API)
    |
    v
EmpleadoServlet.doPost()          -- Deserializa JSON, valida con Validator
    |
    v
EmpleadoService.crearEmpleado()   -- Valida RUT unico, delega persistencia
    |
    v
EmpleadoRepository.save()         -- INSERT via JdbcTemplate, retorna ID generado
    |
    v
EmpleadoMapper.toResponse()       -- Entity -> EmpleadoResponse (DTO generado)
    |
    v
EmpleadoServlet                   -- Serializa DTO a JSON, responde HTTP 201
    |
    v
Browser                           -- SweetAlert2 muestra exito, recarga tabla via GET
```

---

## Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/empleados/
│   │   ├── EmpleadosApplication.java           # Punto de entrada Spring Boot
│   │   ├── config/
│   │   │   ├── DatabaseConfig.java             # Crea tabla empleados via DDL al arranque
│   │   │   └── ServletConfig.java              # Registra EmpleadoServlet en /api/empleados/*
│   │   ├── controller/
│   │   │   └── EmpleadoServlet.java            # HttpServlet: doGet, doPost, doDelete
│   │   ├── exception/business/
│   │   │   └── RutDuplicadoException.java      # Excepcion cuando RUT ya existe
│   │   ├── mapper/
│   │   │   └── EmpleadoMapper.java             # Entity -> EmpleadoResponse + calcula liquido
│   │   ├── model/
│   │   │   └── Empleado.java                   # Entidad de dominio con Bean Validation
│   │   ├── repository/
│   │   │   └── EmpleadoRepository.java         # JDBC con JdbcTemplate (findAll, save, delete)
│   │   ├── service/
│   │   │   └── EmpleadoService.java            # Logica de negocio + validacion RUT unico
│   │   └── validation/
│   │       ├── EmpleadoValido.java             # Anotacion custom @EmpleadoValido
│   │       └── EmpleadoValidator.java          # Valida bono <= 50% salario, descuentos <= salario
│   └── resources/
│       ├── application.properties              # Configuracion JDBC, H2, logging, Swagger UI
│       ├── openapi/
│       │   └── empleados-api.yaml              # Contrato OpenAPI 3.0 (fuente de verdad)
│       └── static/
│           ├── index.html                      # HTML semantico del frontend
│           ├── css/
│           │   └── styles.css                  # Estilos corporativos (paleta tipo Previred)
│           └── js/
│               ├── utils.js                    # Funciones puras: formatMoney, escapeHtml
│               ├── ui.js                       # Manipulacion DOM: alertas, render tabla/cards
│               ├── validation.js               # Validaciones frontend (replica reglas backend)
│               ├── api.js                      # Fetch API: GET, POST, DELETE
│               └── app.js                      # Orquestador: init, eventos, flujo CRUD
├── test/
│   └── java/com/empleados/
│       ├── service/
│       │   └── EmpleadoServiceTest.java        # 8 tests unitarios con Mockito
│       └── servlet/
│           └── EmpleadoServletTest.java         # 10 tests integracion con TestRestTemplate
└── target/generated-sources/openapi/           # AUTO-GENERADO por mvn package
    └── src/main/java/com/empleados/api/generated/
        ├── EmpleadosApi.java                   # Interfaz con @Operation, @RequestMapping
        └── model/
            ├── EmpleadoRequest.java            # DTO de entrada con validaciones
            ├── EmpleadoResponse.java           # DTO de salida con salarioLiquido
            ├── ErrorResponse.java              # DTO de errores con lista de mensajes
            └── MensajeResponse.java            # DTO de mensaje simple
```

---

## Responsabilidades por Archivo

### Backend (Java)

| Archivo | Capa | Responsabilidad |
|---------|------|-----------------|
| `EmpleadosApplication.java` | - | Punto de entrada `main()`. Arranca Spring Boot. |
| `DatabaseConfig.java` | Config | `CommandLineRunner` que ejecuta `CREATE TABLE` via `JdbcTemplate` al inicio. |
| `ServletConfig.java` | Config | Registra `EmpleadoServlet` como bean Servlet en la ruta `/api/empleados/*`. Inyecta dependencias. |
| `EmpleadoServlet.java` | Controller | Extiende `HttpServlet`. Maneja `doGet` (listar), `doPost` (crear con validacion), `doDelete` (eliminar por ID). Serializa/deserializa JSON con Jackson. |
| `EmpleadoService.java` | Service | Logica de negocio. Valida RUT unico, orquesta repository y mapper. Retorna DTOs generados, nunca entidades. |
| `EmpleadoRepository.java` | Repository | Acceso a datos con `JdbcTemplate`. SQL directo con `RowMapper`, `PreparedStatement` y `GeneratedKeyHolder`. |
| `EmpleadoMapper.java` | Mapper | Convierte `Empleado` (entidad) a `EmpleadoResponse` (DTO generado). Calcula `salarioLiquido = salario + bono - descuentos`. |
| `Empleado.java` | Model | Entidad de dominio con Lombok (`@Data`, `@Builder`) y Bean Validation (`@NotBlank`, `@Pattern`, `@Min`, `@EmpleadoValido`). |
| `EmpleadoValido.java` | Validation | Anotacion custom a nivel de clase. Dispara `EmpleadoValidator`. |
| `EmpleadoValidator.java` | Validation | `ConstraintValidator` que valida reglas cross-field: bono <= 50% salario, descuentos <= salario. |
| `RutDuplicadoException.java` | Exception | `RuntimeException` lanzada cuando el RUT ya existe. Capturada en el Servlet para retornar HTTP 400. |

### Frontend (HTML/CSS/JS)

| Archivo | Responsabilidad |
|---------|-----------------|
| `index.html` | Estructura HTML semantica (`<header>`, `<main>`, `<section>`, `<footer>`). Solo markup, sin logica ni estilos inline. |
| `css/styles.css` | Estilos corporativos. Variables CSS, responsive (768px/480px breakpoints), cards mobile. |
| `js/utils.js` | Modulo `Utils`: funciones puras sin DOM (`formatMoney`, `escapeHtml`, `getFieldValue`, `getNumericValue`). |
| `js/ui.js` | Modulo `UI`: manipulacion DOM, SweetAlert2, render tabla/cards, errores de campo, estados loading/empty. |
| `js/validation.js` | Modulo `Validation`: validaciones frontend que replican las del backend (campos requeridos, formato RUT, limites monetarios). |
| `js/api.js` | Modulo `Api`: llamadas Fetch API al endpoint `/api/empleados` (GET, POST, DELETE). |
| `js/app.js` | Modulo `App`: orquestador. Inicializa eventos, coordina flujo CRUD entre Api, UI y Validation. |

### Codigo Generado (target/)

| Archivo | Generado desde | Proposito |
|---------|---------------|-----------|
| `EmpleadosApi.java` | paths en YAML | Interfaz con anotaciones `@Operation`, `@RequestMapping`. No se implementa directamente (el Servlet usa los modelos). |
| `EmpleadoRequest.java` | schema EmpleadoRequest | DTO de entrada con `@NotNull`, `@Size`, `@Pattern`, `@Min`. |
| `EmpleadoResponse.java` | schema EmpleadoResponse | DTO de salida con todos los campos + `salarioLiquido`. |
| `ErrorResponse.java` | schema ErrorResponse | DTO de error con campo `errores` (lista de strings). |
| `MensajeResponse.java` | schema MensajeResponse | DTO de mensaje simple con campo `mensaje`. |

---

## API REST

### Endpoints

| Metodo     | Ruta                    | Status | Request Body        | Response Body         |
|------------|-------------------------|--------|--------------------|-----------------------|
| **GET**    | `/api/empleados`        | 200    | -                  | `[EmpleadoResponse]`  |
| **POST**   | `/api/empleados`        | 201    | JSON (Empleado)    | `EmpleadoResponse`    |
| **POST**   | `/api/empleados`        | 400    | JSON invalido      | `ErrorResponse`       |
| **DELETE** | `/api/empleados/{id}`   | 200    | -                  | `MensajeResponse`     |
| **DELETE** | `/api/empleados/{id}`   | 400    | ID no numerico     | `ErrorResponse`       |
| **DELETE** | `/api/empleados/{id}`   | 404    | ID no existe       | `ErrorResponse`       |

### Ejemplo POST (crear empleado)

```bash
curl -X POST http://localhost:8080/api/empleados \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan",
    "apellido": "Perez",
    "rut": "12.345.678-9",
    "cargo": "Desarrollador",
    "salario": 800000,
    "bono": 200000,
    "descuentos": 50000
  }'
```

Respuesta (201):
```json
{
  "id": 1,
  "nombre": "Juan",
  "apellido": "Perez",
  "rut": "12.345.678-9",
  "cargo": "Desarrollador",
  "salario": 800000,
  "bono": 200000,
  "descuentos": 50000,
  "salarioLiquido": 950000
}
```

---

## Validaciones

### Backend (Bean Validation + Validador Custom)

| Regla | Anotacion/Clase | Campo | HTTP |
|-------|----------------|-------|------|
| Campos obligatorios | `@NotBlank` | nombre, apellido, rut, cargo | 400 |
| Formato RUT | `@Pattern(XX.XXX.XXX-X)` | rut | 400 |
| Salario minimo $400,000 | `@Min(400000)` | salario | 400 |
| Bono >= 0 | `@Min(0)` | bono | 400 |
| Descuentos >= 0 | `@Min(0)` | descuentos | 400 |
| Bono <= 50% salario | `EmpleadoValidator` | bono vs salario | 400 |
| Descuentos <= salario | `EmpleadoValidator` | descuentos vs salario | 400 |
| RUT unico | `EmpleadoService` | rut | 400 |

### Frontend (JavaScript)

Las mismas reglas se replican en `js/validation.js` para feedback inmediato al usuario antes del submit. La validacion backend es la autoritativa.

---

## OpenAPI - Generacion de Codigo (Contract-First)

El proyecto sigue el enfoque **contract-first**: el archivo `empleados-api.yaml` es la fuente de verdad y desde el se generan las clases Java.

### Proceso de generacion

```
src/main/resources/openapi/empleados-api.yaml
           |
           | openapi-generator-maven-plugin (fase generate-sources)
           |
           v
target/generated-sources/openapi/src/main/java/com/empleados/api/generated/
    ├── EmpleadosApi.java
    └── model/
        ├── EmpleadoRequest.java
        ├── EmpleadoResponse.java
        ├── ErrorResponse.java
        └── MensajeResponse.java
```

### Configuracion del plugin (pom.xml)

| Opcion | Valor | Proposito |
|--------|-------|-----------|
| `generatorName` | spring | Genera codigo Spring con anotaciones javax |
| `interfaceOnly` | true | Solo genera interfaces, no implementaciones |
| `skipDefaultInterface` | true | Evita metodos default que requieren ApiUtil |
| `openApiNullable` | false | No usa jackson-databind-nullable |
| `useSpringBoot3` | false | Genera con javax.*, no jakarta.* |
| `generateSupportingFiles` | false | No genera archivos auxiliares innecesarios |

### Configuracion IDE

Para que el IDE reconozca las clases generadas, marcar como "Generated Sources Root":

- **IntelliJ**: Click derecho en `target/generated-sources/openapi/src/main/java` > Mark Directory as > Generated Sources Root
- **Eclipse**: Project Properties > Java Build Path > Source > Add Folder > seleccionar la ruta
- **VS Code**: Maven auto-detecta tras `mvn compile`

---

## Configuracion (application.properties)

| Propiedad | Valor | Descripcion |
|-----------|-------|-------------|
| `server.port` | 8080 | Puerto del servidor |
| `spring.datasource.url` | `jdbc:h2:mem:empleadosdb` | BD H2 en memoria |
| `spring.datasource.username` | sa | Usuario H2 |
| `spring.h2.console.enabled` | true | Habilita consola web H2 |
| `spring.jackson.serialization.indent-output` | true | JSON formateado en respuestas |
| `springdoc.swagger-ui.url` | `/openapi/empleados-api.yaml` | Swagger UI sirve el YAML estatico |
| `logging.level.com.empleados` | DEBUG | Logs de la aplicacion |
| `logging.level.org.springframework.jdbc` | DEBUG | Logs de queries SQL |

---

## Seguridad de Dependencias

| Dependencia | Problema | Mitigacion |
|-------------|----------|-----------|
| SnakeYAML 1.30 (transitiva de Spring Boot 2.7) | CVE-2022-1471 (critica) | Override a version 2.2 via `<snakeyaml.version>` |
| swagger-annotations | Conflicto de versiones con springdoc | Alineada a 2.2.9 (misma que trae springdoc internamente) |
| jackson-databind-nullable | No necesaria (openApiNullable=false) | Removida del pom.xml |

---

## Principios SOLID Aplicados

| Principio | Implementacion |
|-----------|---------------|
| **S**ingle Responsibility | Servlet solo HTTP, Service solo logica, Repository solo datos, Mapper solo conversion |
| **O**pen/Closed | Validador custom `@EmpleadoValido` extensible sin modificar entidad |
| **L**iskov Substitution | `EmpleadoMapper` como componente Spring inyectable/reemplazable |
| **I**nterface Segregation | Repository expone solo metodos necesarios (findAll, save, deleteById, existsByRut) |
| **D**ependency Inversion | Servlet recibe Service, Validator y ObjectMapper por constructor (inyeccion via Spring) |

---

## Frontend

### Diseno

- **Paleta corporativa** inspirada en plataformas de gestion de personal chilenas
- **Responsive**: desktop (tabla) + mobile (cards) con breakpoint en 768px
- **SweetAlert2** para confirmaciones y notificaciones (reemplaza `alert()`/`confirm()`)

### Arquitectura JS (Modulos IIFE)

```
app.js (Orquestador)
  ├── api.js       (Fetch: GET, POST, DELETE)
  ├── ui.js        (DOM: render, alertas, estados)
  ├── validation.js (Reglas de formulario)
  └── utils.js     (Funciones puras)
```

Todas las funciones tienen maximo 15 lineas. Event delegation para botones dinamicos.

---

## Comandos Utiles

```bash
# Compilar + generar modelos + tests
mvn clean package

# Solo compilar (sin tests)
mvn clean package -DskipTests

# Ejecutar la aplicacion
mvn spring-boot:run

# Ejecutar tests
mvn test

# Ver arbol de dependencias
mvn dependency:tree
```
