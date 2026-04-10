"use strict";

// Constantes y Utilidades

const API_URL = '/api/empleados';

const mostrarSpinner = () => document.getElementById('spinnerGlobal').classList.remove('oculto');
const ocultarSpinner = () => document.getElementById('spinnerGlobal').classList.add('oculto');

function obtenerMontoNumerico(inputId) {
    const raw = document.getElementById(inputId).value.replaceAll('.', '').trim();
    if (raw === '') return 0;
    return Number.parseInt(raw, 10);
}

function mostrarMensaje(contenedor, tipo, texto) {
    contenedor.className = 'mensaje ' + tipo;
    contenedor.innerText = texto;
}

function crearCelda(texto) {
    const td = document.createElement('td');
    td.textContent = texto;
    return td;
}

// Validaciones

function validarEmpleado(empleado) {
    if (!empleado.nombre || !empleado.apellido || !empleado.rut || !empleado.cargo) {
        return 'Por favor, complete todos los campos de texto obligatorios.';
    }
    if (Number.isNaN(empleado.salarioBase)) {
        return 'El salario base es obligatorio.';
    }
    if (!/^\d{7,8}-[\dkK]$/.test(empleado.rut)) {
        return 'Formato de RUT inválido (Ej: 12345678-9 o 1234567-K)';
    }
    if (empleado.salarioBase < 400000) {
        return 'El salario base no puede ser inferior a $400.000';
    }
    return null;
}

// Operaciones Fetch API

async function registrarEmpleado(e) {
    e.preventDefault();

    const empleado = {
        nombre: document.getElementById('nombre').value.trim(),
        apellido: document.getElementById('apellido').value.trim(),
        rut: document.getElementById('rut').value.trim(),
        cargo: document.getElementById('cargo').value.trim(),
        salarioBase: obtenerMontoNumerico('salarioBase'),
        bonos: obtenerMontoNumerico('bonos'),
        descuentos: obtenerMontoNumerico('descuentos')
    };

    const contenedor = document.getElementById('mensaje');
    const errorValidacion = validarEmpleado(empleado);

    if (errorValidacion) {
        mostrarMensaje(contenedor, 'error', errorValidacion);
        return;
    }

    mostrarSpinner();

    try {
        const response = await fetch(API_URL, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(empleado)
        });
        const data = await response.json();

        ocultarSpinner();

        if (response.ok) {
            mostrarMensaje(contenedor, 'exito', data.mensaje);
            document.getElementById('empleadoForm').reset();
            cargarEmpleados();
        } else {
            mostrarMensaje(contenedor, 'error', data.mensaje || 'Error interno del servidor');
        }
    } catch (error) {
        ocultarSpinner();
        mostrarMensaje(contenedor, 'error', 'Error: No hay conexión con el servidor.');
        console.error('Error:', error);
    }
}

async function cargarEmpleados() {
    mostrarSpinner();

    try {
        const response = await fetch(API_URL);
        const data = await response.json();

        const tbody = document.querySelector('#tablaEmpleados tbody');
        tbody.innerHTML = '';

        data.forEach(emp => {
            const liquido = (emp.salarioBase + emp.bonos) - emp.descuentos;
            const formatBase = emp.salarioBase.toLocaleString('es-CL');
            const formatLiquido = liquido.toLocaleString('es-CL');

            const tr = document.createElement('tr');
            tr.appendChild(crearCelda(emp.rut));
            tr.appendChild(crearCelda(emp.nombre + ' ' + emp.apellido));
            tr.appendChild(crearCelda(emp.cargo));
            tr.appendChild(crearCelda('$' + formatBase));

            const tdLiquido = document.createElement('td');
            const strong = document.createElement('strong');
            strong.textContent = '$' + formatLiquido;
            tdLiquido.appendChild(strong);
            tr.appendChild(tdLiquido);

            const tdAccion = document.createElement('td');
            const btnEliminar = document.createElement('button');
            btnEliminar.textContent = 'Eliminar';
            btnEliminar.className = 'btn-eliminar';
            btnEliminar.addEventListener('click', () => eliminarEmpleado(emp.id));
            tdAccion.appendChild(btnEliminar);
            tr.appendChild(tdAccion);

            tbody.appendChild(tr);
        });

        ocultarSpinner();
    } catch (error) {
        ocultarSpinner();
        const tbody = document.querySelector('#tablaEmpleados tbody');
        tbody.innerHTML = '';
        const tr = document.createElement('tr');
        const td = document.createElement('td');
        td.setAttribute('colspan', '6');
        td.style.textAlign = 'center';
        td.style.color = 'red';
        td.textContent = 'Error de conexión con el servidor.';
        tr.appendChild(td);
        tbody.appendChild(tr);
        console.error(error);
    }
}

async function eliminarEmpleado(id) {
    const contenedor = document.getElementById('mensajeTabla');

    mostrarSpinner();

    try {
        const response = await fetch(`${API_URL}?id=${id}`, {
            method: 'DELETE'
        });

        ocultarSpinner();

        if (response.ok) {
            mostrarMensaje(contenedor, 'exito', 'Registro eliminado');
            cargarEmpleados();
        } else {
            const data = await response.json();
            mostrarMensaje(contenedor, 'error', data.mensaje || 'No se pudo eliminar el registro');
        }
    } catch (error) {
        ocultarSpinner();
        mostrarMensaje(contenedor, 'error', 'Error de conexión');
        console.error('Error al eliminar:', error);
    }
}

// Formateo de Inputs Monetarios

function formatearEntradaMoneda(e) {
    let value = e.target.value.replaceAll(/\D/g, '');
    if (value === '') {
        e.target.value = '';
        return;
    }
    e.target.value = Number.parseInt(value, 10).toLocaleString('es-CL');
}

// Inicialización y Event Listeners

document.addEventListener('DOMContentLoaded', () => {
    cargarEmpleados();

    document.getElementById('empleadoForm').addEventListener('submit', registrarEmpleado);
    document.getElementById('btnRecargar').addEventListener('click', cargarEmpleados);

    document.getElementById('salarioBase').addEventListener('input', formatearEntradaMoneda);
    document.getElementById('bonos').addEventListener('input', formatearEntradaMoneda);
    document.getElementById('descuentos').addEventListener('input', formatearEntradaMoneda);
});
