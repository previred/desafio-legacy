var API_URL = '/api/empleados';

function formatMoney(value) {
    return '$' + Number(value || 0).toLocaleString('es-CL');
}

function mostrarErrores(errores) {
    var ul = document.getElementById('errores');
    ul.innerHTML = '';
    if (!errores || errores.length === 0) {
        ul.style.display = 'none';
        return;
    }
    errores.forEach(function(err) {
        var li = document.createElement('li');
        li.textContent = err;
        ul.appendChild(li);
    });
    ul.style.display = 'block';
}

function limpiarFormulario() {
    document.getElementById('formEmpleado').reset();
    document.getElementById('bonos').value = '0';
    document.getElementById('descuentos').value = '0';
    mostrarErrores([]);
}

function cargarEmpleados() {
    fetch(API_URL)
        .then(function(res) { return res.json(); })
        .then(function(empleados) {
            var tbody = document.getElementById('tablaEmpleados');
            if (empleados.length === 0) {
                tbody.innerHTML = '<tr><td colspan="9" class="empty-msg">No hay empleados registrados</td></tr>';
                return;
            }
            tbody.innerHTML = empleados.map(function(e) {
                return '<tr>' +
                    '<td>' + e.id + '</td>' +
                    '<td>' + e.nombre + '</td>' +
                    '<td>' + e.apellido + '</td>' +
                    '<td>' + e.rut + '</td>' +
                    '<td>' + e.cargo + '</td>' +
                    '<td class="salary">' + formatMoney(e.salario_base) + '</td>' +
                    '<td class="salary">' + formatMoney(e.bonos) + '</td>' +
                    '<td class="salary">' + formatMoney(e.descuentos) + '</td>' +
                    '<td><button class="btn btn-danger" onclick="eliminarEmpleado(' + e.id + ')">Eliminar</button></td>' +
                    '</tr>';
            }).join('');
        })
        .catch(function(err) {
            console.error('Error al cargar empleados:', err);
        });
}

function validarFrontend(data) {
    var errores = [];
    if (!data.nombre || !data.nombre.trim()) errores.push('El nombre es obligatorio');
    if (!data.apellido || !data.apellido.trim()) errores.push('El apellido es obligatorio');
    if (!data.rut || !data.rut.trim()) {
        errores.push('El RUT/DNI es obligatorio');
    } else if (!/^\d{7,8}-[\dkK]$/.test(data.rut.trim())) {
        errores.push('Formato de RUT/DNI inválido (ej: 12345678-5)');
    }
    if (!data.cargo || !data.cargo.trim()) errores.push('El cargo es obligatorio');
    if (!data.salario_base || data.salario_base < 400000) {
        errores.push('El salario base no puede ser menor a $400,000');
    }
    return errores;
}

function eliminarEmpleado(id) {
    fetch(API_URL + '/' + id, { method: 'DELETE' })
        .then(function(res) {
            if (!res.ok) {
                return res.json().then(function(err) { throw err; });
            }
            cargarEmpleados();
        })
        .catch(function(err) {
            mostrarErrores([err.mensaje || 'Error al eliminar empleado']);
        });
}

document.getElementById('formEmpleado').addEventListener('submit', function(e) {
    e.preventDefault();
    var data = {
        nombre: document.getElementById('nombre').value,
        apellido: document.getElementById('apellido').value,
        rut: document.getElementById('rut').value,
        cargo: document.getElementById('cargo').value,
        salario_base: parseFloat(document.getElementById('salarioBase').value) || 0,
        bonos: parseFloat(document.getElementById('bonos').value) || 0,
        descuentos: parseFloat(document.getElementById('descuentos').value) || 0
    };

    var erroresCliente = validarFrontend(data);
    if (erroresCliente.length > 0) {
        mostrarErrores(erroresCliente);
        return;
    }

    fetch(API_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    })
    .then(function(res) {
        if (!res.ok) {
            return res.json().then(function(err) { throw err; });
        }
        return res.json();
    })
    .then(function() {
        limpiarFormulario();
        cargarEmpleados();
    })
    .catch(function(err) {
        if (err.errores) {
            mostrarErrores(err.errores);
        } else {
            mostrarErrores([err.mensaje || 'Error al crear empleado']);
        }
    });
});

cargarEmpleados();
