# Sistema de Gestión de Empleados - Previred


### Postulante ### 
- **Nombre**: Marcelo Valderrama Correa
- **Empresa reclutadora**: HF SOLUTIONS
- **Correo**: correa96cl@hotmail.com
- **Cargo al que postulas**: Desarrollador Backend Java

## Descripción General

Aplicación web desarrollada con **Java 8**, **Spring Boot**, **Servlets**, **AJAX** y **H2 Database** para gestionar empleados con validaciones de reglas de negocio tanto en backend como en frontend.

## Requisitos Técnicos Cumplidos

### Tecnologías Utilizadas
- **Java 8+**: Lenguaje principal con lambdas y streams
- **Spring Boot 2.7.18**: Framework para la ejecución
- **Apache Tomcat**: Contenedor web integrado en Spring Boot
- **Maven**: Gestor de dependencias y compilación
- **H2 Database**: Base de datos en memoria
- **GSON**: Serialización/deserialización JSON
- **HTML5 + CSS3**: Frontend sin frameworks externos
- **Vanilla JavaScript**: AJAX con Fetch API (sin frameworks)

## Instalación y Ejecución

### Requisitos Previos
- Java 8 o superior instalado
- Maven 3.6+ instalado
- Terminal/CMD accesible

### Pasos de Instalación

1. **Clonar o descargar el proyecto:**
   ```bash
   cd /ruta/al/proyecto/previred
   ```

2. **Compilar el proyecto con Maven:**
   ```bash
   mvn clean compile
   ```

3. **Ejecutar la aplicación:**
   ```bash
   mvn spring-boot:run
   ```

   O compilar generar JAR y ejecutar:
   ```bash
   mvn clean package
   java -jar target/previred-0.0.1-SNAPSHOT.jar
   ```

4. **Acceder a la aplicación:**
   - URL: `http://localhost:8080/`
   - Consola H2: `http://localhost:8080/h2-console` (Usuario: `sa`, Contraseña: vacía)

## Funcionalidades Implementadas

### Parte 1: API REST con Servlets y AJAX

#### Endpoints Disponibles

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| **GET** | `/api/empleados` | Retorna lista de todos los empleados en JSON |
| **POST** | `/api/empleados` | Agrega un nuevo empleado con validaciones |
| **DELETE** | `/api/empleados?id={id}` | Elimina un empleado por su ID |

#### Estructura de Datos del Empleado

```json
{
  "id": 1,
  "nombre": "Juan",
  "apellido": "Pérez",
  "rut": "12345678-9",
  "cargo": "Analista",
  "salario": 2500000,
  "bono": 250000,
  "descuento": 100000
}
```

#### Ejemplo de Solicitud POST

```javascript
fetch('/api/empleados', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    nombre: "Carlos",
    apellido: "López",
    rut: "98765432-1",
    cargo: "Developer",
    salario: 2000000,
    bono: 200000,
    descuento: 50000
  })
})
```

### Parte 2: Validaciones de Reglas de Negocio

#### Validaciones en Backend (Java)

1. **Campos Obligatorios**: Nombre, Apellido, RUT, Cargo, Salario
2. **Salario Mínimo**: No puede ser menor a **$400.000**
3. **RUT Único**: No permite RUT/DNI duplicados
4. **Bonos**: No pueden superar el **50%** del salario base
5. **Descuentos**: No pueden ser mayores al **salario base**

**Respuesta de Error (HTTP 400):**
```json
{
  "errores": [
    "El salario base no puede ser menor a $400.000.",
    "Los bonos no pueden superar el 50% del salario base."
  ]
}
```

#### Validaciones en Frontend (JavaScript)

1. **Campos Completos**: Valida que ningún campo esté vacío
2. **Formato RUT/DNI**: Acepta formatos como `12345678-9` o `12.345.678-9`
3. **Salario Mínimo**: Valida que no sea menor a $400.000
4. **Errores Dinámicos**: Se muestran sin recargar la página

**Características de la Interfaz:**
- Errores mostrados en un panel HTML (no alertas nativas)
- Actualización dinámica de la tabla sin recargar
- Modal de confirmación para eliminar registros
- Cálculo automático del salario líquido (Base + Bono - Descuento)

## Estructura del Proyecto

```
previred/
├── src/
│   ├── main/
│   │   ├── java/cl/hf/previred/
│   │   │   ├── PreviredApplication.java        # Clase principal Spring Boot
│   │   │   ├── config/
│   │   │   │   └── ServletConfig.java          # Configuración del Servlet
│   │   │   ├── dao/
│   │   │   │   └── EmpleadoDao.java            # Capa de acceso a datos (JDBC)
│   │   │   ├── model/
│   │   │   │   └── EmpleadoDTO.java            # Objeto de transferencia de datos
│   │   │   └── servlet/
│   │   │       └── EmpleadoServlet.java        # Servlet principal (GET, POST, DELETE)
│   │   └── resources/
│   │       ├── application.properties           # Configuración de la aplicación
│   │       ├── schema.sql                       # Script de inicialización de BD
│   │       └── static/
│   │           ├── index.html                   # Página principal
│   │           └── app.js                       # Lógica AJAX
│   └── test/
│       └── java/cl/hf/previred/
│           └── PreviredApplicationTests.java    # Tests básicos
├── pom.xml                                      # Configuración Maven
└── README.md                                    # Este archivo
```

