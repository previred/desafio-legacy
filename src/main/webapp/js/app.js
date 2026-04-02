document.addEventListener('DOMContentLoaded', function() {
    cargarEmpleados();
    configurarFormulario();
    configurarValidacionTiempoReal();
    configurarFormatoMoneda();
});

function configurarFormatoMoneda() {
    const camposMoneda = ['salarioBase', 'bonos', 'descuentos'];

    camposMoneda.forEach(campo => {
        const input = document.getElementById(campo);

        input.addEventListener('input', function(e) {
            let valor = e.target.value.replace(/\D/g, '');

            if (valor) {
                valor = parseInt(valor, 10);
                e.target.value = formatearNumero(valor);
            }
        });

        input.addEventListener('blur', function(e) {
            let valor = e.target.value.replace(/\D/g, '');
            if (!valor) {
                e.target.value = '0';
            }
        });
    });
}

function formatearNumero(numero) {
    return numero.toLocaleString('es-CL');
}

function parsearNumero(texto) {
    return parseInt(texto.replace(/\D/g, ''), 10) || 0;
}

async function cargarEmpleados() {
    const loadingIndicator = document.getElementById('loadingIndicator');
    const tableBody = document.getElementById('empleadosTableBody');

    try {
        loadingIndicator.style.display = 'block';
        const empleados = await EmpleadoAPI.obtenerEmpleados();

        tableBody.innerHTML = '';

        if (empleados.length === 0) {
            tableBody.innerHTML = '<tr><td colspan="10" class="no-data">No hay empleados registrados</td></tr>';
        } else {
            empleados.forEach(empleado => {
                const row = crearFilaEmpleado(empleado);
                tableBody.appendChild(row);
            });
        }
    } catch (error) {
        tableBody.innerHTML = '<tr><td colspan="10" class="no-data">Error al cargar empleados</td></tr>';
    } finally {
        loadingIndicator.style.display = 'none';
    }
}

function crearFilaEmpleado(empleado) {
    const row = document.createElement('tr');

    row.innerHTML = `
        <td>${empleado.id}</td>
        <td>${empleado.nombre}</td>
        <td>${empleado.apellido}</td>
        <td>${empleado.rut}</td>
        <td>${empleado.cargo}</td>
        <td class="currency">$${formatNumber(empleado.salarioBase)}</td>
        <td class="currency">$${formatNumber(empleado.bonos)}</td>
        <td class="currency">$${formatNumber(empleado.descuentos)}</td>
        <td class="currency">$${formatNumber(empleado.salarioTotal)}</td>
        <td>
            <button class="btn btn-danger" onclick="eliminarEmpleado(${empleado.id})">Eliminar</button>
        </td>
    `;

    return row;
}

function configurarFormulario() {
    const form = document.getElementById('empleadoForm');

    form.addEventListener('submit', async function(e) {
        e.preventDefault();

        Validator.limpiarErrores();

        const formData = obtenerDatosFormulario();

        if (!validarFormulario(formData)) {
            return;
        }

        const result = await EmpleadoAPI.crearEmpleado(formData);

        if (result.success) {
            form.reset();
            Validator.limpiarErrores();
            await cargarEmpleados();
        } else {
            mostrarErroresFormulario(result.errors);
        }
    });

    form.addEventListener('reset', function() {
        Validator.limpiarErrores();
    });
}

function configurarValidacionTiempoReal() {
    const rutInput = document.getElementById('rut');
    const salarioInput = document.getElementById('salarioBase');
    const bonosInput = document.getElementById('bonos');
    const descuentosInput = document.getElementById('descuentos');

    rutInput.addEventListener('input', function() {
        const formatted = Validator.formatearRut(this.value);
        this.value = formatted;
    });

    rutInput.addEventListener('blur', function() {
        const resultado = Validator.validarRut(this.value);
        Validator.mostrarError('rut', resultado.valido ? '' : resultado.mensaje);
    });

    salarioInput.addEventListener('blur', function() {
        const salario = parsearNumero(this.value);
        const resultado = Validator.validarSalarioMinimo(salario);
        Validator.mostrarError('salarioBase', resultado.valido ? '' : resultado.mensaje);

        validarBonosDescuentos();
    });

    bonosInput.addEventListener('blur', validarBonosDescuentos);
    descuentosInput.addEventListener('blur', validarBonosDescuentos);
}

