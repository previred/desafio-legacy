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
                mostrarError(error.error);
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

function mostrarError(mensaje) {
    const divErrores = document.getElementById('errores');
    divErrores.innerHTML = `<p>⚠️ ${mensaje}</p>`;
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

        agregarEmpleado(empleado);
    });

cargarEmpleados();