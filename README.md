# Desafío Técnico — Gestión de Empleados

**Nombre:** Daniel Ivan Ramos Truitrui  
**Empresa reclutadora:** Tecnova  
**Correo:** dan.ramostr@gmail.com  
**Cargo:** Desarrollador Java

---

## Descripción

Aplicación web para la gestión de empleados implementada con Java 17, Spring Boot 3.5, Servlets y JDBC con base de datos H2 en memoria. Permite listar, agregar y eliminar empleados mediante una interfaz web con AJAX.

---

## Tecnologías

- Java 17
- Spring Boot 3.5.13
- Apache Tomcat (embebido)
- Servlets (`@WebServlet`)
- JdbcTemplate (JDBC puro, sin JPA/Hibernate)
- H2 Database (en memoria)
- JUnit 5 + Mockito
- HTML + CSS + Fetch API (AJAX)

---

## Arquitectura

El proyecto sigue una arquitectura de 3 capas:

```
Controller  →  Servlet puro (@WebServlet)
Service     →  Lógica de negocio y validaciones
Repository  →  Acceso a datos con JdbcTemplate
```

### Estructura de paquetes

```
src/
├── main/
│   ├── java/com/danielr/desafio/
│   │   ├── DesafioApplication.java
│   │   ├── controller/
│   │   │   └── EmployeeController.java
│   │   ├── dto/
│   │   │   ├── EmployeeRequestDTO.java
│   │   │   └── EmployeeResponseDTO.java
│   │   ├── exception/
│   │   │   └── BusinessException.java
│   │   ├── model/
│   │   │   └── Employee.java
│   │   ├── repository/
│   │   │   └── EmployeeRepository.java
│   │   └── service/
│   │       ├── IEmployeeService.java
│   │       └── impl/
│   │           └── EmployeeServiceImpl.java
│   └── resources/
│       ├── static/
│       │   └── index.html
│       ├── application.properties
│       ├── schema.sql
│       └── data.sql
└── test/
    ├── java/com/danielr/desafio/
    │   ├── repository/
    │   │   └── EmployeeRepositoryTest.java
    │   └── service/
    │       └── EmployeeServiceImplTest.java
    └── resources/
        └── application-test.properties
```

---

## Requisitos previos

- Java 17 o superior
- Maven 3.8 o superior

Verificar versiones instaladas:

```bash
java -version
mvn -version
```

---

## Cómo ejecutar

### 1. Clonar el repositorio

```bash
git clone https://github.com/previred/desafio-legacy.git
cd desafio-legacy
```

### 2. Compilar el proyecto

```bash
mvn clean install
```

### 3. Ejecutar la aplicación

```bash
mvn spring-boot:run
```

La aplicación estará disponible en:

```
http://localhost:8084
```

---

## Endpoints disponibles

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/empleados` | Listar todos los empleados |
| POST | `/api/empleados` | Crear un nuevo empleado |
| DELETE | `/api/empleados/{id}` | Eliminar un empleado por ID |

### Ejemplo POST

```bash
curl -X POST http://localhost:8040/api/empleados \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan",
    "apellido": "Pérez",
    "rut": "12345678-9",
    "cargo": "Desarrollador",
    "salario": 800000,
    "bono": 100000,
    "descuentos": 50000
  }'
```

### Ejemplo DELETE

```bash
curl -X DELETE http://localhost:8040/api/empleados/1
```

---

## Códigos de respuesta HTTP

| Código | Descripción |
|---|---|
| 200 | OK — GET exitoso |
| 201 | Created — POST exitoso |
| 204 | No Content — DELETE exitoso |
| 400 | Bad Request — error de validación |
| 500 | Internal Server Error — error inesperado |

### Respuesta de error (HTTP 400)

```json
{
  "errores": [
    "El salario base no puede ser menor a $400.000",
    "El bono no puede superar el 50% del salario base"
  ]
}
```

---

## Reglas de negocio

- El salario base no puede ser menor a **$400.000**
- El bono no puede superar el **50% del salario base**
- Los descuentos no pueden superar el **salario base**
- No se permiten empleados con **RUT duplicado**
- El borrado de empleados es **lógico** (campo `deleted_at`)

---

## Consola H2

Durante el desarrollo se puede acceder a la consola web de H2 para inspeccionar los datos:

```
URL:      http://localhost:8040/h2-console
JDBC URL: jdbc:h2:mem:desafiodb
Usuario:  sa
Password: (vacío)
```

---

## Ejecutar tests

```bash
mvn test
```

Los tests cubren las capas de **Service** (con Mockito) y **Repository** (con H2 real):

```
EmployeeServiceImplTest   →  14 tests — validaciones de negocio
EmployeeRepositoryTest    →  14 tests — queries SQL con H2
```

---

## Interfaz web

La interfaz está disponible en `http://localhost:8040` e incluye:

- Formulario para agregar empleados con validaciones en tiempo real
- Formato automático del RUT mientras se escribe
- Tabla de empleados con botón de eliminación por fila
- Mensajes de error y éxito dinámicos sin recargar la página
- Comunicación con el backend mediante Fetch API (AJAX)

---

## Coleccion Postman

En la raiz del proyecto se encuentra el archivo `empleados.postman_collection.json` con los 3 endpoints listos para importar y probar en Postman.

Para importarlo:
1. Abre Postman
2. Clic en **Import**
3. Selecciona el archivo `empleados.postman_collection.json`
4. Los endpoints quedaran listos para usar

---

## Docker

### Requisitos

- Docker instalado y corriendo
- Docker Compose instalado

Verificar:

```bash
docker -v
docker-compose -v
```

### Opcion 1: Script automatico (recomendado)

La forma mas simple de levantar la aplicacion es usando el script incluido:

```bash
chmod +x start.sh
./start.sh
```

El script verifica que Docker este instalado y corriendo, construye la imagen y levanta el contenedor automaticamente. La aplicacion quedara disponible en `http://localhost:8040`.

### Opcion 2: Docker Compose manualmente

```bash
# Construir y levantar
docker-compose up --build -d
 
# Ver logs
docker-compose logs -f
 
# Detener
docker-compose down
```

### Opcion 3: Docker manualmente

```bash
# Construir la imagen
docker build -t desafio-empleados .
 
# Correr el contenedor
docker run -p 8040:8040 desafio-empleados
```

### URLs con Docker

```
App:      http://localhost:8040
H2:       http://localhost:8040/h2-console
```