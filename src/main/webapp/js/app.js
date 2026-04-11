const API_URL = '/api/empleados';
const SALARIO_MINIMO = 400000;

document.addEventListener('DOMContentLoaded', () => {
    cargarEmpleados();
    setupForm();
    setupRutFormatter();
});

function setupRutFormatter() {
    const rutInput = document.getElementById('rut');
    rutInput.addEventListener('blur', () => {
        const normalizado = normalizarRut(rutInput.value);
        if (normalizado.length >= 8 && normalizado.length <= 9) {
            rutInput.value = formatearRut(normalizado);
        }
    });
}

function normalizarRut(rut) {
    return rut.replace(/\./g, '').replace(/-/g, '').toUpperCase();
}

function esRutValido(rut) {
    const normalizado = normalizarRut(rut);
    
    if (normalizado.length < 8 || normalizado.length > 9) {
        return false;
    }
    
    const cuerpo = normalizado.substring(0, normalizado.length - 1);
    const verificador = normalizado.charAt(normalizado.length - 1);
    
    for (var i = 0; i < cuerpo.length; i++) {
        if (!esDigito(cuerpo.charAt(i))) {
            return false;
        }
    }
    
    var verificadorEsperado = calcularVerificador(cuerpo);
    return verificador === verificadorEsperado;
}

function esDigito(caracter) {
    return caracter >= '0' && caracter <= '9';
}

function calcularVerificador(cuerpo) {
    var suma = 0;
    var multiplicador = 2;
    
    for (var i = cuerpo.length - 1; i >= 0; i--) {
        suma = suma + parseInt(cuerpo.charAt(i)) * multiplicador;
        multiplicador++;
        if (multiplicador === 8) {
            multiplicador = 2;
        }
    }
    
    var resto = suma % 11;
    var verificador = 11 - resto;
    
    if (verificador === 10) {
        return 'K';
    } else if (verificador === 11) {
        return '0';
    } else {
        return verificador.toString();
    }
}

function formatearRut(rut) {
    if (rut.length < 7) {
        return rut;
    }
    
    var cuerpo = rut.substring(0, rut.length - 1);
    var verificador = rut.charAt(rut.length - 1);
    
    var cuerpoFormateado = '';
    for (var i = 0; i < cuerpo.length; i++) {
        if (i > 0 && (cuerpo.length - i) % 3 === 0) {
            cuerpoFormateado = cuerpoFormateado + '.';
        }
        cuerpoFormateado = cuerpoFormateado + cuerpo.charAt(i);
    }
    
    return cuerpoFormateado + '-' + verificador;
}

function setupForm() {
    const form = document.getElementById('empleadoForm');
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        if (validarFormulario()) {
            await crearEmpleado();
        }
    });
}

function validarFormulario() {
    let esValido = true;
    limpiarErrores();

    const nombre = document.getElementById('nombre').value.trim();
    const apellido = document.getElementById('apellido').value.trim();
    const rut = document.getElementById('rut').value.trim();
    const cargo = document.getElementById('cargo').value.trim();
    const salario = parseFloat(document.getElementById('salario').value);
    const bono = parseFloat(document.getElementById('bono').value) || 0;
    const descuentos = parseFloat(document.getElementById('descuentos').value) || 0;

    if (!nombre) {
        mostrarError('nombre', 'El nombre es requerido');
        esValido = false;
    }

    if (!apellido) {
        mostrarError('apellido', 'El apellido es requerido');
        esValido = false;
    }

    if (!rut) {
        mostrarError('rut', 'El RUT es requerido');
        esValido = false;
    } else if (!esRutValido(rut)) {
        mostrarError('rut', 'RUT invalido');
        esValido = false;
    }

    if (!cargo) {
        mostrarError('cargo', 'El cargo es requerido');
        esValido = false;
    }

    if (!salario || salario < SALARIO_MINIMO) {
        mostrarError('salario', 'Salario debe ser >= $400,000');
        esValido = false;
    }

    if (bono > salario * 0.5) {
        mostrarError('bono', 'Bono no puede superar 50% del salario');
        esValido = false;
    }

    if (descuentos > salario) {
        mostrarError('descuentos', 'Descuentos no pueden superar el salario');
        esValido = false;
    }

    return esValido;
}

