# Desafío Previred - Empleados

Solución al desafío técnico. Es una webapp simple para
administrar empleados (listar, agregar, eliminar) usando servlets, JDBC y H2 en
memoria. Spring Boot se usa solo para arrancar Tomcat embebido y resolver las
dependencias, no como framework web.

## Requisitos

- Java 8
- Maven 3.6+

Probado con Oracle OpenJDK 1.8.0_202.

## Cómo correrlo

```bash
mvn spring-boot:run
```

Y abrir http://localhost:8080/ en el navegador.

Si el puerto 8080 está ocupado, cambiarlo en `src/main/resources/application.properties`.

## Endpoints

| Método | URL | Descripción |
|--------|-----|-------------|
| GET    | `/api/empleados`         | Lista todos los empleados |
| POST   | `/api/empleados`         | Crea un empleado (body JSON) |
| DELETE | `/api/empleados?id={id}` | Elimina por id |

Los errores del backend vuelven con HTTP 400 y un JSON tipo:

```json
{ "errors": ["..."], "empleado": { ... } }
```

## Cómo probarlo

**Opción 1: desde el navegador**

Abrir http://localhost:8080/, llenar el formulario y usar los botones de la
tabla. Para revisar los datos persistidos directamente en H2:
http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:desafio`, usuario `sa`,
sin contraseña).

**Opción 2: con curl**

Listar:

```bash
curl http://localhost:8080/api/empleados
```

Crear (caso válido):

```bash
curl -X POST http://localhost:8080/api/empleados \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Juan","apellido":"Perez","rut":"12345678-5","cargo":"Dev","salarioBase":800000,"bonos":100000,"descuentos":50000}'
```

Eliminar:

```bash
curl -X DELETE "http://localhost:8080/api/empleados?id=1"
```

**Casos de error para probar**:

- Repetir el mismo RUT dos veces → "Ya existe un empleado con el RUT...".
- Salario menor a 400000 → "El salario base debe ser mayor o igual a $400000".
- Bonos > 50% del salario → "Los bonos no pueden superar el 50% del salario base".
- Descuentos > salario → "Los descuentos no pueden superar el salario base".
- RUT con dígito verificador inválido → "El RUT no tiene un formato valido".
