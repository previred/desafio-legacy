/**
 * Modulo principal de la aplicacion.
 * Orquesta la interaccion entre API, UI y Validacion.
 * Se ejecuta al cargar el DOM.
 */
var App = (function () {
    'use strict';

    /** Inicializa eventos y carga datos iniciales. */
    function init() {
        bindFormSubmit();
        bindDeleteButtons();
        bindClearButton();
        cargarEmpleados();
    }

    /** Registra el evento submit del formulario. */
    function bindFormSubmit() {
        document.getElementById('form-empleado').addEventListener('submit', function (e) {
            e.preventDefault();
            agregarEmpleado();
        });
    }

    /** Delega clics en botones "Eliminar" desde la tabla y cards. */
    function bindDeleteButtons() {
        document.addEventListener('click', function (e) {
            if (!e.target.matches('.btn-danger[data-id]')) return;
            var id = parseInt(e.target.getAttribute('data-id'));
            eliminarEmpleado(id);
        });
    }

    /** Registra el evento click del boton limpiar. */
    function bindClearButton() {
        document.getElementById('btn-limpiar').addEventListener('click', limpiarFormulario);
    }

    /** Carga la lista de empleados desde el backend. */
    function cargarEmpleados() {
        UI.showLoading();
        Api.listar()
            .then(renderEmpleados)
            .catch(function (err) {
                UI.showLoadingError();
                console.error('Error:', err);
            });
    }

    /** Renderiza la lista de empleados o muestra estado vacio. */
    function renderEmpleados(empleados) {
        if (empleados.length === 0) {
            UI.showEmpty();
            return;
        }
        UI.showTable();
        UI.renderTable(empleados);
        UI.renderMobileCards(empleados);
    }

    /** Recolecta los datos del formulario como objeto. */
    function getFormData() {
        return {
            nombre: Utils.getFieldValue('nombre'),
            apellido: Utils.getFieldValue('apellido'),
            rut: Utils.getFieldValue('rut'),
            cargo: Utils.getFieldValue('cargo'),
            salario: Utils.getNumericValue('salario'),
            bono: Utils.getNumericValue('bono'),
            descuentos: Utils.getNumericValue('descuentos')
        };
    }

    /** Valida y envia un nuevo empleado al backend. */
    function agregarEmpleado() {
        if (!Validation.validarFormulario()) return;

        UI.setSubmitState(true);
        Api.crear(getFormData())
            .then(handleCrearResponse)
            .catch(handleCrearError);
    }

    /** Procesa la respuesta del POST de creacion. */
    function handleCrearResponse(result) {
        UI.setSubmitState(false);
        if (result.status === 201) {
            UI.showAlert('success', 'Empleado agregado correctamente.');
            limpiarFormulario();
            cargarEmpleados();
        } else {
            UI.showAlert('error', result.body.errores || ['Error desconocido.']);
        }
    }

    /** Maneja errores de red al crear empleado. */
    function handleCrearError(err) {
        UI.setSubmitState(false);
        UI.showAlert('error', ['Error de conexion con el servidor.']);
        console.error('Error:', err);
    }

    /** Solicita confirmacion via SweetAlert2 y elimina un empleado. */
    function eliminarEmpleado(id) {
        UI.confirmar('Esta seguro de eliminar este empleado?')
            .then(function (confirmado) {
                if (!confirmado) return;
                ejecutarEliminacion(id);
            });
    }

    /** Ejecuta la llamada DELETE al backend. */
    function ejecutarEliminacion(id) {
        Api.eliminar(id)
            .then(handleEliminarResponse)
            .catch(function (err) {
                UI.showAlert('error', ['Error de conexion con el servidor.']);
                console.error('Error:', err);
            });
    }

    /** Procesa la respuesta del DELETE. */
    function handleEliminarResponse(result) {
        if (result.status === 200) {
            UI.showAlert('success', 'Empleado eliminado correctamente.');
            cargarEmpleados();
        } else {
            UI.showAlert('error', result.body.errores || ['Error al eliminar.']);
        }
    }

    /** Resetea el formulario a su estado inicial. */
    function limpiarFormulario() {
        document.getElementById('form-empleado').reset();
        document.getElementById('bono').value = '0';
        document.getElementById('descuentos').value = '0';
        UI.clearFieldErrors();
        UI.hideAlerts();
    }

    return { init: init };
})();

document.addEventListener('DOMContentLoaded', App.init);
