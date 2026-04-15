const API_URL = '/api/empleados';

function cargarEmpleados() {
    fetch(API_URL)
        .then(response => response.json())
        .then(empleados => {
            const tbody = document.getElementById('tablaEmpleados');
            tbody.innerHTML = '';

            if (empleados.length === 0) {
                tbody.innerHTML = '<tr><td colspan="9" class="sin-empleados">No hay empleados registrados</td></tr>';
                return;
            }

            empleados.forEach(empleado => {
                const fila = document.createElement('tr');
                fila.innerHTML = `
                    <td>${empleado.id}</td>
                    <td>${empleado.nombre}</td>
                    <td>${empleado.apellido}</td>
                    <td>${empleado.rut}</td>
                    <td>${empleado.cargo}</td>
                    <td>$${empleado.salarioBase.toLocaleString('es-CL')}</td>
                    <td>$${empleado.bono.toLocaleString('es-CL')}</td>
                    <td>$${empleado.descuentos.toLocaleString('es-CL')}</td>
                    <td><button class="btn-eliminar" onclick="eliminarEmpleado(${empleado.id})">Eliminar</button></td>
                `;
                tbody.appendChild(fila);
            });
        })
        .catch(error => {
            console.error('Error al cargar empleados:', error);
        });
}

function agregarEmpleado(empleado) {
    fetch(API_URL, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(empleado)
    })
    .then(response => {
        if (response.ok) {
            limpiarErrores();
            limpiarFormulario();
            cargarEmpleados();
        } else {
            return response.json().then(error => {
                mostrarErrores([error.error]);
            });
        }
    })
    .catch(error => {
        console.error('Error al agregar empleado:', error);
    });
}

function eliminarEmpleado(id) {
    fetch(`${API_URL}/${id}`, {
        method: 'DELETE'
    })
    .then(response => {
        if (response.ok) {
            cargarEmpleados();
        } else {
            mostrarError('No se pudo eliminar el empleado');
        }
    })
    .catch(error => {
        console.error('Error al eliminar empleado:', error);
    });
}

function mostrarErrores(errores) {
    const divErrores = document.getElementById('errores');
    divErrores.innerHTML = errores.map(e => `<p>⚠️ ${e}</p>`).join('');
    divErrores.style.display = 'block';
}

function limpiarErrores() {
    const divErrores = document.getElementById('errores');
    divErrores.innerHTML = '';
    divErrores.style.display = 'none';
}

function limpiarFormulario() {
    document.getElementById('formularioEmpleado').reset();
}

function validarCamposCompletos(empleado) {
    const errores = [];
    if (!empleado.nombre.trim())    errores.push('El nombre es obligatorio');
    if (!empleado.apellido.trim())  errores.push('El apellido es obligatorio');
    if (!empleado.rut.trim())       errores.push('El RUT es obligatorio');
    if (!empleado.cargo.trim())     errores.push('El cargo es obligatorio');
    if (!empleado.salarioBase)      errores.push('El salario base es obligatorio');
    return errores;
}

function validarReglas(empleado) {
    const errores = [];
    const rutRegex = /^\d{1,2}\.?\d{3}\.?\d{3}-[\dkK]$/;
    if (!rutRegex.test(empleado.rut)) {
        errores.push('El formato del RUT no es válido. Ej: 12345678-9');
    }
    if (empleado.salarioBase < 400000) {
        errores.push('El salario base no puede ser menor a $400.000');
    }
    return errores;
}

document.getElementById('formularioEmpleado')
    .addEventListener('submit', function(evento) {
        evento.preventDefault();
        limpiarErrores();

        const empleado = {
            nombre:      document.getElementById('nombre').value,
            apellido:    document.getElementById('apellido').value,
            rut:         document.getElementById('rut').value,
            cargo:       document.getElementById('cargo').value,
            salarioBase: parseFloat(document.getElementById('salarioBase').value),
            bono:        parseFloat(document.getElementById('bono').value) || 0,
            descuentos:  parseFloat(document.getElementById('descuentos').value) || 0
        };

        const todosLosErrores = [
            ...validarCamposCompletos(empleado),
            ...validarReglas(empleado)
        ];

        if (todosLosErrores.length > 0) {
            mostrarErrores(todosLosErrores);
            return;
        }

        agregarEmpleado(empleado);
    });

cargarEmpleados();