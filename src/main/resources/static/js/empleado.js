let idEmpleadoAEliminar = null;
let modalEliminar = null;

document.addEventListener('DOMContentLoaded', () => {
    const modalElement = document.getElementById('confirmarEliminarModal');
    if (modalElement) modalEliminar = new bootstrap.Modal(modalElement);

    renderizarTabla();

    document.getElementById('btnConfirmarEliminar')?.addEventListener('click', ejecutarEliminacion);
    configurarFormulario();
});

function configurarFormulario() {
    const formulario = document.getElementById('registroForm');
    const inputRut = document.getElementById('rut_dni');

    if (!formulario) return;

    inputRut.addEventListener('input', (e) => {
        let valorLimpio = e.target.value.replace(/[^0-9kK]/g, '').slice(0, 9);
        e.target.value = Utils.formatearRut(valorLimpio);
    });

    formulario.addEventListener('submit', async (e) => {
        e.preventDefault();
        limpiarErrores();

        const formData = new FormData(e.target);
        const datos = {
            nombre: formData.get('nombre'),
            apellido: formData.get('apellido'),
            rut_dni: formData.get('rut_dni').replace(/\./g, ''),
            cargo: formData.get('cargo'),
            salario: parseFloat(formData.get('salario')),
            bono: parseFloat(formData.get('bono')) || 0,
            descuento: parseFloat(formData.get('descuento')) || 0
        };

        const response = await EmpleadoService.guardar(datos);
        const resData = await response.json();

        if (response.ok) {
            e.target.reset();
            renderizarTabla();
            mostrarNotificacion("¡Empleado guardado con éxito!", "success");
        } else {
            mostrarErroresBackend(resData);
        }
    });
}

async function renderizarTabla() {
    try {
        const empleados = await EmpleadoService.obtenerTodos();
        const tbody = document.getElementById('tablaEmpleadosBody');

        tbody.innerHTML = empleados.map(emp => `
            <tr>
                <td class="text-center">${emp.id}</td>
                <td class="text-center">${emp.nombre}</td>
                <td class="text-center">${emp.apellido}</td>
                <td class="text-center">${Utils.formatearRut(emp.rut_dni)}</td>
                <td class="text-center">${emp.cargo}</td>
                <td class="text-center">${Utils.formatoMoneda(emp.salario)}</td>
                <td class="text-center text-success">${Utils.formatoMoneda(emp.bono)}</td>
                <td class="text-center text-danger">${Utils.formatoMoneda(emp.descuento)}</td>
                <td class="text-center">
                    <button class="btn btn-outline-danger btn-sm" onclick="prepararEliminacion('${emp.id}')">
                        Eliminar
                    </button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error(error);
    }
}

// Funciones de acción
function prepararEliminacion(id) {
    idEmpleadoAEliminar = id;
    modalEliminar ? modalEliminar.show() : (confirm("¿Seguro?") && ejecutarEliminacion());
}

async function ejecutarEliminacion() {
    if (await EmpleadoService.eliminar(idEmpleadoAEliminar)) {
        modalEliminar?.hide();
        renderizarTabla();
        mostrarNotificacion("Empleado eliminado", "danger");
    }
    idEmpleadoAEliminar = null;
}

function mostrarErroresBackend(errores) {
    Object.keys(errores).forEach(campo => {
        const input = document.getElementById(campo);
        const feedback = document.getElementById(`error-${campo}`);
        if (input) input.classList.add('is-invalid');
        if (feedback) feedback.innerText = errores[campo];
    });
}

function limpiarErrores() {
    document.querySelectorAll('.form-control').forEach(i => i.classList.remove('is-invalid'));
}

function mostrarNotificacion(mensaje, tipo) {
    const alerta = document.getElementById('alertaGlobal');
    document.getElementById('textoMensaje').innerText = mensaje;
    alerta.className = `alert alert-${tipo} sticky-top`;
    alerta.classList.remove('d-none');
    window.scrollTo({ top: 0, behavior: 'smooth' });
    setTimeout(() => alerta.classList.add('d-none'), 4000);
}