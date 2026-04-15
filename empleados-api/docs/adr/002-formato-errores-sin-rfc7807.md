# ADR-002: Formato de errores 400 y 500 sin RFC 7807

## Estado

Aceptado

## Contexto

El enunciado pide **HTTP 400** con JSON que indique errores de validación. RFC 7807 (*Problem Details*) es un estándar útil pero añade campos (`type`, `title`, `status`, `instance`) y expectativas en clientes que no son obligatorias en este challenge.

## Decisión

- **400 (validación / negocio / JSON ilegible):** cuerpo `{ "errores": [ { "campo", "mensaje" } ] }`.  
  Para JSON no parseable se usa `campo: "cuerpo"` para unificar el contrato con el front y las pruebas.
- **500 (fallo técnico de persistencia):** `{ "mensaje": "Error interno del servidor." }` sin filtrar causa ni stack al cliente; el detalle vive en logs (incl. correlación `requestId` en MDC).

## Consecuencias clientes

- Contrato simple de integrar en Postman y en el JS actual.
- Migración a RFC 7807 sería un cambio de versión de API o un segundo content-type negociado.

## Alternativas descartadas

- **Solo `mensaje` en 400:** peor para múltiples errores de formulario.
- **RFC 7807 en este repo:** más documentación y overhead para un alcance acotado.
