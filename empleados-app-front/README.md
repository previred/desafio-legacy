# 📌 Empleados App Frontend

## 🧾 Descripción General

Aplicación web desarrollada como parte de una prueba técnica, cuyo
objetivo es gestionar empleados mediante una interfaz dinámica que
consume un servicio REST.

El sistema permite realizar operaciones CRUD básicas (listar, crear y
eliminar empleados) sin recargar la página, utilizando JavaScript puro y
AJAX mediante Fetch API.

------------------------------------------------------------------------

## 🚀 Tecnologías Utilizadas

-   HTML5
-   CSS3
-   JavaScript (ES6+)
-   Fetch API (AJAX)
-   Backend REST (`/api/empleados`)

------------------------------------------------------------------------

## ⚙️ Funcionalidades

La aplicación implementa las siguientes funcionalidades:

### 📋 Listar empleados

-   Obtiene los empleados desde el backend
-   Renderiza dinámicamente en una tabla HTML
-   No requiere recarga de página

### ➕ Agregar empleado

-   Formulario dinámico
-   Envío mediante Fetch API (POST)
-   Actualización automática de la tabla

### 🗑️ Eliminar empleado

-   Botón por cada fila
-   Petición DELETE al backend
-   Actualización inmediata de la vista

------------------------------------------------------------------------

## ⚠️ Validaciones en Frontend

Se implementaron validaciones antes de enviar los datos:

-   Todos los campos obligatorios deben estar completos
-   Validación de formato de DNI mediante expresión regular
-   El salario base no puede ser menor a **\$400,000**
-   Mensajes de error dinámicos en la interfaz
-   No se utilizan alertas (mejor UX)

------------------------------------------------------------------------

## 🧠 Decisiones Técnicas

### 📌 Inclusión de nuevos campos 

Se agregaron los siguientes campos para el formulario de formar no obligatoria :

-   `bono`
-   `descuentos`

#### ✔ Justificación:

Estos campos permiten enriquecer la lógica de negocio y alinearse con
las reglas del backend, facilitando el cálculo del valor neto del
empleado y realizar alguna validaciones solicitudes en la pruebas .

------------------------------------------------------------------------

### 📊 Ajustes en la tabla

Se añadieron nuevas columnas:

-   Bono
-   Descuentos
-   Valor Neto

Esto mejora la visualización de la información y permite un análisis más
completo del salario.

------------------------------------------------------------------------

## 💡 Cálculo del Valor Neto

Se implementó la siguiente fórmula:

**Valor Neto = Salario Base + Bono - Descuentos**

### ✔ Importancia:

-   Permite visualizar el ingreso real del empleado
-   Facilita la toma de decisiones
-   Mejora la experiencia del usuario al mostrar información consolidada

------------------------------------------------------------------------

## ▶️ Instrucciones para Ejecutar

### 1. Backend

Asegúrate de que el backend esté corriendo en:

    http://localhost:8080

------------------------------------------------------------------------

### 2. Frontend

Abrir el proyecto usando un servidor local:

**Opción recomendada:** - Usar extensión Live Server en VS Code

    http://127.0.0.1:5500

------------------------------------------------------------------------

### 3. Verificar funcionamiento

-   Listar empleados
-   Crear nuevo empleado
-   Eliminar empleado

------------------------------------------------------------------------

## 📌 Ejemplo de Uso

### Crear empleado

``` json
{
  "nombre": "Juan",
  "apellido": "Perez",
  "dni": "12345678",
  "cargo": "Developer",
  "salario": 500000,
  "bono": 100000,
  "descuentos": 50000
}
```

### Resultado esperado

``` text
{
    "message": "Empleado creado correctamente"
}
```

### listar empleado

GET http://localhost:8080/api/empleados 

### Resultado esperado

``` json
[
    {
        "id": 1,
        "nombre": "Darwin",
        "apellido": "Bolocho",
        "dni": "43534534534",
        "cargo": "Futbolist",
        "salarioBase": 400000.0,
        "bono": 200000.0,
        "descuentos": 34.0,
        "salarioNeto": 599966.0
    },
    {
        "id": 2,
        "nombre": "Darwin",
        "apellido": "Bolocho",
        "dni": "43534534533",
        "cargo": "Futbolist",
        "salarioBase": 400000.0,
        "bono": 200000.0,
        "descuentos": 0.0,
        "salarioNeto": 600000.0
    }
]
```

### Delete empleado

DELETE http://localhost:8080/api/empleados?id=1

### Resultado esperado

``` json
{
    "message": "Empleado eliminado"
}

```
------------------------------------------------------------------------

## 🎯 Conclusión

Este proyecto demuestra:

-   Uso correcto de JavaScript sin frameworks
-   Consumo de APIs REST con Fetch
-   Manejo de estado en el frontend
-   Buenas prácticas de validación y UX
-   Código modular y mantenible

------------------------------------------------------------------------

## 👨‍💻 Autor

Desarrollado como prueba técnica siguiendo buenas prácticas de
desarrollo full stack. Cristian Palacios
