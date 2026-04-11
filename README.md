# Desafío Técnico: Servlets y AJAX

---

## Documentación de Ejecución y Pruebas

### Requisitos Previos

- JDK 8 o superior
- Maven 3.x

### Ejecutar la Aplicación

1. Compilar y ejecutar desde la raíz del proyecto:

```bash
mvn spring-boot:run
```

2. La aplicación estará disponible en: `http://localhost:8080`

### Endpoints de la API

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/empleados` | Lista todos los empleados |
| POST | `/api/empleados` | Crea un nuevo empleado |
| DELETE | `/api/empleados/{id}` | Elimina un empleado por ID |

### Probar la Aplicación

#### Via Navegador (GUI)
1. Abrir `http://localhost:8080` en el navegador
2. Usar el formulario para agregar empleados
3. Ver la lista de empleados en la tabla
4. Eliminar empleados con el botón correspondiente

#### Via API (curl)

**Listar empleados:**
```bash
curl http://localhost:8080/api/empleados
```

**Crear empleado:**
```bash
curl -X POST http://localhost:8080/api/empleados \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Juan","apellido":"Pérez","rut":"12.345.678-9","cargo":"Desarrollador","salario":500000,"bono":100000,"descuentos":50000}'
```

**Eliminar empleado:**
```bash
curl -X DELETE http://localhost:8080/api/empleados/1
```

### Validaciones Implementadas

**Backend (Java):**
- RUT/DNI no puede estar duplicado
- Salario base mínimo: $400,000
- Bono no puede superar el 50% del salario base
- Descuentos no pueden ser mayores al salario base

**Frontend (JavaScript):**
- Todos los campos obligatorios
- Formato válido de RUT chileno
- Salario mínimo $400,000
- Mensajes de error dinámicos sin alertas
