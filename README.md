# Desafío Legacy

Aplicación web desarrollada con `Java 8`, `Spring Boot`, `Servlets`, `JDBC`, `H2` y `AJAX` nativo para gestionar empleados con validaciones de negocio en backend y frontend.

## Resumen
La solución implementa el endpoint `/api/empleados` con operaciones `GET`, `POST` y `DELETE`, además de una interfaz web simple en `HTML`, `CSS` y `JavaScript` puro para listar, agregar y eliminar empleados sin recargar la página.

Se mantuvo una arquitectura por capas, con separación entre:
- capa HTTP (`servlet`)
- reglas de negocio (`service`)
- acceso a datos (`repository`)
- validaciones (`validation`)
- contratos de intercambio (`dto`)

## Stack técnico
- Java 8
- Maven
- Spring Boot 2.7.18
- Apache Tomcat embebido
- Servlets
- JDBC puro
- H2 en memoria
- HTML + CSS + JavaScript nativo
- JUnit 5 + Mockito + AssertJ

## Decisiones de implementación
- El endpoint principal está implementado con `HttpServlet`, no con controladores MVC.
- Se usa `spring-boot-starter-web` para habilitar el arranque web y el contenedor embebido, pero la exposición de la API sigue resolviéndose con servlets registrados manualmente.
- Para cubrir la ambigüedad del enunciado entre `salario`, `salario base`, `bonos` y `descuentos`, el modelo final incorpora:
  - `salarioBase`
  - `bono`
  - `descuentos`

## Estructura principal
- `src/main/java/com/previred/desafio/Application.java`
- `src/main/java/com/previred/desafio/config/`
- `src/main/java/com/previred/desafio/servlet/EmpleadoServlet.java`
- `src/main/java/com/previred/desafio/service/EmpleadoService.java`
- `src/main/java/com/previred/desafio/repository/EmpleadoRepository.java`
- `src/main/java/com/previred/desafio/validation/EmpleadoValidator.java`
- `src/main/java/com/previred/desafio/dto/`
- `src/main/resources/static/`

## Modelo de empleado
Campos utilizados:
- `id`
- `nombre`
- `apellido`
- `rutDni`
- `cargo`
- `salarioBase`
- `bono`
- `descuentos`

## Reglas de negocio implementadas
Backend y frontend validan:
- campos obligatorios
- formato de `RUT/DNI`
- `salarioBase >= 400000`
- `bono <= 50% del salarioBase`
- `descuentos <= salarioBase`

Además, en backend:
- no se permite duplicidad de `RUT/DNI`
- los errores se devuelven con `HTTP 400` y JSON estructurado

## Endpoints

### `GET /api/empleados`
Retorna la lista de empleados en formato JSON.

### `POST /api/empleados`
Crea un nuevo empleado.

Ejemplo de request:

```json
{
  "nombre": "Ana",
  "apellido": "Pérez",
  "rutDni": "12345678-9",
  "cargo": "Analista",
  "salarioBase": 650000,
  "bono": 80000,
  "descuentos": 25000
}
```

### `DELETE /api/empleados?id=1`
Elimina un empleado por su identificador.

## Formato de errores
Cuando ocurre un error de validación o negocio, la API responde con una estructura como esta:

```json
{
  "status": 400,
  "message": "Validation failed",
  "errors": [
    {
      "field": "rutDni",
      "code": "DUPLICATE_RUT_DNI",
      "message": "El RUT/DNI ya existe",
      "rejectedValue": "12345678-9"
    }
  ]
}
```

Ejemplo de error inesperado:

```json
{
  "status": 500,
  "message": "Internal Server Error",
  "errors": [
    {
      "field": null,
      "code": "UNEXPECTED_ERROR",
      "message": "Ocurrio un error inesperado",
      "rejectedValue": null
    }
  ]
}
```

## Interfaz web
La vista está disponible en:

- `http://localhost:8080`

La API queda disponible en:

- `http://localhost:8080/api/empleados`

La interfaz permite:
- listar empleados sin recargar
- agregar empleados desde formulario
- eliminar empleados desde la tabla
- mostrar errores de validación en pantalla

## Configuración
Puerto y base en memoria definidos en `application.properties`:

```properties
server.port=8080
app.datasource.url=jdbc:h2:mem:desafiodb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
app.datasource.username=sa
app.datasource.password=
```

## Cómo ejecutar

### Requisitos
- Java 8 instalado
- Maven instalado

### Levantar la aplicación

```bash
mvn spring-boot:run
```

O bien:

```bash
mvn clean package
java -jar target/desafio-0.0.1-SNAPSHOT.jar
```

## Cómo probar manualmente
1. Abrir `http://localhost:8080`.
2. Verificar que carguen los empleados iniciales.
3. Crear un empleado con datos válidos.
4. Probar validaciones:
   - salario menor a `400000`
   - bono mayor al `50%`
   - descuentos mayores al salario
   - `RUT/DNI` duplicado
5. Eliminar un empleado desde la tabla.

## Datos iniciales
La base se inicializa automáticamente con:
- `src/main/resources/schema.sql`
- `src/main/resources/data.sql`

## Tests
Se agregaron tests unitarios y de integración para reforzar el desafío.

Suite actual:
- `EmpleadoValidatorTest`
- `EmpleadoServiceTest`
- `EmpleadoServletTest`
- `EmpleadoApiIntegrationTest`

Ejecutar tests:

```bash
mvn test
```

## Buenas prácticas aplicadas
- separación por capas
- DTOs para request y response
- validaciones centralizadas
- errores estructurados
- uso de JDBC encapsulado en repositorio
- documentación breve con Javadoc en clases clave
- tests unitarios e integración mínima

## Autor
Realizado por Sergio Suárez Suárez.
