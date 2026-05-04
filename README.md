# Gestión de Empleados — Desafío Técnico

Aplicación web Java 8 para gestión de empleados, implementada con **Arquitectura Hexagonal** (Puertos y Adaptadores), Servlets nativos y AJAX vanilla.

## Stack Tecnológico

| Componente | Tecnología |
|---|---|
| Lenguaje | Java 8 |
| Runtime / Contenedor | Spring Boot 2.7.18 + Apache Tomcat 9 (embebido) |
| Endpoints | Servlets nativos (`javax.servlet`) |
| Persistencia | JDBC nativo + H2 en memoria |
| Serialización JSON | Gson 2.10.1 |
| Frontend | HTML5 + JavaScript Vanilla (Fetch API) |
| Build | Maven 3.x |
| Tests | JUnit 5 + Mockito |

## Requisitos

- Java 8 o superior
- Maven 3.6 o superior

## Cómo ejecutar

```bash
# 1. Clonar o descomprimir el proyecto
cd empleados-app

# 2. Compilar y ejecutar
mvn spring-boot:run
```

La aplicación levanta en **http://localhost:8080**

## Endpoints disponibles

| Método | URL | Descripción |
|---|---|---|
| `GET` | `/api/empleados` | Retorna lista de empleados en JSON |
| `POST` | `/api/empleados` | Agrega un nuevo empleado |
| `DELETE` | `/api/empleados?id={id}` | Elimina un empleado por ID |

### Ejemplo POST

```json
POST /api/empleados
Content-Type: application/json

{
  "nombre": "Juan",
  "apellido": "Pérez",
  "rut": "12345678-5",
  "cargo": "Desarrollador",
  "salarioBase": 600000,
  "bono": 200000,
  "descuentos": 50000
}
```

Respuesta exitosa `201 Created`:
```json
{
  "id": 1,
  "nombre": "Juan",
  "apellido": "Pérez",
  "rut": "12345678-5",
  "cargo": "Desarrollador",
  "salarioBase": 600000.0,
  "bono": 200000.0,
  "descuentos": 50000.0,
  "salarioNeto": 750000.0
}
```

Respuesta de error `400 Bad Request`:
```json
{
  "errores": ["El salario base ($300000) no puede ser menor a $400000"],
  "timestamp": "2026-05-04T03:57:21Z"
}
```

## Reglas de Negocio validadas

| Regla | Comportamiento |
|---|---|
| Salario base mínimo | No puede ser menor a **$400.000** |
| Tope de bono | No puede superar el **50% del salario base** |
| Tope de descuentos | No pueden superar el **salario base** |
| RUT único | Se rechaza RUT ya registrado |
| Formato RUT | Validación de formato y dígito verificador (módulo 11 chileno) |

Toda violación retorna `HTTP 400` con JSON de errores. Múltiples infracciones se reportan en un solo response.

## Interfaz Web

Abrir en el navegador: **http://localhost:8080/index.html**

- Lista de empleados con actualización dinámica (sin recargar página)
- Formulario de alta con validaciones en frontend antes de enviar
- Botón de eliminar por empleado
- Mensajes de error inline (sin `alert()`)

## Ejecutar los tests

```bash
mvn test
```

12 tests unitarios sobre las reglas de negocio (`ValidacionServiceImplTest`):
- Salario base exacto en el límite ($400.000)
- Salario base por debajo del límite
- Bono exactamente al 50%
- Bono sobre el 50%
- Descuentos iguales al salario base
- Descuentos sobre el salario base
- RUT válido (módulo 11)
- RUT con dígito verificador K
- RUT con formato inválido
- RUT con dígito verificador incorrecto
- Múltiples errores reportados juntos

## Consola H2

Base de datos accesible en: **http://localhost:8080/h2-console**
- JDBC URL: `jdbc:h2:mem:empleadosdb`
- Usuario: `sa`
- Contraseña: *(vacía)*

## Arquitectura

```
com.mindgrid.empleados/
├── adapters/
│   ├── inbound/web/         # Servlets, DTOs, Mapper Web, WebExceptionHandler
│   └── outbound/database/   # EmpleadoDbAdapter (JDBC), Entity, DbMapper
├── application/usecase/     # GestionEmpleadoUseCase (interfaz + implementación)
├── domain/
│   ├── model/               # Empleado (Builder pattern)
│   ├── exception/           # BusinessException
│   ├── repository/          # EmpleadoRepository (interfaz — puerto de salida)
│   └── service/             # ValidacionService (interfaz + implementación)
└── infrastructure/config/   # DbConnectionConfig (DataSource H2)
```

El dominio no tiene ninguna dependencia de Spring, HTTP ni JDBC. Las reglas de negocio viven exclusivamente en `domain/service/ValidacionServiceImpl`.
