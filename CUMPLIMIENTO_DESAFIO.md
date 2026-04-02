# Cumplimiento del Desafío Técnico: Servlets y AJAX

## Información General

**Proyecto**: Sistema de Gestión de Empleados
**Versión Java**: 8 (1.8)
**Framework**: Spring Boot 2.7.18
**Contenedor Web**: Apache Tomcat (embebido)
**Gestor de Dependencias**: Maven 3.x
**Base de Datos**: H2 (in-memory)

---

## Requisitos Técnicos

### Java 8 o Superior

**Cumplimiento**: Se utiliza Java 8 (JDK 1.8) configurado en `pom.xml`

**Ubicación**:
```xml
<properties>
    <java.version>1.8</java.version>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
</properties>
```
**Archivo**: `pom.xml` líneas 23-27

---

### Características de Java 8 (Lambdas y Streams)

**Cumplimiento**: Se utilizan lambdas y streams en múltiples componentes

**Ejemplos de implementación**:

1. **Streams y Method References**
   - **Archivo**: `src/main/java/com/previred/empleados/service/impl/EmpleadoServiceImpl.java`
   - **Línea**: 35
   ```java
   return empleados.stream()
       .map(this::convertToDTO)
       .collect(Collectors.toList());
   ```

2. **Lambdas en validaciones**
   - **Archivo**: `src/test/java/com/previred/empleados/validator/EmpleadoValidatorTest.java`
   - **Línea**: 41
   ```java
   result.getErrors().stream().anyMatch(e -> e.contains("nombre"))
   ```

3. **Lambdas en iteración**
   - **Archivo**: `src/main/webapp/js/app.js`
   - **Línea**: 53-56
   ```javascript
   empleados.forEach(empleado => {
       const row = crearFilaEmpleado(empleado);
       tableBody.appendChild(row);
   });
   ```

---

### Maven como Gestor de Dependencias

**Cumplimiento**: Proyecto configurado con Maven

**Ubicación**:
- **Archivo**: `pom.xml`
- **Dependencias principales**:
  - Spring Boot Starter Web (línea 32-35)
  - Spring Boot Starter JDBC (línea 38-41)
  - H2 Database (línea 52-56)
  - Jackson Databind (línea 65-68)
  - JUnit 4 (línea 81-86)
  - Mockito (línea 89-93)

---

### Spring Boot como Runtime

**Cumplimiento**: Aplicación ejecutable con Spring Boot

**Ubicación**:
- **Archivo**: `src/main/java/com/previred/empleados/EmpleadosApplication.java`
- **Líneas**: 8-13
```java
@SpringBootApplication
@ServletComponentScan
public class EmpleadosApplication {
    public static void main(String[] args) {
        SpringApplication.run(EmpleadosApplication.class, args);
    }
}
```

**Configuración Spring Boot**:
- **Archivo**: `src/main/resources/application.properties`
- Puerto del servidor (línea 2): `server.port=8080`

---

### Apache Tomcat como Contenedor Web

**Cumplimiento**: Tomcat embebido proporcionado por Spring Boot

