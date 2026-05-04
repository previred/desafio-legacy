# EmpleadoServlet API

Este componente  basado en Servlets actúa como punto de entrada para la gestión de empleados.

## Requisitos Previos

*   **Java JDK 8:** Es obligatorio tener configurada la variable de entorno `JAVA_HOME` apuntando a una versión de JDK 8.
*   **Maven Wrapper:** El proyecto incluye `./mvnw` para gestionar la construcción sin necesidad de instalar Maven globalmente.

## Ejecución de la Aplicación

Para compilar y levantar el servidor, ejecuta los siguientes comandos en tu terminal (PowerShell o CMD):

1. **Compilar el proyecto:**
   ```powershell
   .\mvnw clean package
   ```

2. **Acceder al directorio de salida:**
   ```powershell
   cd .\target\
   ```

3. **Ejecutar el archivo JAR:**
   ```powershell
   java -jar desafio-legacy-1.0-SNAPSHOT.jar
   ```

---

## Accesos Rápidos (UI & Herramientas)

*   **Interfaz de Usuario (UI):** [http://localhost:8080/index.html](http://localhost:8080/index.html)
*   **Consola de Base de Datos (H2):** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

### Configuración de Conexión H2
Para acceder a la base de datos en memoria desde la consola, utiliza los siguientes parámetros:
- **JDBC URL:** `jdbc:h2:mem:empleados_db;DB_CLOSE_DELAY=-1`
- **User Name:** (generalmente `sa`)
- **Password:** (en blanco por defecto)

---

## Endpoints y Ejemplos de Uso (PowerShell)

La base de la URL para todos los endpoints es: `http://localhost:8080/api/empleados`

### 1. Listar Empleados
Obtiene una lista de todos los empleados registrados.

*   **Método:** `GET`
```powershell
Invoke-RestMethod -Method GET `
  -Uri "http://localhost:8080/api/empleados"
```

---

### 2. Crear un Empleado
Registra un nuevo empleado enviando un objeto JSON en el cuerpo de la petición.

*   **Método:** `POST`
*   **Comando:**
```powershell
Invoke-RestMethod -Method POST `
  -Uri "http://localhost:8080/api/empleados" `
  -ContentType "application/json" `
  -Body '{"name":"Juan","surname":"Perez","fiscalId":"123232345223233323638-9","role":"DEVELOPER","salary":1000000,"bonus":50000,"discounts":2222}'
```

---

### 3. Eliminar un Empleado
Elimina a un empleado específico mediante su ID en la URL.

*   **Método:** `DELETE`
*   **Comando (Ejemplo para ID 1):**
```powershell
Invoke-RestMethod -Method DELETE `
  -Uri "http://localhost:8080/api/empleados/1"
```

## Códigos de Respuesta HTTP
- **201 Created**: Creación exitosa del empleado.
- **200 OK**: Consulta o eliminación procesada correctamente.
- **400 Bad Request**: Datos inválidos, ID no proporcionado o error de formato.
- **500 Internal Server Error**: Error inesperado en el procesamiento interno.