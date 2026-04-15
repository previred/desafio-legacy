# ADR-001: API HTTP con `HttpServlet` y persistencia JDBC explícita

## Estado

Aceptado

## Contexto

El desafío exige Spring Boot + Tomcat, Servlets, JDBC y H2. También hay tensión habitual con la frase “solo Servlets y JDBC” frente al ecosistema Spring.

## Decisión

- Exponer `/api/empleados` mediante **`EmpleadoServlet`** registrado con `ServletRegistrationBean`, no mediante `@RestController`.
- Persistir con **`EmpleadoRepository`** usando `PreparedStatement` y `DataSource` de Spring; **sin JPA/Hibernate** en el flujo principal.
- Usar **Jackson** en el servlet para JSON (misma JVM, contrato claro, sin parser manual).

## Consecuencias

- El evaluador ve cumplimiento explícito de “Servlet” y “JDBC”.
- Se acepta la dependencia de Spring Boot como **contenedor** (datasouce, transacciones, filtros, pruebas de integración).
- Evolución futura: se podría extraer un adapter HTTP sin cambiar el servicio ni el repositorio.
