# ADR-003: Jackson en el servlet para JSON

## Estado

Aceptado

## Contexto

El servlet debe producir y consumir JSON sin añadir un framework REST completo. Las alternativas son parser manual, templates de texto o biblioteca dedicada.

## Decisión

Usar **Jackson** (`ObjectMapper` de Spring Boot) dentro de `EmpleadoServlet` para:

- Deserializar `EmpleadoAltaRequest` en `POST`.
- Serializar `EmpleadoResponse` en `GET`/`POST` 201 y estructuras de error compartidas con el front.

## Consecuencias

- Dependencia explícita del modelo de binding a anotacionesJackson en DTOs de entrada/salida (`EmpleadoAltaRequest`, `EmpleadoResponse`); el agregado `Empleado` del dominio conserva anotaciones mínimas solo si aún se reutiliza en algún serializado — la API pública prioriza DTOs.
- Tests de contrato deben alinear tipos JSON (números vs strings) con la configuración por defecto del `ObjectMapper` de Spring.

## Alternativas descartadas

- **JSON-B / Gson** duplicarían stack sin ganancia en este alcance.
- **Parsing manual:** más control, más código y más superficie de fallos para un challenge con tiempo limitado.
