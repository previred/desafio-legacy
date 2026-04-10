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
