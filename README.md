# Desafío Técnico: Servlets y AJAX

## Objetivo:
Demostrar el conocimiento sobre Java (mínimo versión 8), manejo de servlets y peticiones AJAX nativas.

## Requisitos Técnicos:
### Java:
- Utiliza Java 8 o superior para la implementación.
- Utiliza las características de Java como lambdas y streams, cuando sea apropiado.
- Utilizar Maven como gestor de dependencias.
- Utilizar Spring Boot como Runtime para la ejecución del desafío en conjunto con Apache Tomcat como contenedor web.

## Parte 1: Implementación de un Servicio Web con Servlets y AJAX
```
  Crear una aplicación web en Java 8 con Servlets y manejo de AJAX, con las siguientes características: 

    Endpoint: /api/empleados 
      GET: Retorna una lista de empleados en formato JSON. 
      POST: Permite agregar un nuevo empleado enviando datos en formato JSON. 
      DELETE: Elimina un empleado por su ID. 

  Datos esperados del empleado: 

    ID (autogenerado), Nombre, Apellido, RUT/DNI, Cargo, Salario.

  Interfaz con AJAX: 
    Crear una página web simple en HTML + JavaScript (sin frameworks como React o Angular). 
    Usar AJAX (Fetch API o XMLHttpRequest) para:  
      - Cargar la lista de empleados sin recargar la página. 
      - Agregar nuevos empleados mediante un formulario sin recargar la página. 
      - Eliminar empleados con un botón sin recargar la página. 

  Requerimientos técnicos: 
    - No usar frameworks externos, solo Servlets y JDBC para conexión con una BD en memoria como H2. 
    - Manejo adecuado de excepciones y logging. 
    - Validación de datos en los endpoints. 
```

## Parte 2: Validaciones de Reglas de Negocio con AJAX

```
  Implementar validaciones en la carga de empleados y nóminas: 

    1. En el backend (Java 8): 
        - Rechazar empleados con RUT/DNI duplicado. 
        - No permitir salarios base menores a $400,000. 
        - Bonos no pueden superar el 50% del salario base. 
        - El total de descuentos no puede ser mayor al salario base. 
        - Si alguna regla se incumple, se debe retornar una respuesta HTTP 400 con un JSON indicando los registros con error. 
    2. En el frontend (JavaScript + AJAX): 
        - Implementar validaciones antes de enviar el formulario:  
        - Verificar que todos los campos estén completos. 
        - Validar formato del RUT/DNI. 
        - Validar que el salario base no sea menor a $400,000. 
        - Mostrar errores de validación de forma dinámica en la página (sin alertas de JavaScript). 
```

## Entregables:
### Repositorio de GitHub:
- Realiza un Pull request a este repositorio indicando tu nombre, empresa reclutadora, correo y cargo al que postulas.
- Todos los PR serán rechazados, no es un indicador de la prueba.

### Documentación:
- Incluye instrucciones claras en un README en formato markdown, sobre cómo ejecutar y probar la aplicación.

## Evaluación:
Se evaluará la solución en función de los siguientes criterios:

- Correcta implementación de las funcionalidades solicitadas.
- Aplicación de buenas prácticas de desarrollo, patrones de diseño y principios SOLID.
- Uso adecuado de Java y Javascript.
- Claridad y completitud de la documentación.

---

# Sistema de Gestión de Empleados - Implementación

## Descripción

Aplicación web completa para la gestión de empleados con arquitectura en capas profesional, implementando Servlets, AJAX, validaciones de RUT chileno y reglas de negocio.

## Tecnologías Utilizadas

- **Java 8**: Lambdas, Streams, Optional
- **Spring Boot 2.7.18**: Framework de aplicación y runtime
- **Servlets 4.0**: Controladores REST
- **JDBC Puro**: Acceso a datos sin ORM
- **H2 Database**: Base de datos en memoria
- **HikariCP**: Connection pooling
- **Jackson**: Serialización JSON
- **SLF4J + Logback**: Logging con rotación de archivos
- **Maven**: Gestor de dependencias
- **HTML5 + JavaScript Vanilla**: Frontend sin frameworks
- **Fetch API**: Cliente REST AJAX
- **JUnit 4 + Mockito**: Testing

## Arquitectura

```
Frontend (HTML/JS/CSS)
    ↓ HTTP/JSON
EmpleadoServlet (Controller)
    ↓
EmpleadoService (Business Logic)
    ↓
EmpleadoValidator + RutValidator
    ↓
EmpleadoDAO (JDBC)
    ↓
H2 Database (In-Memory)
```

### Principios Aplicados
- **SOLID**: Separación de responsabilidades
- **DAO Pattern**: Acceso a datos desacoplado
- **DTO Pattern**: Transferencia de datos
- **Service Layer**: Lógica de negocio centralizada
- **Validación multi-capa**: Frontend + Backend