## Patrones de Diseño y Principios SOLID Aplicados

### 1. Single Responsibility Principle (SRP)
- **EmpleadoDAO**: Responsable solo de operaciones CRUD y persistencia
- **EmpleadoServlet**: Maneja solo las solicitudes HTTP y validaciones de negocio
- **EmpleadoDTO**: Únicamente representa la estructura de datos
- **ServletConfig**: Solo configuración del registro del servlet

### 2. Open/Closed Principle (OCP)
- La clase `EmpleadoDAO` está abierta para extensión (métodos helper privados)
- Se puede extender fácilmente agregando nuevos métodos sin modificar el código existente

### 3. Dependency Injection (DI)
- Uso de anotaciones `@Autowired` de Spring para inyectar `DataSource` y `EmpleadoDao`
- Facilita testing y desacoplamiento de componentes

### 4. Repository Pattern
- `EmpleadoDAO` actúa como repositorio, abstraiendo la lógica de persistencia
- Los servlets no necesitan conocer detalles de JDBC

### 5. Separation of Concerns
- **Validaciones en Backend**: Garantizan integridad de datos
- **Validaciones en Frontend**: Mejoran la experiencia del usuario
- **Try-with-resources**: Gestión automática de recursos JDBC

## Manejo de Excepciones y Logging

### Logging
- Uso de `java.util.logging.Logger` para registrar operaciones importantes
- Niveles de log: `INFO` (operaciones), `SEVERE` (errores críticos), `WARNING` (advertencias)

### Manejo de Errores
- **SQLException**: Capturada y registrada con detalles en el backend
- **NumberFormatException**: Validada en conversión de ID
- **JsonSyntaxException**: Manejada en parsing de JSON
- **Try-with-resources**: Garantiza liberación de recursos (conexiones, PreparedStatements)

## Pruebas Manuales

### Caso 1: Agregar empleado válido
1. Completar formulario con datos válidos
2. Salario: 2.500.000+
3. Bono: 0-1.250.000 (máximo 50%)
4. Click en "Finalizar Registro"
5. ✓ Empleado aparece en la tabla

### Caso 2: Salario por debajo del mínimo
1. Nombre: "Juan"
2. Salario: 300.000
3. Click en "Finalizar Registro"
4. ✓ Error mostrado dinámicamente: "El salario base no puede ser menor a $400.000."

### Caso 3: RUT duplicado
1. Intentar agregar empleado con RUT existente
2. ✓ Error: "El RUT/DNI ya se encuentra registrado."

### Caso 4: Bono superior al 50%
1. Salario: 1.000.000
2. Bono: 600.000 (60%)
3. ✓ Error: "Los bonos no pueden superar el 50% del salario base."

### Caso 5: Eliminar empleado
1. Click en botón "Eliminar" de un registro
2. Confirmar en modal
3. ✓ Empleado eliminado de la tabla

## Buenas Prácticas Aplicadas

1. **Código Limpio**: Nombres descriptivos, métodos pequeños y enfocados
2. **Documentación**: Comentarios en métodos complejos, docstrings en clases
3. **Validación Multicapa**: Frontend + Backend para máxima seguridad
4. **Gestión de Recursos**: Try-with-resources para cerrar conexiones automáticamente
5. **Respuestas HTTP Semánticas**:
   - `201 Created`: POST exitoso
   - `204 No Content`: DELETE exitoso
   - `400 Bad Request`: Validación fallida
   - `500 Internal Server Error`: Error de servidor
6. **Formato JSON Consistente**: Respuestas estructuradas con "errores" o "error"
7. **Seguridad**: PreparedStatements contra SQL injection

## Configuración de Base de Datos

La aplicación usa **H2 en memoria** que se inicializa automáticamente con:

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
```

**Tabla creada en schema.sql:**
- `id`: BIGINT AUTO_INCREMENT PRIMARY KEY
- `nombre`, `apellido`: VARCHAR(100) NOT NULL
- `rut`: VARCHAR(20) NOT NULL UNIQUE
- `cargo`: VARCHAR(100) NOT NULL
- `salario`: DOUBLE NOT NULL
- `bono`: DOUBLE DEFAULT 0
- `descuento`: DOUBLE DEFAULT 0

## Troubleshooting

### Puerto 8080 ocupado
```bash
# En Windows
netstat -ano | findstr :8080

# En Mac/Linux
lsof -i :8080

# Cambiar puerto en application.properties
server.port=8081
```

### Error "B2 Database not found"
- Asegurar que H2 está en el pom.xml
- Ejecutar `mvn clean install`

### Cambios no se reflejan
- Ejecutar `mvn clean compile`
- Reiniciar la aplicación

## Roadmap Futuro

- [ ] Agregar roles y autenticación
- [ ] Paginación en la lista de empleados
- [ ] Exportar a CSV/PDF
- [ ] Historial de cambios
- [ ] Migración a una BD persistente (PostgreSQL)
- [ ] API RESTful con Spring REST Controllers
- [ ] Frontend con React/Vue

## Autor

Desarrollado como solución del Desafío Técnico - Previred

## Licencia

Todos los derechos reservados.
