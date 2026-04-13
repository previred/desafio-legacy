
✅ Desafío Técnico – Servlets y AJAX (Java 8)

📌 Descripción General
Este proyecto implementa una aplicación web completa para la gestión de empleados, utilizando Java 8, Servlets nativos, JDBC puro, AJAX (Fetch API) y una base de datos H2 en memoria, ejecutándose sobre Spring Boot únicamente como runtime (Apache Tomcat embebido).
El objetivo del desafío es demostrar dominio del stack Java solicitado, manejo correcto de HTTP, validaciones de reglas de negocio, separación de responsabilidades y una interfaz web simple que opera sin recarga de página.

# 🛠️ Tecnologías Utilizadas

Java 8
Spring Boot 2.7.x (solo como runtime)
Apache Tomcat embebido
Servlets nativos (@WebServlet)
JDBC puro
Base de datos H2 en memoria
Maven
HTML + CSS
JavaScript puro
Fetch API (AJAX nativo)

Restricciones del desafío:

❌ No se utilizan frameworks MVC como Spring MVC
❌ No se utiliza JPA / Hibernate
❌ No se utilizan frameworks frontend (React, Angular, Vue)


# 📂 Estructura del Proyecto

src/main/java
└── com.example.empleados
├── Application.java
├── servlet
│   └── EmpleadoServlet.java
├── model
│   └── Empleado.java
├── dao
│   └── EmpleadoDAO.java
└── util
└── DBConnection.java

src/main/resources
├── application.properties
└── static
└── index.html

pom.xml
README.md


# ▶️ Cómo Ejecutar el Proyecto

✅ Requisitos Previos

Java 8 o superior
Maven 3 o superior

▶️ Ejecución
Desde la raíz del proyecto ejecutar:
mvn spring-boot:run
También es posible ejecutar directamente la clase principal:
com.example.empleados.Application


# 🌐 Accesos Importantes


Frontend (HTML + AJAX):
http://localhost:8080/index.html


API REST (Servlet):
http://localhost:8080/api/empleados


Consola H2:
http://localhost:8080/h2-console



# 🗄 Base de Datos H2

La base de datos utilizada es H2 en memoria y se inicializa automáticamente al arrancar la aplicación.
🔑 Conexión a la Consola H2

Driver Class: org.h2.Driver
JDBC URL: jdbc:h2:mem:empleadosdb
Usuario: sa
Password: (vacío)

📋 Tabla creada automáticamente
SQLEMPLEADOS (  id IDENTITY,  nombre VARCHAR,  apellido VARCHAR,  rut VARCHAR UNIQUE,  cargo VARCHAR,  salario DOUBLE)``


#  APIS y reglas de negocio

📡 API REST – Endpoints
🔹 GET /api/empleados
Obtiene la lista completa de empleados almacenados.
Ejemplo de respuesta:
JSON[  {    "id": 1,    "nombre": "José",    "apellido": "Pérez",    "rut": "19256644-8",    "cargo": "Pescador",    "salario": 400000  }]

🔹 POST /api/empleados
Crea un nuevo empleado.
Body (JSON):
JSON{  "nombre": "Juan",  "apellido": "Gómez",  "rut": "12345678-9",  "cargo": "Analista",  "salario": 650000}``
Respuestas posibles:

201 Created → Empleado creado correctamente
400 Bad Request → Errores de validación (retorna JSON con mensajes)


🔹 DELETE /api/empleados?id={id}
Elimina un empleado existente por su ID.
Ejemplo:
JSONDELETE /api/empleados?id=1
Respuestas posibles:

204 No Content → Eliminado correctamente
404 Not Found → Empleado no existe
400 Bad Request → Parámetro inválido


✅ Reglas de Negocio – Backend
Las validaciones se realizan en el servidor:

Nombre obligatorio
Apellido obligatorio
RUT / DNI obligatorio
RUT / DNI único
Salario base no puede ser menor a $400.000
Manejo correcto de estados HTTP
Errores retornados en formato JSON

Ejemplo de error:
JSON[  "Salario base no puede ser menor a 400.000",  "RUT/DNI duplicado"]

✅ Validaciones Frontend (AJAX)
Antes de enviar el formulario se validan:

Campos obligatorios
Formato básico de RUT / DNI
Salario mínimo permitido
Errores mostrados dinámicamente en pantalla
No se utilizan alert()
No se recarga la página


# 🧪 Cómo Probar Todo el Sistema

✅ 1. Probar desde el Frontend

Abrir: http://localhost:8080/index.html
Completar el formulario
Enviar datos inválidos y observar mensajes de error
Enviar datos válidos y confirmar que el empleado se agrega a la tabla
Eliminar empleados usando el botón Eliminar


✅ 2. Probar la API Manualmente
Usando Postman, Insomnia o cURL:

GET /api/empleados
POST /api/empleados
DELETE /api/empleados?id=X


✅ 3. Probar Persistencia

Abrir la consola H2
Ejecutar la consulta:

JSONSELECT * FROM EMPLEADOS;

Verificar que los datos coinciden con el frontend


# 🧱 Arquitectura y Buenas Prácticas

Separación clara de responsabilidades:

Servlet → manejo HTTP
DAO → persistencia
Model → dominio


Uso de JDBC con PreparedStatement
Uso de Java 8 (streams, lambdas)
Manejo explícito de errores HTTP
Código simple, claro y mantenible
Spring Boot utilizado solo como contenedor


✅ Cumplimiento del Enunciado

Java 8 o superior ✅
Maven ✅
Spring Boot como runtime ✅
Apache Tomcat ✅
Servlets nativos ✅<
AJAX nativo ✅
H2 en memoria ✅
JDBC ✅
Validaciones backend y frontend ✅
Sin frameworks externos ✅


👤 Autor
Nombre: Rodrigo Muñoz Zapata
Correo: rodrigomzapata@gmail.com
Empresa reclutadora: HF Solutions
Cargo al que postula: Desarrollador Backend Java
=======
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

