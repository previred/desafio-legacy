# Sistema de Gestión de Empleados

Sistema web para gestión de empleados con arquitectura en capas, implementando Servlets, AJAX, validaciones de RUT chileno y reglas de negocio.

**Documentación de cumplimiento**: Ver [CUMPLIMIENTO_DESAFIO.md](CUMPLIMIENTO_DESAFIO.md) para trazabilidad completa de requisitos.

## Stack Tecnológico

**Backend**:
- Java 8 (Lambdas, Streams, Optional)
- Spring Boot 2.7.18 + Apache Tomcat embebido
- Servlets 4.0.1
- JDBC puro (sin ORM) + HikariCP
- H2 Database (in-memory)
- Jackson (JSON)
- SLF4J + Logback

**Frontend**:
- HTML5 + CSS3
- JavaScript Vanilla (sin frameworks)
- Fetch API (AJAX nativo)

**Testing**:
- JUnit 4 + Mockito
- 37 tests unitarios

## Requisitos Previos

- JDK 8 o superior
- Maven 3.6+

## Instalación y Ejecución

### 1. Clonar e instalar
```bash
git clone https://github.com/Angelocerpa/desafio-legacy.git
cd desafio-legacy
mvn clean install
```

### 2. Ejecutar aplicación
```bash
mvn spring-boot:run
```

### 3. Acceder
- **Aplicación**: http://localhost:8080
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:empleadosdb`
  - User: `sa`
  - Password: (vacío)

## Arquitectura

```
Frontend (HTML/JS/CSS)
    ↓ HTTP/JSON
EmpleadoServlet (REST Controller)
    ↓
EmpleadoService (Business Logic)
    ↓
EmpleadoValidator + RutValidator
    ↓
EmpleadoDAO (JDBC)
    ↓
H2 Database (In-Memory)
```

**Patrones aplicados**: DAO, DTO, Service Layer, Dependency Injection

**Principios SOLID**: Separación de responsabilidades, interfaces segregadas, inversión de dependencias

## API REST

### GET /api/empleados
Listar todos los empleados

**Response 200**:
```json
[
  {
    "id": 1,
    "nombre": "Juan",
    "apellido": "Pérez",
    "rut": "12.345.678-5",
    "cargo": "Desarrollador Senior",
    "salarioBase": 1200000,
    "bonos": 300000,
    "descuentos": 50000,
    "salarioTotal": 1450000
  }
]
```

### POST /api/empleados
Crear nuevo empleado

**Request**:
```json
{
  "nombre": "María",
  "apellido": "González",
  "rut": "23.456.789-6",
  "cargo": "Product Manager",
  "salarioBase": 1500000,
  "bonos": 500000,
  "descuentos": 100000
}
```

**Response 201**: Empleado creado

**Response 400**: Errores de validación
```json
{
  "errors": [
    "El RUT ya existe en el sistema",
    "Los bonos no pueden superar el 50% del salario base"
  ]
}
```

### DELETE /api/empleados/{id}
Eliminar empleado por ID

**Response 204**: Eliminado exitosamente

## Validaciones

### Backend (Java)
- RUT único (no duplicados)
- RUT válido con algoritmo Módulo 11
- Salario base: $400.000 - $100.000.000
- Bonos: máximo 50% del salario base
- Descuentos: máximo igual al salario base
- Campos obligatorios

### Frontend (JavaScript)
- Validación de RUT en tiempo real
- Auto-formateo de RUT (XX.XXX.XXX-X)
- Validación de rangos numéricos
- Mensajes de error dinámicos (sin alerts)

## Testing

```bash
mvn test
```

**Cobertura**: 37/37 tests pasando
- RutValidatorTest: Algoritmo Módulo 11
- EmpleadoValidatorTest: Reglas de negocio
- EmpleadoServiceTest: Lógica con Mockito
- EmpleadoDAOTest: JDBC con H2

## Estructura del Proyecto

```
src/
├── main/java/com/previred/empleados/
│   ├── servlet/         # EmpleadoServlet (REST endpoints)
│   ├── service/         # Lógica de negocio
│   ├── dao/             # Acceso a datos JDBC
│   ├── validator/       # Validadores (RUT, reglas)
│   ├── model/           # Entidad Empleado
│   ├── dto/             # Request/Response/Error DTOs
│   └── exception/       # Excepciones personalizadas
├── main/resources/
│   ├── application.properties
│   ├── schema.sql       # DDL
│   └── data.sql         # Datos iniciales
├── main/webapp/
│   ├── index.html
│   ├── css/styles.css
│   └── js/
│       ├── app.js       # Lógica UI
│       ├── api.js       # Cliente REST
│       └── validator.js # Validaciones cliente
└── test/java/           # Tests unitarios
```

## Características Destacadas

**Java 8**:
- Lambdas y method references
- Streams para transformaciones
- Optional para manejo de nulos

**Seguridad**:
- PreparedStatement (prevención SQL injection)
- Validación multi-capa
- Manejo centralizado de excepciones

**Buenas Prácticas**:
- Código limpio y legible
- Logging estructurado
- Connection pooling
- Configuración externalizada
- Tests con alta cobertura

## Datos de Prueba

La aplicación incluye 3 empleados iniciales:
1. Juan Pérez - RUT: 12.345.678-5
2. María González - RUT: 23.456.789-6
3. Carlos Rodríguez - RUT: 18.765.432-7

Todos los RUTs validados con algoritmo Módulo 11.