## Requisitos Previos

- **JDK 8 o superior**
- **Maven 3.6+**
- **Git**

## Instalación

```bash
git clone https://github.com/tu-usuario/desafio-legacy.git
cd desafio-legacy
mvn clean install
```

## Ejecución

```bash
mvn spring-boot:run
```

La aplicación estará disponible en:
- **Aplicación**: http://localhost:8080
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:empleadosdb`
  - Username: `sa`
  - Password: (vacío)

## Estructura del Proyecto

```
desafio-legacy/
├── src/main/java/com/previred/empleados/
│   ├── config/             # Configuración (DataSource, Servlets)
│   ├── servlet/            # EmpleadoServlet (REST endpoints)
│   ├── service/            # Lógica de negocio
│   ├── dao/                # Acceso a datos (JDBC)
│   ├── model/              # Entidad Empleado
│   ├── dto/                # DTOs (Request, Response, Error)
│   ├── validator/          # Validadores (RUT, Reglas de negocio)
│   ├── exception/          # Excepciones personalizadas
│   └── util/               # Utilidades (JsonUtil)
├── src/main/resources/
│   ├── application.properties
│   ├── logback.xml
│   ├── schema.sql          # DDL base de datos
│   └── data.sql            # Datos iniciales
├── src/main/webapp/
│   ├── index.html          # Interfaz principal
│   ├── css/styles.css      # Estilos
│   └── js/
│       ├── app.js          # Lógica UI
│       ├── api.js          # Cliente REST
│       └── validator.js    # Validaciones cliente
└── src/test/java/          # Tests unitarios
```

## API REST

### GET /api/empleados
Obtener lista de empleados

**Response 200 OK:**
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

**Request Body:**
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

**Response 201 Created:** (mismo formato que GET)

**Response 400 Bad Request:**
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

**Response 204 No Content:** Éxito sin body

## Reglas de Negocio

### Validaciones Backend (HTTP 400)
1. RUT único (no duplicados)
2. RUT formato válido con Módulo 11
3. Salario base >= $400,000
4. Bonos <= 50% salario base
5. Descuentos <= salario base
6. Campos obligatorios completos

### Validaciones Frontend (feedback dinámico)
1. Validación formato RUT en tiempo real
2. Auto-formateo RUT (XX.XXX.XXX-X)
3. Validación salario >= $400,000
4. Validación bonos al cambiar salario/bonos
5. Validación descuentos
6. Errores dinámicos en DOM (sin alerts)

## Validación de RUT Chileno

Implementado algoritmo **Módulo 11** para validar RUT:
- Soporta formatos: `12.345.678-9`, `12345678-9`, `123456789`
- Dígito verificador: 0-9 o K
- Auto-formateo en frontend
- Normalización en backend

## Testing

Ejecutar tests:
```bash
mvn test
```

### Cobertura de Tests
- **RutValidatorTest**: Validación RUT con Módulo 11
- **EmpleadoValidatorTest**: Reglas de negocio
- **EmpleadoServiceTest**: Lógica con Mockito
- **EmpleadoDAOTest**: JDBC con H2

## Logging

Los logs se generan en:
- **logs/empleados-app.log**: Todos los logs (rotación 30 días)
- **logs/empleados-error.log**: Solo errores (rotación 60 días)

Niveles:
- **INFO**: Flujo de aplicación
- **DEBUG**: Detalles técnicos
- **ERROR**: Excepciones

## Características Destacadas

### Java 8 Features
- Lambdas: `empleados.stream().map(this::convertToDTO)`
- Streams: Transformación Entity → DTO
- Optional: Manejo de valores nulos
- Method references

### Patrones de Diseño
- **DAO Pattern**: Separación acceso a datos
- **DTO Pattern**: Desacoplamiento API
- **Service Layer**: Lógica centralizada
- **Dependency Injection**: Spring autowiring

### Buenas Prácticas
- Validación multi-capa (Defense in Depth)
- Manejo centralizado de excepciones
- Logging estructurado
- Configuración externalizada
- SQL parametrizado (PreparedStatement)
- Connection pooling (HikariCP)
- Código limpio y legible

## Datos de Prueba

La aplicación incluye 3 empleados de ejemplo:
1. Juan Pérez (RUT: 12.345.678-5)
2. María González (RUT: 23.456.789-6)
3. Carlos Rodríguez (RUT: 18.765.432-7)

## Troubleshooting

### Puerto 8080 ocupado
Cambiar puerto en `application.properties`:
```properties
server.port=8081
```

### Error de compilación
Verificar JDK 8+:
```bash
java -version
mvn -version
```

### Base de datos no inicializa
Verificar `application.properties`:
```properties
spring.sql.init.mode=always
```

### Tests fallan
Limpiar y reconstruir:
```bash
mvn clean test
```

## Contacto

Para consultas o dudas sobre la implementación, contactar a través del repositorio.
