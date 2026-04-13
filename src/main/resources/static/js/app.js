const tablaEmpleados = document.getElementById('tabla-empleados');
const formEmpleado = document.getElementById('form-empleado');
const contenedorErrores = document.getElementById('errores-formulario');
const modalEmpleado = document.getElementById('modalEmpleado');
const modalEliminar = document.getElementById('modalEliminar');
const btnConfirmarEliminar = document.getElementById('btn-confirmar-eliminar');

let empleadoIdAEliminar = null;

async function cargarEmpleados() {
    try {
        const respuesta = await fetch('/api/empleados');

        if (!respuesta.ok) {
            throw new Error('No se pudo cargar la lista de empleados.');
        }

        const empleados = await respuesta.json();
        pintarTablaEmpleados(empleados);

    } catch (error) {
        console.error('Error al cargar empleados:', error);
        mostrarMensajeTabla('Error al cargar los empleados.', 'danger');
    }
}

function pintarTablaEmpleados(empleados) {
    const hayEmpleados = Array.isArray(empleados) && empleados.length > 0;

    if (!hayEmpleados) {
        mostrarMensajeTabla('No hay empleados registrados.', 'secondary');
        return;
    }

    let filas = '';

    empleados.forEach(empleado => {
        filas += crearFilaEmpleado(empleado);
    });

    tablaEmpleados.innerHTML = filas;
}

function crearFilaEmpleado(empleado) {
    const salario = formatearSalario(empleado.salario);

    return `
        <tr>
            <td>${empleado.id ?? ''}</td>
            <td>${empleado.nombre ?? ''}</td>
            <td>${empleado.apellido ?? ''}</td>
            <td>${empleado.rutDni ?? ''}</td>
            <td>${empleado.cargo ?? ''}</td>
            <td class="text-end">$${salario}</td>
            <td class="text-center">
                <button type="button"
                        class="btn btn-sm btn-outline-danger"
                        title="Eliminar"
                        data-bs-toggle="modal"
                        data-bs-target="#modalEliminar"
                        data-id="${empleado.id}">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        </tr>
    `;
}

function formatearSalario(valor) {
    if (valor === null || valor === undefined) {
        return '0';
    }

    return Number(valor).toLocaleString('es-CL');
}

function mostrarMensajeTabla(mensaje, tipo) {
    tablaEmpleados.innerHTML = `
        <tr>
            <td colspan="7" class="text-center text-${tipo} py-4">
                ${mensaje}
            </td>
        </tr>
    `;
}

function obtenerDatosFormulario() {
    return {
        nombre: document.getElementById('nombre').value.trim(),
        apellido: document.getElementById('apellido').value.trim(),
        rutDni: document.getElementById('rut').value.trim(),
        cargo: document.getElementById('cargo').value.trim(),
        salario: document.getElementById('salario').value
            ? Number(document.getElementById('salario').value)
            : null,
        bono: 0,
        descuentos: 0
    };
}

function mostrarErroresFormulario(errores) {
    contenedorErrores.classList.remove('d-none');
    contenedorErrores.innerHTML = `
        <ul class="mb-0">
            ${errores.map(error => `<li>${error}</li>`).join('')}
        </ul>
    `;
}

function limpiarErroresFormulario() {
    contenedorErrores.classList.add('d-none');
    contenedorErrores.innerHTML = '';
}

async function guardarEmpleado(event) {
    event.preventDefault();
    limpiarErroresFormulario();

    const empleado = obtenerDatosFormulario();
    const erroresFrontend = validarFormularioEmpleado(empleado);

    if (erroresFrontend.length > 0) {
        mostrarErroresFormulario(erroresFrontend);
        return;
    }

    try {
        const respuesta = await fetch('/api/empleados', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(empleado)
        });

        if (respuesta.status === 400) {
            const data = await respuesta.json();
            const errores = Array.isArray(data.errores)
                ? data.errores.map(error => error.mensaje)
                : ['No se pudo guardar el empleado.'];

            mostrarErroresFormulario(errores);
            return;
        }

        if (!respuesta.ok) {
            throw new Error('Error al guardar empleado.');
        }

        formEmpleado.reset();
        limpiarErroresFormulario();

        const instanciaModal = bootstrap.Modal.getInstance(modalEmpleado);
        if (instanciaModal) {
            instanciaModal.hide();
        }

        await cargarEmpleados();

    } catch (error) {
        console.error('Error al guardar empleado:', error);
        mostrarErroresFormulario(['Ocurrió un error inesperado al guardar el empleado.']);
    }
}

async function eliminarEmpleado() {
    if (empleadoIdAEliminar === null) {
        return;
    }

    try {
        const respuesta = await fetch(`/api/empleados?id=${empleadoIdAEliminar}`, {
            method: 'DELETE'
        });

        if (!respuesta.ok) {
            throw new Error('Error al eliminar empleado.');
        }

        btnConfirmarEliminar.blur();

        const instanciaModal = bootstrap.Modal.getInstance(modalEliminar);
        if (instanciaModal) {
            instanciaModal.hide();
        }

        empleadoIdAEliminar = null;
        await cargarEmpleados();

    } catch (error) {
        console.error('Error al eliminar empleado:', error);
    }
}

function manejarClickEliminar(event) {
    const botonEliminar = event.target.closest('[data-id]');

    if (!botonEliminar) {
        return;
    }

    empleadoIdAEliminar = botonEliminar.dataset.id;
}

document.addEventListener('DOMContentLoaded', () => {
    cargarEmpleados();
    formEmpleado.addEventListener('submit', guardarEmpleado);
    btnConfirmarEliminar.addEventListener('click', eliminarEmpleado);
    document.addEventListener('click', manejarClickEliminar);
});