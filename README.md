# Guia del desafio Empleados

## Enfoque de arquitectura

Ocupa `spring-boot-starter-web`. para el arranque con aplicationRUN con tomcat embebido la clase se llama: EmpleadosApplication.java


- Los endpoints principales se implementan como Servlets donde los controladores del servlet estan en: EmpleadoServlet.java


- Los servlets se registran em clase ServletConfig.java donde aqui esta la comunicacion con el levantamiento de la pagina y luego su referencia de API mediante FETCH para la activavion del servlet


- La persistencia con JDC y H2 esta en la configuracion en clase DatabaseInitializer.java


## Qué incluye Resumen

- Clase `EmpleadosApplication` con `main()` para ejecutar como Spring Boot App
- Tomcat embebido vía `spring-boot-starter-web`
- Servlet `HomeServlet` para servir la vista principal
- Servlet `EmpleadoServlet` para API AJAX
- Logging y manejo centralizado de errores esta en la carpeta logs 
- SOLID y patrones básicos por capas

## Cómo ejecutar

### Desde Eclipse o terminal
```bash
mvn spring-boot:run
```

O bien:
```bash
mvn clean package
java -jar target/empleados-1.0.0.jar
```

## URL
```text
http://localhost:8080/home
```

## Estructura

```text
/previred
├── logs
│   └── app.log
├── menuprincipal.png
├── pom.xml
├── README.md
├── src
│   ├── main
│   │   ├── java
│   │   │   └── cl
│   │   │       └── previred
│   │   │           ├── config
│   │   │           │   ├── AppConfig.java
│   │   │           │   ├── DatabaseInitializer.java
│   │   │           │   └── ServletConfig.java
│   │   │           ├── dto
│   │   │           │   ├── ApiResponse.java
│   │   │           │   ├── EmpleadoRequest.java
│   │   │           │   └── EmpleadoResponse.java
│   │   │           ├── EmpleadosApplication.java
│   │   │           ├── exception
│   │   │           │   ├── AppException.java
│   │   │           │   ├── ResourceNotFoundException.java
│   │   │           │   └── ValidationException.java
│   │   │           ├── mapper
│   │   │           │   └── EmpleadoMapper.java
│   │   │           ├── model
│   │   │           │   └── Empleado.java
│   │   │           ├── repository
│   │   │           │   ├── EmpleadoRepository.java
│   │   │           │   └── impl
│   │   │           │       └── EmpleadoJdbcRepository.java
│   │   │           ├── service
│   │   │           │   ├── EmpleadoService.java
│   │   │           │   └── impl
│   │   │           │       └── EmpleadoServiceImpl.java
│   │   │           ├── servlet
│   │   │           │   ├── EmpleadoServlet.java
│   │   │           │   └── HomeServlet.java
│   │   │           └── util
│   │   │               ├── AppExceptionHandler.java
│   │   │               ├── DatabaseUtil.java
│   │   │               ├── JsonUtils.java
│   │   │               └── ValidationUtils.java
│   │   └── resources
│   │       ├── application.properties
│   │       ├── logback-spring.xml
│   │       └── static
│   │           ├── css
│   │           │   └── styles.css
│   │           ├── index.html
│   │           └── js
│   │               └── app.js
│   └── test
│       └── java
│           └── cl
│               └── previred
│                   └── ApplicationTests.java

```