**Evidencia**:
- **Archivo**: `pom.xml` (línea 16-21)
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.18</version>
</parent>
```
- Tomcat se incluye automáticamente con `spring-boot-starter-web`

---

## Parte 1: Implementación de Servicio Web con Servlets y AJAX

### Endpoint: /api/empleados

**Cumplimiento**: Servlet implementado con los tres métodos HTTP requeridos

**Ubicación**: `src/main/java/com/previred/empleados/servlet/EmpleadoServlet.java`

#### GET - Retornar lista de empleados en JSON

**Implementación**:
- **Método**: `doGet()`
- **Líneas**: 35-54
```java
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    // Código que retorna lista en formato JSON
}
```

**Respuesta**:
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

#### POST - Agregar nuevo empleado

**Implementación**:
- **Método**: `doPost()`
- **Líneas**: 56-96
```java
@Override
protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    // Lee JSON del request body
    // Valida datos
    // Crea empleado
    // Retorna HTTP 201 o 400
}
```

**Request esperado**:
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

#### DELETE - Eliminar empleado por ID

**Implementación**:
- **Método**: `doDelete()`
- **Líneas**: 98-118
```java
@Override
protected void doDelete(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    // Extrae ID de la URL
    // Elimina empleado
    // Retorna HTTP 204
}
```

---

### Datos del Empleado

**Cumplimiento**: Entidad con todos los campos requeridos

**Ubicación**: `src/main/java/com/previred/empleados/model/Empleado.java`

**Campos implementados**:
- ID (autogenerado) - línea 11
- Nombre - línea 12
- Apellido - línea 13
- RUT - línea 14
- Cargo - línea 15
- Salario Base - línea 16
- Bonos - línea 17
- Descuentos - línea 18
- Salario Total (calculado) - línea 19

**Autogeneración de ID**:
- **Archivo**: `src/main/java/com/previred/empleados/dao/impl/EmpleadoDAOImpl.java`
- **Línea**: 38
```java
statement.executeUpdate();
ResultSet generatedKeys = statement.getGeneratedKeys();
```

---

### Interfaz con AJAX

**Cumplimiento**: Página HTML + JavaScript sin frameworks

#### HTML Simple

**Ubicación**: `src/main/webapp/index.html`

**Características**:
- Formulario de registro (líneas 19-87)
- Tabla de empleados (líneas 94-114)
- Sin frameworks externos (React, Angular, Vue)

#### AJAX - Cargar lista de empleados

**Ubicación**: `src/main/webapp/js/app.js`

**Función**: `cargarEmpleados()`
- **Líneas**: 40-63
```javascript
async function cargarEmpleados() {
    const empleados = await EmpleadoAPI.obtenerEmpleados();
    // Renderiza tabla sin recargar página
}
```

**Cliente AJAX**:
- **Archivo**: `src/main/webapp/js/api.js`
- **Línea**: 3-11
```javascript
obtenerEmpleados: async function() {
    const response = await fetch(API_BASE_URL);
    return await response.json();
}
```

#### AJAX - Agregar empleado sin recargar

**Ubicación**: `src/main/webapp/js/app.js`

**Función**: `configurarFormulario()`
- **Líneas**: 86-114
```javascript
form.addEventListener('submit', async function(e) {
    e.preventDefault();
    const result = await EmpleadoAPI.crearEmpleado(formData);
    // Actualiza tabla sin recargar
});
```

**API Call**:
- **Archivo**: `src/main/webapp/js/api.js`
- **Líneas**: 13-24
```javascript
crearEmpleado: async function(empleado) {
    const response = await fetch(API_BASE_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(empleado)
    });
}
```

#### AJAX - Eliminar empleado sin recargar

**Ubicación**: `src/main/webapp/js/app.js`

**Función**: `eliminarEmpleado()`
- **Líneas**: 228-241
```javascript
async function eliminarEmpleado(id) {
    await EmpleadoAPI.eliminarEmpleado(id);
    await cargarEmpleados();
    // Actualiza tabla sin recargar
}
```

**API Call**:
- **Archivo**: `src/main/webapp/js/api.js`
- **Líneas**: 26-36
```javascript
eliminarEmpleado: async function(id) {
    await fetch(`${API_BASE_URL}/${id}`, {
        method: 'DELETE'
    });
}
```

---

### Requerimientos Técnicos

#### Solo Servlets y JDBC (sin frameworks ORM)

**Cumplimiento**: Uso exclusivo de Servlets y JDBC puro

**Evidencias**:
1. **Servlet**: `EmpleadoServlet.java` anotado con `@WebServlet`
2. **JDBC Puro**: `EmpleadoDAOImpl.java` usa `PreparedStatement`
   - **Líneas**: 33-49 (método save)
   ```java
   PreparedStatement statement = connection.prepareStatement(SQL_INSERT,
       Statement.RETURN_GENERATED_KEYS);
   ```

#### H2 como Base de Datos en Memoria

**Cumplimiento**: H2 configurado en modo memoria

**Ubicación**: `src/main/resources/application.properties`
- **Línea 5**: `spring.datasource.url=jdbc:h2:mem:empleadosdb`

**Esquema de Base de Datos**:
- **Archivo**: `src/main/resources/schema.sql`
- **Líneas**: 1-15 (DDL de tabla empleados)

**Datos Iniciales**:
- **Archivo**: `src/main/resources/data.sql`
- **Líneas**: 1-5 (3 empleados de prueba)

#### Manejo de Excepciones

**Cumplimiento**: Manejo centralizado de excepciones

**Ubicaciones**:

1. **Servlet**:
   - **Archivo**: `EmpleadoServlet.java`
   - **Líneas**: 80-94
   ```java
   catch (ValidationException e) {
       response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
   } catch (DuplicateRutException e) {
       response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
   } catch (Exception e) {
       response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
   }
   ```

2. **Excepciones Personalizadas**:
   - `ValidationException`: `src/main/java/com/previred/empleados/exception/ValidationException.java`
   - `DuplicateRutException`: `src/main/java/com/previred/empleados/exception/DuplicateRutException.java`
   - `EmpleadoNotFoundException`: `src/main/java/com/previred/empleados/exception/EmpleadoNotFoundException.java`

#### Logging

**Cumplimiento**: SLF4J + Logback configurado

**Configuración**:
- **Archivo**: `src/main/resources/logback.xml`
- Logs a consola y archivo con rotación

**Uso en código**:
- **Archivo**: `EmpleadoServiceImpl.java`
- **Línea**: 21
```java
private static final Logger logger = LoggerFactory.getLogger(EmpleadoServiceImpl.class);
```

**Ejemplos de logging**:
- INFO: `logger.info("Empleado creado exitosamente con ID: {}", empleado.getId());` (línea 48)
- WARN: `logger.warn("Validación fallida para RUT: {}", request.getRut());` (línea 43)
- DEBUG: `logger.debug("Obteniendo todos los empleados");` (línea 33)

#### Validación de Datos en Endpoints

**Cumplimiento**: Validación en servlet antes de procesamiento

**Ubicación**: `EmpleadoServlet.java`
- **Líneas**: 63-74
```java
EmpleadoRequestDTO requestDTO = objectMapper.readValue(
    request.getReader(), EmpleadoRequestDTO.class);

