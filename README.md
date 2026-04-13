# Desafío Legacy - Sistema de Gestión de Empleados

Este proyecto es una aplicación web Java para la gestión de empleados, usando servlets construida con una arquitectura de 3 capas (Controller, Service, Repository) y persistencia en base de datos H2.
Para el frontend se uso html + ajax (Fetch Api).

## Requisitos Previos

* **Java JDK 11** o superior.
* **Maven**
* Navegador web actualizado.

### 1. Instalación y Ejecución (Consola), en Windows

Si no deseas utilizar un IDE, puedes ejecutar la aplicación siguiendo estos pasos:

1. **Clonar o descargar el proyecto** en tu carpeta local.
2. **Abrir una terminal** en la raíz del proyecto.
3. **Limpiar y compilar el proyecto:**
   ```cmd
   mvn wrapper:wrapper
   mvn clean package
4. **Ejecutar la aplicación**
   ```cmd
   mvn spring-boot:run

### 2. Para probar la aplicación Web (Frontend)
Accede desde tu navegador a: [http://localhost:8080/desafio-legacy/](http://localhost:8080/desafio-legacy/)

Desde esta interfaz podrás realizar las siguientes operaciones:

* **Crear:** Registrar nuevos empleados en el sistema de manera intuitiva.
* **Listar:** Visualizar únicamente los empleados vigentes. Gracias a la **eliminación lógica**, los registros borrados no aparecerán en esta lista, permitiendo una interfaz limpia sin perder la integridad de los datos.
* **Eliminar:** Al presionar eliminar, el sistema no destruye el registro; en su lugar, realiza un `UPDATE` en la base de datos cambiando el campo `activo` a `false`.

### 2. Base de Datos H2 (Consola Web)
Para verificar que la **eliminación lógica** funciona correctamente (es decir, que los registros permanecen en la BD con `activo = false`), puedes inspeccionar el estado real de la tabla:

* **URL de acceso:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
* **JDBC URL:** `jdbc:h2:mem:desafiolegacydb`
* **Usuario:** `sa`
* **Password:** *12345*

**Consulta de verificación:**
Para visualizar todos los registros, incluidos aquellos marcados como "eliminados", ejecute la siguiente sentencia SQL:
```sql
SELECT * FROM empleados;
```

### 3. Pruebas de API (Postman / cURL)
La aplicación expone endpoints **RESTful** que pueden ser consumidos directamente para pruebas de integración:

* **Obtener empleados (Listar):** `GET /api/empleados`
* **Crear empleado:** `POST /api/empleados`
* **Eliminar empleado:** `DELETE /api/empleados`

**Ejemplo (JSON) para la creación:**
```json
{
  "nombre": "Juan",
  "apellido": "Pérez",
  "rut": "12345678-9",
  "cargo": "Desarrollador",
  "salario": 950000,
  "bono": 50000,
  "descuento": 10000
}




