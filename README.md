# Desafio Legacy

**Descripción:** 

La estructura del proyecto es un aplicacione web para la gestion de empleados contruido con java 1.8 y javascript como estipula las intrucciones entregadas en el desafio.  La applicacion presenta un CRUD deform asincrona utilizando una capa api y base datos h2 en memoria. 


**Tecnoligas Utilizada:** 
 - Java 1.8
 - Maven
 - SpringBoot (runtime)
 - Java Servlets
 - H2 Database
 - Jacson
 - SLF4J
 - Javascript Vanilla


** Arquitectura y Diseño:**
El proyecto esta construido en capas:
 1. Controlladores: Expone las apis de employees para que se comuniquen con el front.
 2. Servicios: Encapsula lógica de negocios y validaciones. 
 3. Repository: Encapsula la conexión a la base datos.
 4- Front: Interfaz de usuario en html con javascrip y ajax.

### Requisito
 - Java 1.8
 - Maven
 

### Configuración del Entorno de Desarrollo

    ***Clonar repositorio***
    ```bash
        git clone https://github.com/previred/desafio-legacy.git
    ```
    ***Compilar y descargar dependencias***
    ```bash
        mvn clean install
    ```
    ***Ejecutar***
    ```bash
       mvn spring-boot:run
    ```


## Api EndPoint


| Metodo| Endpoint |Descripción|
| ------------------------ | 
| get| api/empleados/ | Retornar la lista de Empleandos|
| post| api/empleados/|Agrega nuevos empleados|
| delete| api/empleados/ |Elimina Empleados|


###Autor
 Christopher Gaete Oliveres - Desarrollador.