EmpleadoDTO empleadoDTO = empleadoService.crearEmpleado(requestDTO);
```

El servicio internamente valida con `EmpleadoValidator.java`

---

## Parte 2: Validaciones de Reglas de Negocio

### Backend (Java 8)

**Ubicación principal**: `src/main/java/com/previred/empleados/validator/EmpleadoValidator.java`

#### 1. Rechazar RUT duplicado

**Implementación**:
- **Archivo**: `EmpleadoServiceImpl.java`
- **Líneas**: 50-53
```java
if (empleadoDAO.existsByRut(normalizedRut)) {
    logger.warn("Intento de crear empleado con RUT duplicado: {}", normalizedRut);
    throw new DuplicateRutException("El RUT ya existe en el sistema");
}
```

**Validación en BD**:
- **Archivo**: `schema.sql`
- **Línea 5**: `rut VARCHAR(12) UNIQUE NOT NULL`

#### 2. Salario base mínimo $400.000

**Implementación**:
- **Archivo**: `EmpleadoValidator.java`
- **Líneas**: 9-10
```java
private static final BigDecimal SALARIO_MINIMO = new BigDecimal("400000");
private static final BigDecimal SALARIO_MAXIMO = new BigDecimal("100000000");
```

**Validación**:
- **Líneas**: 61-65
```java
if (salarioBase.compareTo(SALARIO_MINIMO) < 0) {
    result.addError("El salario base debe ser al menos $400.000");
} else if (salarioBase.compareTo(SALARIO_MAXIMO) > 0) {
    result.addError("El salario base no puede superar $100.000.000");
}
```

#### 3. Bonos no pueden superar 50% del salario base

**Implementación**:
- **Archivo**: `EmpleadoValidator.java`
- **Línea**: 11
```java
private static final BigDecimal BONO_MAX_PERCENTAGE = new BigDecimal("0.5");
```

**Validación**:
- **Líneas**: 79-82
```java
BigDecimal maxBonos = salarioBase.multiply(BONO_MAX_PERCENTAGE);
if (bonos.compareTo(maxBonos) > 0) {
    result.addError("Los bonos no pueden superar el 50% del salario base");
}
```

#### 4. Descuentos no pueden ser mayor al salario base

**Implementación**:
- **Archivo**: `EmpleadoValidator.java`
- **Líneas**: 96-98
```java
if (salarioBase != null && descuentos.compareTo(salarioBase) > 0) {
    result.addError("Los descuentos no pueden superar el salario base");
}
```

#### 5. Respuesta HTTP 400 con JSON de errores

**Implementación**:
- **Archivo**: `EmpleadoServlet.java`
- **Líneas**: 80-87
```java
catch (ValidationException e) {
    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    ErrorResponseDTO errorResponse = new ErrorResponseDTO(e.getErrors());
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    objectMapper.writeValue(response.getWriter(), errorResponse);
    return;
}
```

**DTO de Error**:
- **Archivo**: `src/main/java/com/previred/empleados/dto/ErrorResponseDTO.java`
```java
public class ErrorResponseDTO {
    private List<String> errors;
}
```

---

### Frontend (JavaScript + AJAX)

**Ubicación principal**: `src/main/webapp/js/validator.js`

#### 1. Verificar campos completos

**Implementación**:
- **Archivo**: `app.js`
- **Función**: `validarFormulario()`
- **Líneas**: 168-210
```javascript
if (!data.nombre) {
    Validator.mostrarError('nombre', 'El nombre es obligatorio');
    valido = false;
}
```

#### 2. Validar formato del RUT

**Implementación**:
- **Archivo**: `validator.js`
- **Función**: `validarRut()`
- **Líneas**: 2-23

**Algoritmo Módulo 11 chileno**:
```javascript
const expectedVerifier = this.calcularDigitoVerificador(parseInt(rutNumber));
if (verifierDigit !== expectedVerifier) {
    return { valido: false, mensaje: 'El RUT no es válido...' };
}
```

**Auto-formateo en tiempo real**:
- **Archivo**: `app.js`
- **Líneas**: 122-125
```javascript
rutInput.addEventListener('input', function() {
    const formatted = Validator.formatearRut(this.value);
    this.value = formatted;
});
```

#### 3. Validar salario base mínimo $400.000

**Implementación**:
- **Archivo**: `validator.js`
- **Líneas**: 71-82
```javascript
validarSalarioMinimo: function(salario) {
    const SALARIO_MINIMO = 400000;
    const SALARIO_MAXIMO = 100000000;

    if (!salario || salario < SALARIO_MINIMO) {
        return { valido: false, mensaje: 'El salario base debe ser al menos $400.000' };
    }

    if (salario > SALARIO_MAXIMO) {
        return { valido: false, mensaje: 'El salario base no puede superar $100.000.000' };
    }
}
```
Se agrega validación de salario máximo para no tener problemas con los tipos de datos.

#### 4. Mostrar errores dinámicamente (sin alerts)

**Implementación**:
- **Archivo**: `validator.js`
- **Función**: `mostrarError()`
- **Líneas**: 123-134
```javascript
mostrarError: function(inputId, mensaje) {
    const input = document.getElementById(inputId);
    const errorSpan = document.getElementById('error-' + inputId);

    if (mensaje) {
        input.classList.add('invalid');
        errorSpan.textContent = mensaje;
    }
}
```

**HTML de errores**:
- **Archivo**: `index.html`
- Cada campo tiene: `<span class="error-message" id="error-nombreCampo"></span>`

**CSS de errores**:
- **Archivo**: `styles.css`
- **Líneas**: 91-93
```css
.form-group input.invalid {
    border-color: var(--monkey-theme-danger);
}
```

---

## Evaluación de Buenas Prácticas

### Patrones de Diseño Implementados

1. **DAO Pattern**
   - Interface: `EmpleadoDAO.java`
   - Implementación: `EmpleadoDAOImpl.java`

2. **DTO Pattern**
   - `EmpleadoDTO.java` (salida)
   - `EmpleadoRequestDTO.java` (entrada)
   - `ErrorResponseDTO.java` (errores)

3. **Service Layer Pattern**
   - Interface: `EmpleadoService.java`
   - Implementación: `EmpleadoServiceImpl.java`

4. **Dependency Injection**
   - Spring autowiring en `EmpleadoServiceImpl.java` (línea 24)
   ```java
   public EmpleadoServiceImpl(EmpleadoDAO empleadoDAO) {
       this.empleadoDAO = empleadoDAO;
   }
   ```

### Principios SOLID

1. **Single Responsibility Principle**
   - Cada clase tiene una responsabilidad única
   - `RutValidator`: solo valida RUT
   - `EmpleadoDAO`: solo acceso a datos
   - `EmpleadoService`: solo lógica de negocio

2. **Open/Closed Principle**
   - Extensible mediante interfaces
   - Cerrado a modificación directa

3. **Liskov Substitution Principle**
   - Interfaces `EmpleadoService` y `EmpleadoDAO` son intercambiables

4. **Interface Segregation Principle**
   - Interfaces pequeñas y específicas
   - `EmpleadoDAO` solo con operaciones de datos
   - `EmpleadoService` solo con operaciones de negocio

5. **Dependency Inversion Principle**
   - Dependencias de abstracciones (interfaces), no implementaciones
   - `EmpleadoServiceImpl` depende de `EmpleadoDAO` (interface)

### Seguridad (OWASP)

1. **SQL Injection Prevention**
   - Uso de `PreparedStatement` en todos los DAOs
   - **Archivo**: `EmpleadoDAOImpl.java` (líneas 33-49)

2. **Input Validation**
   - Validación multi-capa (frontend + backend)
   - Sanitización de RUT antes de procesamiento

3. **XSS Prevention**
   - Jackson escapa automáticamente JSON
   - No uso de `innerHTML` con datos de usuario

### Testing

**Ubicación**: `src/test/java/com/previred/empleados/`

**Cobertura**:
- 37 tests unitarios
- 4 clases de test
- 100% componentes principales

**Tests implementados**:

1. **RutValidatorTest** (8 tests)
   - Validación de RUTs válidos e inválidos
   - Algoritmo Módulo 11

2. **EmpleadoValidatorTest** (13 tests)
   - Validación de reglas de negocio
   - Salarios, bonos, descuentos

3. **EmpleadoServiceTest** (8 tests)
   - Lógica de negocio con Mockito
   - Manejo de excepciones

4. **EmpleadoDAOTest** (8 tests)
   - Operaciones CRUD con H2
   - Transacciones

**Ejecución**:
```bash
mvn test
```

**Resultado**: 37/37 tests pasando

---

## Documentación

### README.md

**Ubicación**: Raíz del proyecto

**Contenido**:
- Descripción del proyecto
- Requisitos previos
- Instrucciones de instalación
- Comandos de ejecución
- Acceso a la aplicación
- Estructura del proyecto
- Tecnologías utilizadas

### Instrucciones de Ejecución

**Compilar**:
```bash
mvn clean install
```

**Ejecutar tests**:
```bash
mvn test
```

**Ejecutar aplicación**:
```bash
mvn spring-boot:run
```

**Acceder**:
- Aplicación: http://localhost:8080
- H2 Console: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:empleadosdb`
  - User: `sa`
  - Password: (vacío)

