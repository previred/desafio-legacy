const API_URL = '/api/empleados';

function cargarEmpleados() {
    fetch(API_URL)
        .then(response => response.json())
        .then(empleados => {
            const tbody = document.getElementById('tablaEmpleados');
            tbody.innerHTML = '';

            if (empleados.length === 0) {
                const tr = document.createElement('tr');
                const td = document.createElement('td');
                td.setAttribute('colspan', '9');
                td.className = 'sin-empleados';
                td.textContent = 'No hay empleados registrados';
                tr.appendChild(td);
                tbody.appendChild(tr);
                return;
            }

            empleados.forEach(empleado => {
                const fila = document.createElement('tr');

                const celdas = [
                    empleado.id,
                    empleado.nombre,
                    empleado.apellido,
                    empleado.rut,
                    empleado.cargo,
                    '$' + empleado.salarioBase.toLocaleString('es-CL'),
                    '$' + empleado.bono.toLocaleString('es-CL'),
                    '$' + empleado.descuentos.toLocaleString('es-CL')
                ];

                celdas.forEach(valor => {
                    const td = document.createElement('td');
                    td.textContent = valor;
                    fila.appendChild(td);
                });

                const tdAccion = document.createElement('td');
                const boton = document.createElement('button');
                boton.className = 'btn-eliminar';
                boton.textContent = 'Eliminar';
                boton.addEventListener('click', () => eliminarEmpleado(empleado.id));
                tdAccion.appendChild(boton);
                fila.appendChild(tdAccion);

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
            mostrarErrores(['No se pudo eliminar el empleado']);
        }
    })
    .catch(error => {
        console.error('Error al eliminar empleado:', error);
    });
}

function mostrarErrores(errores) {
    const divErrores = document.getElementById('errores');
    divErrores.innerHTML = '';
    errores.forEach(mensaje => {
        const p = document.createElement('p');
        p.textContent = `⚠️ ${mensaje}`;
        divErrores.appendChild(p);
    });
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