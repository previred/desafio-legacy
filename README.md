# Sistema de Gestión de Empleados - Desafío Técnico Previred

Este proyecto es una aplicación web para la gestión de empleados, desarrollada como parte de un desafío técnico. La solución integra una arquitectura limpia, validaciones de negocio robustas y una interfaz dinámica sin el uso de frameworks de persistencia pesados, cumpliendo con los requisitos de desarrollo "Legacy" y modernización con AJAX.

## 🚀 Tecnologías Utilizadas

* **Backend:** Java 8+, Spring Boot 2.7.x (como contenedor), Servlets nativos (`javax.servlet`).
* **Persistencia:** JDBC Nativo con base de datos H2 (en memoria).
* **Frontend:** HTML5, CSS3 y JavaScript Vanilla (Fetch API).
* **Documentación:** OpenAPI 3.0 (Swagger).
* **Gestión de Dependencias:** Maven.

## 🛠️ Requisitos Previos

* Java JDK 8 o superior instalado.
* Maven 3.6+ instalado.
* Navegador web moderno (Chrome, Firefox, Edge).

## 📥 Instalación y Ejecución

1.  **Clonar el repositorio:**
    ```bash
    git clone [https://github.com/tu-usuario/desafio-previred.git](https://github.com/tu-usuario/desafio-previred.git)
    cd desafio-previred
    ```

2.  **Compilar el proyecto:**
    ```bash
    mvn clean install
    ```

3.  **Ejecutar la aplicación:**
    ```bash
    mvn spring-boot:run
    ```

4.  **Acceder a la aplicación:**
    Abre tu navegador en: [http://localhost:8080](http://localhost:8080)

## 📝 Reglas de Negocio Implementadas

La aplicación valida automáticamente las siguientes reglas antes de permitir el registro de un empleado:

1.  **Salario Mínimo:** No se permiten registros con un salario base inferior a **$400.000**.
2.  **Tope de Bonos:** El monto por concepto de bonos no puede superar el **50% del salario base**.
3.  **Tope de Descuentos:** El total de descuentos no puede ser superior al **salario base**.
4.  **RUT Único:** No se permite el registro de dos empleados con el mismo RUT.
5.  **Campos Obligatorios:** Validación de integridad para asegurar que los datos básicos estén presentes.

## 🔍 Cómo Probar la Aplicación

### Interfaz Web (Frontend)
1.  Ingresa a `http://localhost:8080`.
2.  Completa el formulario. El sistema calculará automáticamente el **Sueldo Líquido** en la tabla inferior.
3.  Intenta ingresar un salario de `$300.000` para observar la captura de errores dinámicos (HTTP 400).
4.  Utiliza el botón **Eliminar** para probar la persistencia en tiempo real.

### API y Documentación (Swagger)
* Puedes consultar la definición de la API en el archivo `swagger.yaml` incluido en la raíz.
* Para probar los endpoints manualmente, puedes usar herramientas como Postman o el navegador:
    * **GET** `http://localhost:8080/api/empleados` (Listar todos).
    * **POST** `http://localhost:8080/api/empleados` (Registrar).
    * **DELETE** `http://localhost:8080/api/empleados?id=1` (Eliminar).

### Consola de Base de Datos
Para visualizar las tablas en tiempo real:
* URL: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
* JDBC URL: `jdbc:h2:mem:previred_db`
* User: `sa` | Password: (en blanco)

---
Desarrollado por **Franco Carrasco** - Abril 2026