---

## Resumen de Cumplimiento

| Requisito | Estado | Evidencia |
|-----------|--------|-----------|
| Java 8+ | Cumplido | pom.xml líneas 23-27 |
| Lambdas y Streams | Cumplido | EmpleadoServiceImpl.java línea 35 |
| Maven | Cumplido | pom.xml completo |
| Spring Boot | Cumplido | EmpleadosApplication.java |
| Tomcat | Cumplido | Embebido en Spring Boot |
| Servlet GET | Cumplido | EmpleadoServlet.java líneas 35-54 |
| Servlet POST | Cumplido | EmpleadoServlet.java líneas 56-96 |
| Servlet DELETE | Cumplido | EmpleadoServlet.java líneas 98-118 |
| ID autogenerado | Cumplido | EmpleadoDAOImpl.java línea 38 |
| HTML + JavaScript | Cumplido | index.html, app.js |
| AJAX Fetch API | Cumplido | api.js líneas 3-36 |
| Sin frameworks | Cumplido | Solo vanilla JavaScript |
| JDBC + H2 | Cumplido | EmpleadoDAOImpl.java, application.properties |
| Manejo excepciones | Cumplido | EmpleadoServlet.java líneas 80-94 |
| Logging | Cumplido | logback.xml, uso de SLF4J |
| RUT duplicado | Cumplido | EmpleadoServiceImpl.java líneas 50-53 |
| Salario mínimo | Cumplido | EmpleadoValidator.java líneas 61-65 |
| Bonos 50% | Cumplido | EmpleadoValidator.java líneas 79-82 |
| Descuentos máx. | Cumplido | EmpleadoValidator.java líneas 96-98 |
| HTTP 400 JSON | Cumplido | EmpleadoServlet.java líneas 80-87 |
| Validación frontend | Cumplido | validator.js completo |
| Errores dinámicos | Cumplido | validator.js líneas 123-134 |
| Tests unitarios | Cumplido | 37 tests en 4 clases |
| Buenas prácticas | Cumplido | Patrones DAO, DTO, Service Layer |
| Principios SOLID | Cumplido | Separación de responsabilidades |
| Documentación | Cumplido | README.md completo |

