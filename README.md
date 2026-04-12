# Desafío Técnico: Servlets y AJAX

## Descripción
Aplicación web de gestión de empleados desarrollada con Java 8, Spring Boot 2.7, Servlets y AJAX (Fetch API).  
Utiliza H2 como base de datos en memoria y JDBC para la persistencia.

## Arquitectura
```
Controller (Servlet) → Service → Repository
     ↕                    ↕
   Model ← Mapper → Entity
```
- **Entity**: Representación de la tabla en BD (nunca se expone al cliente).
- **Model**: DTO con `@SerializedName` para serialización JSON.
- **Mapper**: Conversión Entity ↔ Model.
- **Repository**: Acceso a datos con JDBC (`JdbcTemplate`).
- **Service**: Lógica de negocio y validaciones.
- **Servlet**: Controlador HTTP (`HttpServlet`) con endpoint RESTful.

## Requisitos Previos
- Java 8 o superior
- Maven 3.6+

## Cómo Ejecutar

```bash
# Clonar el repositorio
git clone <url-del-repositorio>
cd desafio-legacy

# Compilar y ejecutar
mvn spring-boot:run
```

La aplicación estará disponible en: **http://localhost:8080**

## Endpoints

| Método | URL                    | Descripción            |
|--------|------------------------|------------------------|
| GET    | `/api/empleados`       | Listar empleados       |
| POST   | `/api/empleados`       | Crear empleado         |
| DELETE | `/api/empleados/{id}`  | Eliminar empleado      |

## Interfaces Disponibles

| URL                        | Descripción                    |
|----------------------------|--------------------------------|
| http://localhost:8080      | Interfaz web (HTML + AJAX)     |
| http://localhost:8080/h2-console | Consola H2 (JDBC URL: `jdbc:h2:mem:empleadosdb`) |
| http://localhost:8080/swagger-ui.html | Swagger UI              |
| http://localhost:8080/openapi.yaml | Especificación OpenAPI   |

## Ejemplo de Uso (cURL)

```bash
# Listar empleados
curl http://localhost:8080/api/empleados

# Crear empleado
curl -X POST http://localhost:8080/api/empleados \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Juan","apellido":"Pérez","rut":"12345678-5","cargo":"Desarrollador","salario_base":500000,"bonos":100000,"descuentos":50000}'

# Eliminar empleado
curl -X DELETE http://localhost:8080/api/empleados/1
```

## Validaciones de Negocio (Backend)
- RUT/DNI duplicado → HTTP 400
- Salario base menor a $400,000 → HTTP 400
- Bonos mayores al 50% del salario base → HTTP 400
- Descuentos mayores al salario base → HTTP 400
- Campos obligatorios vacíos → HTTP 400
- Formato de RUT inválido → HTTP 400

## Validaciones Frontend
- Campos obligatorios completos
- Formato de RUT/DNI (ej: `12345678-5`)
- Salario base mínimo $400,000
- Errores mostrados dinámicamente (sin `alert()`)

## Tecnologías
- Java 8
- Spring Boot 2.7.18
- Apache Tomcat (embebido)
- H2 Database (en memoria)
- JDBC (`JdbcTemplate`)
- Servlets (`HttpServlet`)
- Gson (serialización JSON)
- HTML5 + CSS3 + JavaScript (Fetch API)
- SpringDoc OpenAPI (Swagger UI)
