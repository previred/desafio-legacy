const API_URL = '/api/empleados';

const empleadoForm = document.getElementById('empleadoForm');
const empleadosBody = document.getElementById('empleadosBody');
const validationErrors = document.getElementById('validationErrors');
const globalMessage = document.getElementById('globalMessage');
const btnRecargar = document.getElementById('btnRecargar');

document.addEventListener('DOMContentLoaded', function () {
    cargarEmpleados();
});

btnRecargar.addEventListener('click', function () {
    cargarEmpleados();
});

empleadoForm.addEventListener('submit', async function (event) {
    event.preventDefault();

    limpiarMensajes();

    const empleado = obtenerDatosFormulario();
    const erroresFrontend = validarFormulario(empleado);

    if (erroresFrontend.length > 0) {
        mostrarErrores(erroresFrontend);
        return;
    }

    try {
        const response = await fetch(API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(empleado)
        });

        const data = await response.json();

        if (!response.ok) {
            if (data.errors) {
                mostrarErrores(data.errors);
            } else {
                mostrarMensaje(data.message || 'Error al guardar empleado', 'error');
            }
            return;
        }

        mostrarMensaje(data.message || 'Empleado creado correctamente', 'success');
        empleadoForm.reset();
        document.getElementById('bono').value = 0;
        document.getElementById('descuentos').value = 0;
        cargarEmpleados();

    } catch (error) {
        mostrarMensaje('Error de conexión al guardar empleado', 'error');
        console.error(error);
    }
});

async function cargarEmpleados() {
    limpiarMensajes();
    empleadosBody.innerHTML = '<tr><td colspan="9">Cargando empleados...</td></tr>';

    try {
        const response = await fetch(API_URL);
        const empleados = await response.json();

        if (!response.ok) {
            empleadosBody.innerHTML = '<tr><td colspan="9">Error al cargar empleados</td></tr>';
            return;
        }

        if (!empleados || empleados.length === 0) {
            empleadosBody.innerHTML = '<tr><td colspan="9">No hay empleados registrados</td></tr>';
            return;
        }

        empleadosBody.innerHTML = '';

        empleados.forEach(function (empleado) {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${empleado.id ?? ''}</td>
                <td>${empleado.nombre ?? ''}</td>
                <td>${empleado.apellido ?? ''}</td>
                <td>${empleado.rutDni ?? ''}</td>
                <td>${empleado.cargo ?? ''}</td>
                <td>${formatearNumero(empleado.salarioBase)}</td>
                <td>${formatearNumero(empleado.bono)}</td>
                <td>${formatearNumero(empleado.descuentos)}</td>
                <td>
                    <button class="delete-btn" onclick="eliminarEmpleado(${empleado.id})">
                        Eliminar
                    </button>
                </td>
            `;
            empleadosBody.appendChild(row);
        });

    } catch (error) {
        empleadosBody.innerHTML = '<tr><td colspan="9">Error de conexión al cargar empleados</td></tr>';
        console.error(error);
    }
}

async function eliminarEmpleado(id) {
    limpiarMensajes();

    if (!confirm('¿Deseas eliminar este empleado?')) {
        return;
    }

    try {
        const response = await fetch(`${API_URL}/${id}`, {
             method: 'DELETE'
        });

        const data = await response.json();

        if (!response.ok) {
            mostrarMensaje(data.message || 'No se pudo eliminar el empleado', 'error');
            return;
        }

        mostrarMensaje(data.message || 'Empleado eliminado correctamente', 'success');
        cargarEmpleados();

    } catch (error) {
        mostrarMensaje('Error de conexión al eliminar empleado', 'error');
        console.error(error);
    }
}

function obtenerDatosFormulario() {
    return {
        nombre: document.getElementById('nombre').value.trim(),
        apellido: document.getElementById('apellido').value.trim(),
        rutDni: document.getElementById('rutDni').value.trim(),
        cargo: document.getElementById('cargo').value.trim(),
        salarioBase: convertirNumero(document.getElementById('salarioBase').value),
        bono: convertirNumero(document.getElementById('bono').value),
        descuentos: convertirNumero(document.getElementById('descuentos').value)
    };
}

function validarFormulario(empleado) {
    const errores = [];

    if (!empleado.nombre) {
        errores.push({ field: 'nombre', message: 'El nombre es obligatorio' });
    }

    if (!empleado.apellido) {
        errores.push({ field: 'apellido', message: 'El apellido es obligatorio' });
    }

    if (!empleado.rutDni) {
        errores.push({ field: 'rutDni', message: 'El RUT/DNI es obligatorio' });
    } else if (!esRutDniValido(empleado.rutDni)) {
        errores.push({ field: 'rutDni', message: 'El formato del RUT/DNI no es válido' });
    }

    if (!empleado.cargo) {
        errores.push({ field: 'cargo', message: 'El cargo es obligatorio' });
    }

    if (empleado.salarioBase === null || isNaN(empleado.salarioBase)) {
        errores.push({ field: 'salarioBase', message: 'El salario base es obligatorio' });
    } else if (empleado.salarioBase < 400000) {
        errores.push({ field: 'salarioBase', message: 'El salario base no puede ser menor a 400000' });
    }

    if (empleado.bono !== null && empleado.salarioBase !== null) {
        if (empleado.bono > empleado.salarioBase * 0.5) {
            errores.push({ field: 'bono', message: 'El bono no puede superar el 50% del salario base' });
        }
    }

    if (empleado.descuentos !== null && empleado.salarioBase !== null) {
        if (empleado.descuentos > empleado.salarioBase) {
            errores.push({ field: 'descuentos', message: 'Los descuentos no pueden superar el salario base' });
        }
    }

    return errores;
}

function esRutDniValido(valor) {
    const regexRut = /^[0-9]{7,8}-[0-9kK]$/;
    const regexDni = /^[0-9]{7,12}$/;
    return regexRut.test(valor) || regexDni.test(valor);
}

function convertirNumero(valor) {
    if (valor === undefined || valor === null || valor === '') {
        return 0;
    }
    return Number(valor);
}

function mostrarErrores(errores) {
    validationErrors.innerHTML = '';

    if (!errores || errores.length === 0) {
        return;
    }

    const div = document.createElement('div');
    div.className = 'error-box';

    let html = '<strong>Se encontraron errores:</strong><ul>';
    errores.forEach(function (error) {
        html += `<li><strong>${error.field}</strong>: ${error.message}</li>`;
    });
    html += '</ul>';

    div.innerHTML = html;
    validationErrors.appendChild(div);
}

function mostrarMensaje(mensaje, tipo) {
    globalMessage.innerHTML = '';

    const div = document.createElement('div');

    if (tipo === 'success') {
        div.className = 'success-box';
    } else if (tipo === 'info') {
        div.className = 'info-box';
    } else {
        div.className = 'error-box';
    }

    div.textContent = mensaje;
    globalMessage.appendChild(div);
}

function limpiarMensajes() {
    validationErrors.innerHTML = '';
    globalMessage.innerHTML = '';
}

function formatearNumero(valor) {
    if (valor === null || valor === undefined) {
        return '0';
    }
    return Number(valor).toLocaleString('es-CL');
}