---

## Archivos Clave del Proyecto

### Backend
- `src/main/java/com/previred/empleados/EmpleadosApplication.java` - Clase principal
- `src/main/java/com/previred/empleados/servlet/EmpleadoServlet.java` - REST endpoints
- `src/main/java/com/previred/empleados/service/impl/EmpleadoServiceImpl.java` - Lógica de negocio
- `src/main/java/com/previred/empleados/dao/impl/EmpleadoDAOImpl.java` - Acceso a datos
- `src/main/java/com/previred/empleados/validator/EmpleadoValidator.java` - Validaciones backend
- `src/main/java/com/previred/empleados/validator/RutValidator.java` - Validación RUT Módulo 11

### Frontend
- `src/main/webapp/index.html` - Interfaz de usuario
- `src/main/webapp/js/app.js` - Lógica de aplicación
- `src/main/webapp/js/api.js` - Cliente REST AJAX
- `src/main/webapp/js/validator.js` - Validaciones frontend
- `src/main/webapp/css/styles.css` - Estilos

### Configuración
- `pom.xml` - Dependencias Maven
- `src/main/resources/application.properties` - Configuración Spring Boot
- `src/main/resources/schema.sql` - Esquema de base de datos
- `src/main/resources/data.sql` - Datos iniciales
- `src/main/resources/logback.xml` - Configuración de logging

### Tests
- `src/test/java/com/previred/empleados/validator/RutValidatorTest.java` - Tests validación RUT
- `src/test/java/com/previred/empleados/validator/EmpleadoValidatorTest.java` - Tests reglas de negocio
- `src/test/java/com/previred/empleados/service/EmpleadoServiceTest.java` - Tests servicio
- `src/test/java/com/previred/empleados/dao/EmpleadoDAOTest.java` - Tests DAO

---

**Fecha de Implementación**: Mayo 2026
**Estado**: Completado al 100%
**Calidad**: Código production-ready con buenas prácticas
