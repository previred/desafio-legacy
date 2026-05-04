const apiUrl = '/api/empleados';
const form = document.getElementById('empleadoForm');
const tablaBody = document.getElementById('tablaEmpleadosBody');
const mensaje = document.getElementById('mensaje');
const errores = document.getElementById('errores');
const recargarBtn = document.getElementById('recargarBtn');

const rutPattern = /^[0-9]{1,2}\.?[0-9]{3}\.?[0-9]{3}-[0-9kK]{1}$/;

function limpiarMensaje() { mensaje.textContent = ''; mensaje.className = 'message hidden'; }
function mostrarMensaje(texto, tipo) { mensaje.textContent = texto; mensaje.className = 'message ' + tipo; }
function limpiarErrores() { errores.innerHTML = ''; }
function mostrarErrores(lista) {
    errores.innerHTML = '';
    lista.forEach(function(item) {
        const li = document.createElement('li');
        li.textContent = item;
        errores.appendChild(li);
    });
}
function obtenerPayload() {
    return {
        nombre: document.getElementById('nombre').value.trim(),
        apellido: document.getElementById('apellido').value.trim(),
        rutDni: document.getElementById('rutDni').value.trim(),
        cargo: document.getElementById('cargo').value.trim(),
        salario: document.getElementById('salario').value
    };
}
function validarFormulario(payload) {
    const lista = [];
    if (!payload.nombre) lista.push('El nombre es obligatorio.');
    if (!payload.apellido) lista.push('El apellido es obligatorio.');
    if (!payload.rutDni) lista.push('El RUT/DNI es obligatorio.');
    else if (!(rutPattern.test(payload.rutDni))) lista.push('El RUT/DNI debe tener formato válido.');
    if (!payload.cargo) lista.push('El cargo es obligatorio.');
    if (!payload.salario) lista.push('El salario es obligatorio.');
    else if (Number(payload.salario) < 400000) lista.push('El salario no puede ser menor a $400,000.');
    return lista;
}
function formatearSalario(valor) {
    return new Intl.NumberFormat('es-CL', { style: 'currency', currency: 'CLP', minimumFractionDigits: 0}).format(Number(valor));
}
function renderizarTabla(empleados) {
    if (!empleados || empleados.length === 0) {
        tablaBody.innerHTML = '<tr><td colspan="7">No hay empleados registrados.</td></tr>';
        return;
    }
    tablaBody.innerHTML = empleados.map(function(emp) {
        return '<tr>' +
            '<td>' + emp.id + '</td>' +
            '<td>' + emp.nombre + '</td>' +
            '<td>' + emp.apellido + '</td>' +
            '<td>' + emp.rutDni + '</td>' +
            '<td>' + emp.cargo + '</td>' +
            '<td>' + formatearSalario(emp.salario) + '</td>' +
            '<td><button class="btn-sm" onclick="eliminarEmpleado(' + emp.id + ')">Eliminar</button></td>' +
            '</tr>';
    }).join('');
}
function cargarEmpleados() {
    tablaBody.innerHTML = '<tr><td colspan="7">Cargando empleados...</td></tr>';
    fetch(apiUrl)
        .then(function(response) { return response.json().then(function(data) { return { ok: response.ok, data: data }; }); })
        .then(function(result) {
            if (!result.ok) throw new Error(result.data.message || 'No se pudo cargar la lista.');
            renderizarTabla(result.data.data);
        })
        .catch(function(error) {
            tablaBody.innerHTML = '<tr><td colspan="7">Error al cargar empleados.</td></tr>';
            mostrarMensaje(error.message, 'error');
        });
}
function eliminarEmpleado(id) {
    limpiarMensaje();
    limpiarErrores();
    fetch(apiUrl + '/' + encodeURIComponent(id), { method: 'DELETE' })
        .then(function(response) { return response.json().then(function(data) { return { ok: response.ok, data: data }; }); })
        .then(function(result) {
            if (!result.ok) throw new Error(result.data.message || 'No se pudo eliminar.');
            mostrarMensaje(result.data.message, 'success');
            cargarEmpleados();
        })
        .catch(function(error) { mostrarMensaje(error.message, 'error'); });
}
form.addEventListener('submit', function(event) {
    event.preventDefault();
    limpiarMensaje();
    limpiarErrores();
    const payload = obtenerPayload();
    const erroresForm = validarFormulario(payload);
    if (erroresForm.length > 0) {
        mostrarErrores(erroresForm);
        mostrarMensaje('Corrige los errores del formulario.', 'error');
        return;
    }
    fetch(apiUrl, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    })
        .then(function(response) { return response.json().then(function(data) { return { ok: response.ok, data: data }; }); })
        .then(function(result) {
            if (!result.ok) {
                if (Array.isArray(result.data.data)) mostrarErrores(result.data.data);
                throw new Error(result.data.message || 'No fue posible guardar.');
            }
            form.reset();
            mostrarMensaje(result.data.message, 'success');
            cargarEmpleados();
        })
        .catch(function(error) {
            if (!errores.innerHTML) mostrarErrores([error.message]);
            mostrarMensaje('No fue posible guardar el empleado.', 'error');
        });
});
recargarBtn.addEventListener('click', function() { limpiarMensaje(); limpiarErrores(); cargarEmpleados(); });
window.eliminarEmpleado = eliminarEmpleado;
window.addEventListener('DOMContentLoaded', cargarEmpleados);
