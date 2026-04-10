// ÚNICA INICIALIZACIÓN DEL DOM
console.log('app.js actual cargado');

document.addEventListener('DOMContentLoaded', () => {
    console.log('DOMContentLoaded - iniciando app');

    cargarEmpleados();

    // Event listener del formulario
    document.getElementById('empleadoForm').addEventListener('submit', function (e) {
        e.preventDefault();
        guardarEmpleado();
    });

    // Configurar botones del modal
    document.getElementById('btnCancel').onclick = () => {
        document.getElementById('deleteModal').style.display = 'none';
        idParaEliminar = null;
    };

    document.getElementById('btnConfirmDelete').onclick = async () => {
        if (idParaEliminar) {
            await ejecutarEliminacion(idParaEliminar);
            document.getElementById('deleteModal').style.display = 'none';
        }
    };
});

/**
 * CARGAR: Obtiene la lista de empleados (GET)
 */
async function cargarEmpleados() {
    try {
        const response = await fetch('/api/empleados');
        const empleados = await response.json();
        console.log("Empleados cargados:", empleados);

        const tbody = document.querySelector('#tablaEmpleados tbody');
        tbody.innerHTML = '';

        empleados.forEach(emp => {
            const salario = Number(emp.salario || 0);
            const bono = Number(emp.bono || 0);
            const descuento = Number(emp.descuento || 0);
            const salarioLiquido = salario + bono - descuento;
            console.log(`Empleado: ${emp.nombre} ${emp.apellido}, Salario: ${salario}, Bono: ${bono}, Descuento: ${descuento}, Salario Líquido: ${salarioLiquido}`);

            const row = `<tr>
                <td>${emp.id}</td>
                <td>${emp.nombre} ${emp.apellido}</td>
                <td>${emp.rut}</td>
                <td>${emp.cargo}</td>
                <td>$${salario.toLocaleString('es-CL')}</td>
                <td>$${bono.toLocaleString('es-CL')}</td>
                <td>$${descuento.toLocaleString('es-CL')}</td>
                <td>$${salarioLiquido.toLocaleString('es-CL')}</td>
                <td><button class="btn-delete" onclick="eliminarEmpleado(${emp.id})">Eliminar</button></td>
            </tr>`;
            tbody.innerHTML += row;
        });
    } catch (error) {
        console.error(error);
        mostrarError(["Error al conectar con el servidor."]);
    }
}

/**
 * GUARDAR: Envía datos en formato JSON (POST)
 */
async function guardarEmpleado() {
    ocultarError();

    // Captura de datos
    const nombre = document.getElementById('nombre').value.trim();
    const apellido = document.getElementById('apellido').value.trim();
    const rut = document.getElementById('rut').value.trim();
    const cargo = document.getElementById('cargo').value.trim();
    const salarioRaw = document.getElementById('salario').value.trim();
    const bonoRaw = document.getElementById('bono').value.trim();
    const descuentoRaw = document.getElementById('descuento').value.trim();

    const salario = parseFloat(salarioRaw);
    const bono = bonoRaw ? parseFloat(bonoRaw) : 0;
    const descuento = descuentoRaw ? parseFloat(descuentoRaw) : 0;

    // --- VALIDACIONES FRONTEND ---
    let errores = [];

    // 1. Verificar campos OBLIGATORIOS solamente
    if (!nombre || !apellido || !rut || !cargo || !salarioRaw) {
        errores.push("Todos los campos obligatorios deben completarse (Nombre, Apellido, RUT, Cargo, Salario).");
    }

    // 2. Validar que salario sea un número válido
    if (salarioRaw && isNaN(salario)) {
        errores.push("El salario debe ser un número válido.");
    }

    // 3. Validar formato RUT (ej: 12345678-9 o 12.345.678-9)
    const rutRegex = /^[0-9]{7,8}-?[0-9kK]{1}$/;
    const rutLimpio = rut.replace(/\./g, '');
    if (rut && !rutRegex.test(rutLimpio)) {
        errores.push("El formato del RUT/DNI es inválido (ejemplo válido: 12345678-9 o 12.345.678-9).");
    }

    // 4. Validar salario base mínimo ($400,000)
    if (!isNaN(salario) && salario < 400000) {
        errores.push("El salario base no puede ser menor a $400,000.");
    }

    // Si hay errores en el frontend, se muestran y se detiene el envío
    if (errores.length > 0) {
        mostrarError(errores);
        return;
    }

    const empleado = { nombre, apellido, rut, cargo, salario, bono, descuento };

    try {
        const response = await fetch('/api/empleados', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(empleado)
        });

        const result = await response.json();

        if (response.ok) {
            document.getElementById('empleadoForm').reset();
            cargarEmpleados();
        } else {
            const mensajesError = result.errores || [result.error] || ["Error desconocido"];
            mostrarError(mensajesError);
        }
    } catch (error) {
        mostrarError(["No se pudo procesar la solicitud por un error de red."]);
    }
}

/**
 * ELIMINAR: Borra un registro por ID (DELETE)
 */
let idParaEliminar = null;

/**
 * Muestra el modal en lugar del confirm nativo
 */
function eliminarEmpleado(id) {
    idParaEliminar = id;
    document.getElementById('deleteModal').style.display = 'block';
}

/**
 * Lógica real de borrado (AJAX)
 */
async function ejecutarEliminacion(id) {
    try {
        const response = await fetch(`/api/empleados?id=${id}`, { method: 'DELETE' });
        if (response.ok) {
            cargarEmpleados();
        } else {
            mostrarError(["No se pudo eliminar el registro en el servidor."]);
        }
    } catch (error) {
        mostrarError(["Error de red al intentar eliminar."]);
    }
}

/**
 * Muestra errores de forma dinámica en la página
 * @param {Array} mensajes - Lista de strings con los errores
 */
function mostrarError(mensajes) {
    const box = document.getElementById('errorMessage');
    box.innerHTML = '';

    if (mensajes.length > 0) {
        const ul = document.createElement('ul');
        ul.style.margin = "0";
        ul.style.paddingLeft = "20px";

        mensajes.forEach(m => {
            const li = document.createElement('li');
            li.innerText = m;
            ul.appendChild(li);
        });

        box.appendChild(ul);
        box.style.display = 'block';
    }
}

function ocultarError() {
    const box = document.getElementById('errorMessage');
    box.style.display = 'none';
    box.innerHTML = '';
}