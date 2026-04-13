# Gestion de Empleados - Prueba Tecnica (Servlet + AJAX)

Aplicacion para administrar empleados con enfoque backend clasico: Servlet nativo + JDBC, usando Spring Boot como runtime y configuracion.

## Alcance funcional

- Listar empleados
- Crear empleado
- Eliminar empleado por ID
- Buscar con filtros por texto (`q`) y cargo (`cargo`)

## Stack

- Java 8+ (compilacion configurada en 1.8)
- Spring Boot 2.7 (Tomcat embebido)
- API de Servlets (`javax.servlet`)
- JDBC (`JdbcTemplate`)
- H2 en memoria
- Maven
- Frontend: HTML + CSS + JavaScript (Fetch API)

## Arquitectura

- Flujo principal: `http -> service -> repository`
- Capa HTTP implementada con `HttpServlet` (sin `@RestController`)
- Contratos separados por interfaz en `service/contract` y `repository/contract`
- Validacion en dos capas: frontend y backend

## Arranque rapido

### Requisitos

- JDK 8 o superior
- Maven 3.8 o superior

Compatibilidad validada en este repo:

- Compila con `java.version=1.8` (`pom.xml`)
- Tests ejecutados correctamente en JDK 17

### Ejecutar en desarrollo

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Ejecutar en productivo

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### URLs

- UI: `http://localhost:8080/`
- API: `http://localhost:8080/api/empleados`
- OpenAPI: `http://localhost:8080/openapi.yaml`
- H2 Console (solo `dev`): `http://localhost:8080/h2-console`

## API

### `GET /api/empleados`

Filtros opcionales:

- `q`: nombre, apellido, rutDni o cargo
- `cargo`: filtro por cargo

Ejemplo:

```http
GET /api/empleados?q=garcia&cargo=analista
```

### `POST /api/empleados`

Ejemplo:

```json
{
  "nombre": "Carla",
  "apellido": "Mendez",
  "rutDni": "16543210-4",
  "cargo": "Analista",
  "salarioBase": 650000,
  "bono": 30000,
  "descuentos": 15000
}
```

Notas de montos:

- `salarioBase`, `bono` y `descuentos` se manejan con `BigDecimal`
- OpenAPI define precision de 2 decimales con `multipleOf: 0.01`

### `DELETE /api/empleados/{id}`

Ejemplo:

```http
DELETE /api/empleados/7
```

## Validaciones

### Backend

- No permite `rutDni` duplicado
- `salarioBase` minimo: `400000`
- `bono` maximo: 50% del salario base
- `descuentos` no puede superar salario base

Codigos de error manejados:

- `VALIDATION_ERROR`
- `INVALID_JSON`
- `INVALID_PARAMETER`
- `EMPLEADO_NOT_FOUND`
- `UNEXPECTED_ERROR`

### Frontend

- Campos obligatorios
- Validacion de montos y reglas de negocio base
- Validacion de RUT chileno real (modulo 11)
- Autoformato de RUT al escribir (ejemplo: `216697405` -> `21.669.740-5`)
- Errores en pantalla sin `alert()`

## Base de datos

- JDBC URL: `jdbc:h2:mem:desafiolegacy`
- Usuario: `sa`
- Password: vacio

Nota: al ser in-memory, los datos se pierden al detener la aplicacion.

## Pruebas

Ejecutar:

```bash
mvn test
```

Estado actual de suite (ultima ejecucion local):

- Tests: `28`
- Failures: `0`
- Errors: `0`

Suites:

- `EmpleadoServletTest`
- `JdbcEmpleadoRepositoryTest`
- `EmpleadoBusinessValidatorTest`
- `EmpleadoServiceImplTest`
- `DesafioLegacyApplicationTests`

## Estructura del proyecto

```text
src/main/java/com/desafio/legacy
  |- config
  |- dto
  |- exception
  |- http
  |- model
  |- repository
  |  |- contract
  |- service
  |  |- contract

src/main/resources
  |- application.properties
  |- application-dev.properties
  |- application-prod.properties
  |- schema.sql
  |- static/
     |- index.html
     |- styles.css
     |- js/
```

## Datos del postulante

- Nombre: Felipe Benitez Sura
- Cargo: Desarrollador Backend Java
- Email: felipebenitezsura@gmail.com
