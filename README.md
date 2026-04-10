# Desafío Técnico: Servlets y AJAX - Gestión de Empleados

## Descripción

Aplicación web de gestión de empleados desarrollada con Java 17, Spring Boot, Servlets y AJAX nativo. Utiliza H2 como base de datos en memoria y JDBC para la persistencia.

## Tecnologías

- **Java 17** (lambdas, streams)
- **Spring Boot 3.2.5** con Apache Tomcat embebido
- **Servlets** (Jakarta Servlet API)
- **JDBC** con Spring JdbcTemplate
- **H2 Database** (en memoria)
- **Lombok** (reducción de boilerplate)
- **HTML + CSS + JavaScript** vanilla (Fetch API)
- **Maven** como gestor de dependencias

## Arquitectura

Separación en 3 capas siguiendo principios SOLID:

- **Servlet** (capa de presentación) - Manejo de peticiones HTTP
- **Service** (capa de negocio) - Validaciones y reglas de negocio (interfaz + implementación)
- **Repository** (capa de datos) - Acceso a base de datos con JDBC

Adicionalmente:
- **DTO** - Objetos de transferencia para la API (no se expone la entidad)
- **Mapper** - Conversión entre Entity y DTO (clase utilitaria con métodos estáticos)
- **Model** - Entidad de dominio interna

## Requisitos Previos

- **Java 17** o superior instalado
- **Maven 3.6+** instalado (o usar el wrapper incluido `mvnw`)

## Cómo Ejecutar

### Opción 1: Con Maven Wrapper

```bash
# Linux/Mac
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

### Opción 2: Con Maven instalado

```bash
mvn spring-boot:run
```

### Opción 3: Ejecutar el JAR

```bash
mvn clean package -DskipTests
java -jar target/desafio-legacy-0.0.1-SNAPSHOT.jar
```

La aplicación estará disponible en: **http://localhost:8080**

## Endpoints API

| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/api/empleados` | Lista todos los empleados |
| POST | `/api/empleados` | Crea un nuevo empleado |
| DELETE | `/api/empleados?id={id}` | Elimina un empleado por ID |

### Ejemplo de Body (POST)

```json
{
  "nombre": "Juan",
  "apellido": "Pérez",
  "rut": "12.345.678-9",
  "cargo": "Desarrollador",
  "salario": 500000,
  "bonos": 100000,
  "descuentos": 50000
}
```

## Consola H2

Disponible en: **http://localhost:8080/h2-console**

- JDBC URL: `jdbc:h2:mem:empleadosdb`
- Usuario: `sa`
- Password: _(vacío)_

## Reglas de Negocio

### Validaciones Backend
- RUT/DNI no puede estar duplicado
- Salario base mínimo: $400,000
- Bonos no pueden superar el 50% del salario base
- Descuentos no pueden superar el salario base
- Todos los campos son obligatorios
- Formato de RUT válido (ej: 12.345.678-9)

### Validaciones Frontend
- Campos obligatorios completos
- Formato de RUT/DNI válido
- Salario base no menor a $400,000
- Errores mostrados dinámicamente (sin alertas de JavaScript)

## Estructura del Proyecto

```
src/main/java/app/v1/cl/desafiolegacy/
├── DesafioLegacyApplication.java          # Clase principal Spring Boot
├── config/
│   └── ServletConfig.java                 # Registro del Servlet en Spring Boot
├── dto/
│   └── EmpleadoDTO.java                   # Objeto de transferencia (API)
├── mapper/
│   └── EmpleadoMapper.java                # Conversión Entity <-> DTO
├── model/
│   └── Empleado.java                      # Entidad de dominio
├── repository/
│   └── EmpleadoRepository.java            # Acceso a datos (JDBC)
├── service/
│   ├── EmpleadoValidationService.java     # Interfaz del servicio
│   └── impl/
│       └── EmpleadoValidationServiceImpl.java  # Implementación con reglas de negocio
└── servlet/
    └── EmpleadoServlet.java               # Servlet /api/empleados (GET, POST, DELETE)

src/main/resources/
├── application.properties                 # Configuración de la aplicación
├── schema.sql                             # Esquema de la base de datos H2
└── static/
    └── index.html                         # Frontend HTML + CSS + JavaScript (AJAX)
```

## Cómo Probar

1. Ejecutar la aplicación con alguna de las opciones anteriores
2. Abrir `http://localhost:8080` en el navegador
3. Usar el formulario para agregar empleados
4. Verificar las validaciones ingresando datos inválidos (RUT mal formateado, salario bajo, etc.)
5. Eliminar empleados desde la tabla
6. Opcionalmente, probar la API directamente con curl:

```bash
# Listar empleados
curl http://localhost:8080/api/empleados

# Crear empleado
curl -X POST http://localhost:8080/api/empleados \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Juan","apellido":"Pérez","rut":"12.345.678-9","cargo":"Desarrollador","salario":500000,"bonos":100000,"descuentos":50000}'

# Eliminar empleado
curl -X DELETE "http://localhost:8080/api/empleados?id=1"
```
