'use strict';

const API_URL = '/api/empleados';

function $(id) { return document.getElementById(id); }

function mostrarErrores(errores) {
    const container = $('error-container');
    const lista = $('error-list');
    lista.innerHTML = errores.map(e => `<li>${escapeHtml(e)}</li>`).join('');
    container.classList.add('visible');
}

function limpiarErrores() {
    $('error-container').classList.remove('visible');
    $('error-list').innerHTML = '';
}

function limpiarErroresCampo() {
    document.querySelectorAll('.field-error').forEach(el => el.textContent = '');
    document.querySelectorAll('.input-error').forEach(el => el.classList.remove('input-error'));
}

function mostrarErrorCampo(campo, mensaje) {
    const input = $('campo-' + campo);
    const errorEl = $(campo + '-error');
    if (input) input.classList.add('input-error');
    if (errorEl) errorEl.textContent = mensaje;
}

function escapeHtml(str) {
    const div = document.createElement('div');
    div.appendChild(document.createTextNode(str));
    return div.innerHTML;
}

function formatMoney(value) {
    return '$' + Number(value).toLocaleString('es-CL');
}

function validarRutFormato(rut) {
    if (!rut) return false;
    const rutLimpio = rut.replace(/\./g, '').toUpperCase();
    return /^\d{7,8}-[\dK]$/.test(rutLimpio);
}

function calcularDV(cuerpo) {
    let suma = 0, multiplicador = 2;
    for (let i = cuerpo.length - 1; i >= 0; i--) {
        suma += parseInt(cuerpo[i]) * multiplicador;
        multiplicador = multiplicador === 7 ? 2 : multiplicador + 1;
    }
    const resto = 11 - (suma % 11);
    if (resto === 11) return '0';
    if (resto === 10) return 'K';
    return String(resto);
}

function validarRutCompleto(rut) {
    const rutLimpio = rut.replace(/\./g, '').toUpperCase();
    if (!/^\d{7,8}-[\dK]$/.test(rutLimpio)) return false;
    const [cuerpo, dv] = rutLimpio.split('-');
    return calcularDV(cuerpo) === dv;
}

function validarFormulario() {
    limpiarErroresCampo();
    limpiarErrores();

    let valido = true;

    ['nombre', 'apellido', 'rut', 'cargo', 'salarioBase'].forEach(campo => {
        const valor = $('campo-' + campo).value.trim();
        if (!valor) {
            mostrarErrorCampo(campo, 'Este campo es requerido');
            valido = false;
        }
    });

    const rut = $('campo-rut').value.trim();
    if (rut) {
        if (!validarRutFormato(rut)) {
            mostrarErrorCampo('rut', 'Formato inválido. Use: 12345678-5');
            valido = false;
        } else if (!validarRutCompleto(rut)) {
            mostrarErrorCampo('rut', 'Dígito verificador incorrecto');
            valido = false;
        }
    }

    const salario = parseFloat($('campo-salarioBase').value);
    if (!isNaN(salario) && salario < 400000) {
        mostrarErrorCampo('salarioBase', 'El salario base mínimo es $400.000');
        valido = false;
    }

    return valido;
}

async function cargarEmpleados() {
    const tbody = $('tabla-empleados');
    tbody.innerHTML = '<tr><td colspan="10" class="loading">Cargando...</td></tr>';

    try {
        const res = await fetch(API_URL);
        const empleados = await res.json();

        if (empleados.length === 0) {
            tbody.innerHTML = '<tr><td colspan="10" class="loading">No hay empleados registrados</td></tr>';
            return;
        }

        tbody.innerHTML = empleados.map(e => `
            <tr>
                <td>${e.id}</td>
                <td>${escapeHtml(e.nombre)}</td>
                <td>${escapeHtml(e.apellido)}</td>
                <td>${escapeHtml(e.rut)}</td>
                <td>${escapeHtml(e.cargo)}</td>
                <td>${formatMoney(e.salarioBase)}</td>
                <td>${formatMoney(e.bono)}</td>
                <td>${formatMoney(e.descuentos)}</td>
                <td><span class="badge">${formatMoney(e.salarioNeto)}</span></td>
                <td><button class="btn-danger" onclick="eliminarEmpleado(${e.id})">Eliminar</button></td>
            </tr>
        `).join('');
    } catch {
        tbody.innerHTML = '<tr><td colspan="10" class="loading">Error al cargar empleados</td></tr>';
    }
}

async function agregarEmpleado(event) {
    event.preventDefault();

    if (!validarFormulario()) return;

    const payload = {
        nombre:      $('campo-nombre').value.trim(),
        apellido:    $('campo-apellido').value.trim(),
        rut:         $('campo-rut').value.trim(),
        cargo:       $('campo-cargo').value.trim(),
        salarioBase: parseFloat($('campo-salarioBase').value),
        bono:        parseFloat($('campo-bono').value) || 0,
        descuentos:  parseFloat($('campo-descuentos').value) || 0
    };

    try {
        const res = await fetch(API_URL, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (res.status === 201) {
            $('form-empleado').reset();
            limpiarErrores();
            limpiarErroresCampo();
            await cargarEmpleados();
        } else {
            const data = await res.json();
            mostrarErrores(data.errores || ['Error desconocido del servidor']);
        }
    } catch {
        mostrarErrores(['No se pudo conectar con el servidor']);
    }
}

async function eliminarEmpleado(id) {
    try {
        const res = await fetch(`${API_URL}?id=${id}`, { method: 'DELETE' });

        if (res.status === 204) {
            await cargarEmpleados();
        } else {
            const data = await res.json();
            mostrarErrores(data.errores || ['Error al eliminar el empleado']);
        }
    } catch {
        mostrarErrores(['No se pudo conectar con el servidor']);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    cargarEmpleados();
    $('form-empleado').addEventListener('submit', agregarEmpleado);
});