function validarBonosDescuentos() {
    const salarioBase = parsearNumero(document.getElementById('salarioBase').value);
    const bonos = parsearNumero(document.getElementById('bonos').value);
    const descuentos = parsearNumero(document.getElementById('descuentos').value);

    const resultadoBonos = Validator.validarBonos(salarioBase, bonos);
    Validator.mostrarError('bonos', resultadoBonos.valido ? '' : resultadoBonos.mensaje);

    const resultadoDescuentos = Validator.validarDescuentos(salarioBase, descuentos);
    Validator.mostrarError('descuentos', resultadoDescuentos.valido ? '' : resultadoDescuentos.mensaje);
}

function obtenerDatosFormulario() {
    return {
        nombre: document.getElementById('nombre').value.trim(),
        apellido: document.getElementById('apellido').value.trim(),
        rut: document.getElementById('rut').value.trim(),
        cargo: document.getElementById('cargo').value.trim(),
        salarioBase: parsearNumero(document.getElementById('salarioBase').value),
        bonos: parsearNumero(document.getElementById('bonos').value),
        descuentos: parsearNumero(document.getElementById('descuentos').value)
    };
}

function validarFormulario(data) {
    let valido = true;

    if (!data.nombre) {
        Validator.mostrarError('nombre', 'El nombre es obligatorio');
        valido = false;
    }

    if (!data.apellido) {
        Validator.mostrarError('apellido', 'El apellido es obligatorio');
        valido = false;
    }

    const resultadoRut = Validator.validarRut(data.rut);
    if (!resultadoRut.valido) {
        Validator.mostrarError('rut', resultadoRut.mensaje);
        valido = false;
    }

    if (!data.cargo) {
        Validator.mostrarError('cargo', 'El cargo es obligatorio');
        valido = false;
    }

    const resultadoSalario = Validator.validarSalarioMinimo(data.salarioBase);
    if (!resultadoSalario.valido) {
        Validator.mostrarError('salarioBase', resultadoSalario.mensaje);
        valido = false;
    }

    const resultadoBonos = Validator.validarBonos(data.salarioBase, data.bonos);
    if (!resultadoBonos.valido) {
        Validator.mostrarError('bonos', resultadoBonos.mensaje);
        valido = false;
    }

    const resultadoDescuentos = Validator.validarDescuentos(data.salarioBase, data.descuentos);
    if (!resultadoDescuentos.valido) {
        Validator.mostrarError('descuentos', resultadoDescuentos.mensaje);
        valido = false;
    }

    return valido;
}

function mostrarErroresFormulario(errores) {
    const errorBox = document.getElementById('formErrors');

    if (errores && errores.length > 0) {
        let html = '<ul>';
        errores.forEach(error => {
            html += `<li>${error}</li>`;
        });
        html += '</ul>';

        errorBox.innerHTML = html;
        errorBox.style.display = 'block';
    }
}

async function eliminarEmpleado(id) {
    const errorBox = document.getElementById('formErrors');

    try {
        await EmpleadoAPI.eliminarEmpleado(id);
        await cargarEmpleados();
    } catch (error) {
        errorBox.innerHTML = '<ul><li>Error al eliminar empleado. Por favor, intente nuevamente.</li></ul>';
        errorBox.style.display = 'block';
        setTimeout(() => {
            errorBox.style.display = 'none';
        }, 5000);
    }
}

function formatNumber(num) {
    if (num === null || num === undefined) {
        return '0';
    }
    return num.toLocaleString('es-CL', { minimumFractionDigits: 0, maximumFractionDigits: 2 });
}
