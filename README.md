# Desafío Técnico Previred

![Java](https://img.shields.io/badge/Java-8-ED8B00?style=flat-square&logo=openjdk&logoColor=white) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-2.7.x-6DB33F?style=flat-square&logo=spring-boot&logoColor=white) ![Servlets](https://img.shields.io/badge/API-Servlets_Nativos-007396?style=flat-square&logo=java&logoColor=white) ![OpenAPI](https://img.shields.io/badge/Docs-OpenAPI_3.0-85EA2D?style=flat-square&logo=swagger&logoColor=black)

Solución al desafío técnico implementando Servlets, JDBC puro, Fetch API, respetando el uso estricto de Java 8.

> **Ver Enunciado Original:** Puedes consultar las reglas y requerimientos exactos de la prueba en el documento [INSTRUCCIONES.md](./INSTRUCCIONES.md).

## Stack Tecnológico
- **Backend:** Java 8, Servlet API nativa (`@WebServlet`), habilitada vía `@ServletComponentScan` con Spring Boot 2.7 (actuando como Runtime).
- **Base de Datos:** H2 In-Memory + Spring JDBC (`JdbcTemplate`)
- **JSON:** Gson (serialización y deserialización de JSON)
- **Frontend:** HTML5, CSS, Vanilla JavaScript.
- **Documentación:** OpenAPI 3.0 (Swagger UI)

## Arquitectura y Decisiones
Siguiendo los lineamientos solicitados:
- **3 Capas:** `Controller` > `Service` > `Repository`.
- **Independencia del modelo:** La API recibe e interactúa vía `EmpleadoDTO`. El `EmpleadoEntity` es exclusivo de la capa de persistencia.
- **Logging:** Trazabilidad completa con `SLF4J` (INFO para accesos, DEBUG para lógica interna, WARN para validaciones rechazadas, ERROR para fallos).
- **Validaciones y Errores:** Validaciones estrictas según reglas de negocio, manejadas por un `GlobalExceptionHandler` que retorna JSON puro.
- **Desglose de Salario:** El campo "Salario" del enunciado (Parte 1) se descompone en `salarioBase`, `bonos` y `descuentos` para poder implementar las reglas de negocio de la Parte 2 (topes de bonos, límites de descuentos). El **sueldo líquido** se calcula como: `salarioBase + bonos - descuentos`.
- **Documentación OpenAPI:** La API usa la especificación OpenAPI 3.0 (`openapi.yml`) y se visualiza mediante Swagger-UI. De esta forma se cumple el "Tip Excepcional" del desafío respetando la arquitectura de Servlets.

---

## Requisitos Previos
- **Java Development Kit (JDK):** Versión 8 (1.8).
- **Apache Maven:** Versión 3.5 o superior (Requisito oficial de Spring Boot 2.7.x).
- **Puerto:** El puerto `8080` debe estar libre en la máquina local.

---

## Instalación y Ejecución
**1. Clonar el repositorio:**
```text
git clone https://github.com/unicoast/desafio-legacy.git
cd desafio-legacy
```

**2. Levantar el proyecto (opción A - desarrollo):**
```text
mvn clean spring-boot:run
```

**Levantar el proyecto (opción B - JAR empaquetado):**
```text
mvn clean package -DskipTests
java -jar target/desafio-legacy-1.0.0.jar
```

**3. Accesos Locales:**
- **Frontend:** http://localhost:8080
- **Base de Datos (H2 Console):** http://localhost:8080/h2-console *(URL: `jdbc:h2:mem:desafioDB` | User: `sa` | Password: vacía. Al ser en memoria, los datos se reinician con cada ejecución)*
- **Documentación API (Swagger):** http://localhost:8080/swagger-ui/index.html

## Pruebas Rápidas (cURL)

**Listar Empleados (Mac/Linux - Bash/Zsh):**
```bash
curl http://localhost:8080/api/empleados
```

**Listar Empleados (Windows - PowerShell):**
```powershell
curl.exe http://localhost:8080/api/empleados
```

**Registrar Empleado (Mac/Linux - Bash/Zsh):**
```bash
curl -X POST http://localhost:8080/api/empleados \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Nicolás","apellido":"Astudillo","rut":"12345678-9","cargo":"Desarrollador","salarioBase":850000,"bonos":100000,"descuentos":50000}'
```

**Registrar Empleado (Windows - PowerShell):**
```powershell
curl.exe -X POST http://localhost:8080/api/empleados `
  -H "Content-Type: application/json" `
  -d '{"nombre":"Nicolás","apellido":"Astudillo","rut":"12345678-9","cargo":"Desarrollador","salarioBase":850000,"bonos":100000,"descuentos":50000}'
```

**Eliminar Empleado (Mac/Linux - Bash/Zsh):**
```bash
curl -X DELETE "http://localhost:8080/api/empleados?id=1"
```

**Eliminar Empleado (Windows - PowerShell):**
```powershell
curl.exe -X DELETE "http://localhost:8080/api/empleados?id=1"
```