function mostrarError(campo, mensaje) {
    const errorSpan = document.getElementById('error-' + campo);
    if (errorSpan) {
        errorSpan.textContent = mensaje;
    }
}

function limpiarErrores() {
    document.querySelectorAll('.error').forEach(el => el.textContent = '');
}

async function cargarEmpleados() {
    try {
        const response = await fetch(API_URL);
        if (!response.ok) throw new Error('Error al cargar empleados');
        const empleados = await response.json();
        renderizarTabla(empleados);
    } catch (error) {
        mostrarMensaje('Error al cargar la lista de empleados', 'error');
    }
}

function renderizarTabla(empleados) {
    const tbody = document.getElementById('empleadosBody');
    tbody.innerHTML = '';

    if (empleados.length === 0) {
        tbody.innerHTML = '<tr><td colspan="9" style="text-align:center;">No hay empleados registrados</td></tr>';
        return;
    }

    empleados.forEach(emp => {
        const tr = document.createElement('tr');
        tr.innerHTML = '<td>' + emp.id + '</td>' +
            '<td>' + emp.nombre + '</td>' +
            '<td>' + emp.apellido + '</td>' +
            '<td>' + emp.rut + '</td>' +
            '<td>' + emp.cargo + '</td>' +
            '<td>$' + formatearNumero(emp.salario) + '</td>' +
            '<td>$' + formatearNumero(emp.bono) + '</td>' +
            '<td>$' + formatearNumero(emp.descuentos) + '</td>' +
            '<td><button class="delete-btn" onclick="eliminarEmpleado(' + emp.id + ')">Eliminar</button></td>';
        tbody.appendChild(tr);
    });
}

function formatearNumero(num) {
    return num ? num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, '.') : '0';
}

async function crearEmpleado() {
    const empleado = {
        nombre: document.getElementById('nombre').value.trim(),
        apellido: document.getElementById('apellido').value.trim(),
        rut: document.getElementById('rut').value.trim(),
        cargo: document.getElementById('cargo').value.trim(),
        salario: parseFloat(document.getElementById('salario').value),
        bono: parseFloat(document.getElementById('bono').value) || 0,
        descuentos: parseFloat(document.getElementById('descuentos').value) || 0
    };

    try {
        const response = await fetch(API_URL, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(empleado)
        });

        const data = await response.json();

        if (response.status === 400) {
            if (data.errores && Array.isArray(data.errores)) {
                data.errores.forEach(err => mostrarError(err.campo, err.mensaje));
            } else {
                mostrarMensaje('Error de validacion', 'error');
            }
            return;
        }

        if (response.status === 201) {
            mostrarMensaje('Empleado creado exitosamente', 'success');
            document.getElementById('empleadoForm').reset();
            await cargarEmpleados();
        }
    } catch (error) {
        mostrarMensaje('Error al crear empleado', 'error');
    }
}

async function eliminarEmpleado(id) {
    if (!confirm('Esta seguro de eliminar este empleado?')) return;

    try {
        const response = await fetch(API_URL + '/' + id, { method: 'DELETE' });

        if (response.status === 204) {
            mostrarMensaje('Empleado eliminado', 'success');
            await cargarEmpleados();
        } else if (response.status === 404) {
            mostrarMensaje('Empleado no encontrado', 'error');
        } else {
            mostrarMensaje('Error al eliminar empleado', 'error');
        }
    } catch (error) {
        mostrarMensaje('Error al eliminar empleado', 'error');
    }
}

function mostrarMensaje(texto, tipo) {
    const msg = document.getElementById('message');
    msg.textContent = texto;
    msg.className = 'message ' + tipo;
    setTimeout(() => { msg.className = 'message'; }, 3000);
}
