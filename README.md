# Desafío Técnico — Gestión de Empleados

Aplicación web para gestión de empleados construida con Java 21, Spring Boot, Servlets puros y JDBC sobre H2 in-memory.

---

## Requisitos previos

- Java 21
- Maven 3.6+ (o usar el Maven Wrapper incluido `./mvnw`)

Verificar versión de Java:
```bash
java -version
```

Para ejecutar sin Maven instalado globalmente usar `./mvnw` en lugar de `mvn`.

---

## Ejecutar la aplicación

```bash
./mvnw spring-boot:run
```

La aplicación estará disponible en: http://localhost:8080

---

## Ejecutar los tests

```bash
./mvnw test
```

---

## Endpoints disponibles

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | /api/empleados | Retorna lista de empleados ordenada por nombre |
| POST | /api/empleados | Crea un nuevo empleado |
| DELETE | /api/empleados/{id} | Elimina un empleado por ID |

### Ejemplo POST

```json
{
    "nombre": "Wilson",
    "apellido": "Fisk",
    "rut": "12345678-9",
    "cargo": "Desarrollador Backend",
    "salarioBase": 800000,
    "bono": 200000,
    "descuentos": 50000
}
```

### Validaciones backend

- RUT no puede estar duplicado
- Salario base mínimo: $400.000
- Bono máximo: 50% del salario base
- Descuentos no pueden superar el salario base
- Bono y descuentos son opcionales (valor por defecto: 0)

---

## Consola H2

Durante el desarrollo, la consola H2 está disponible en: http://localhost:8080/h2-console

| Campo | Valor |
|-------|-------|
| JDBC URL | jdbc:h2:mem:empleadosdb |
| Username | sa |
| Password | (vacío) |

---

## Decisiones de diseño

**Servlets puros con ServletRegistrationBean**
Los endpoints se implementaron con `HttpServlet` registrado manualmente via `ServletRegistrationBean`, sin usar `@RestController` ni Spring MVC, cumpliendo el requisito de la prueba.

**JDBC puro con JdbcTemplate**
La persistencia se implementó con `JdbcTemplate` sin JPA ni Hibernate. El mapeo de filas a objetos se realiza con `RowMapper` usando lambdas.

**Arquitectura por capas**
`EmpleadoServlet` → `EmpleadoService` → `EmpleadoRepository` + `EmpleadoValidator`. Cada capa tiene una única responsabilidad (SOLID - SRP).

**Inyección de dependencias por constructor**
Todas las dependencias se inyectan por constructor, cumpliendo el principio de Inversión de Dependencias (SOLID - DIP).

**BigDecimal para valores monetarios**
Los campos monetarios (salarioBase, bono, descuentos) usan `BigDecimal` en lugar de `double` para garantizar precisión decimal exacta — crítico en sistemas de nómina.

**Ordenamiento en Java con Streams**
La lista de empleados se ordena alfabéticamente por nombre usando `Stream.sorted()` con `Comparator.comparing()`, demostrando el uso de lambdas y Streams requerido por el enunciado.

**Prevención XSS**
El frontend usa `createElement` + `textContent` en lugar de `innerHTML` para renderizar datos del servidor, previniendo XSS.

**Protección SQL Injection**
`JdbcTemplate` usa queries parametrizadas en todas las operaciones, previniendo SQL Injection.

---

## Limitaciones conocidas

- El catch genérico en `doPost` retorna "El cuerpo de la solicitud no es un JSON válido" para cualquier error no controlado, incluyendo errores internos del servidor. En producción se separarían los tipos de excepción.
- Los datos no persisten al reiniciar la aplicación — H2 in-memory es intencional para este contexto de prueba.

---

## Mejoras futuras

- Validación del dígito verificador del RUT chileno (algoritmo módulo 11)
- Ordenamiento por columnas en la tabla del frontend
- Sanitización de largo máximo en campos de texto
- Tests de integración para `EmpleadoRepository` con `@JdbcTest`