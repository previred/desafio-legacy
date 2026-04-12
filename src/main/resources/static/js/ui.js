/**
 * Modulo de manipulacion del DOM.
 * Responsable de renderizar la tabla, cards, alertas y errores de campo.
 */
var UI = (function () {
    'use strict';

    /** Muestra una alerta SweetAlert2 segun el tipo (success/error). */
    function showAlert(type, message) {
        var icon = type === 'success' ? 'success' : 'error';
        var title = type === 'success' ? 'Exito' : 'Error';
        Swal.fire({
            icon: icon,
            title: title,
            html: buildAlertContent(message),
            confirmButtonColor: '#007cba'
        });
    }

    /** Construye el HTML del mensaje para SweetAlert2. */
    function buildAlertContent(message) {
        if (!Array.isArray(message)) return Utils.escapeHtml(message);
        var items = message.map(function (m) {
            return '<li>' + Utils.escapeHtml(m) + '</li>';
        });
        return '<ul style="text-align:left">' + items.join('') + '</ul>';
    }

    /** No-op mantenida por compatibilidad con limpiarFormulario. */
    function hideAlerts() {
        Swal.close();
    }

    /** Muestra un dialogo de confirmacion SweetAlert2. Retorna Promise<boolean>. */
    function confirmar(texto) {
        return Swal.fire({
            title: 'Confirmar',
            text: texto,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#c0392b',
            cancelButtonColor: '#5a6a7a',
            confirmButtonText: 'Si, eliminar',
            cancelButtonText: 'Cancelar'
        }).then(function (result) {
            return result.isConfirmed;
        });
    }

    /** Limpia todos los mensajes de error de campo. */
    function clearFieldErrors() {
        document.querySelectorAll('.field-error').forEach(function (el) {
            el.textContent = '';
        });
        document.querySelectorAll('.input-error').forEach(function (el) {
            el.classList.remove('input-error');
        });
    }

    /** Muestra un error en un campo especifico del formulario. */
    function setFieldError(field, message) {
        var errorEl = document.getElementById('error-' + field);
        var inputEl = document.getElementById(field);
        if (errorEl) errorEl.textContent = message;
        if (inputEl) inputEl.classList.add('input-error');
    }

    /** Muestra el estado de carga y oculta tabla/vacio. */
    function showLoading() {
        document.getElementById('loading').style.display = 'block';
        document.getElementById('tabla-container').style.display = 'none';
        document.getElementById('empty-state').style.display = 'none';
    }

    /** Muestra el estado vacio y oculta carga/tabla. */
    function showEmpty() {
        document.getElementById('loading').style.display = 'none';
        document.getElementById('empty-state').style.display = 'block';
    }

    /** Muestra el contenedor de tabla y oculta carga/vacio. */
    function showTable() {
        document.getElementById('loading').style.display = 'none';
        document.getElementById('tabla-container').style.display = 'block';
    }

    /** Muestra mensaje de error en la zona de carga. */
    function showLoadingError() {
        document.getElementById('loading').textContent = 'Error al cargar empleados.';
    }

    /** Habilita o deshabilita el boton de guardar con texto. */
    function setSubmitState(loading) {
        var btn = document.getElementById('btn-guardar');
        btn.disabled = loading;
        btn.textContent = loading ? 'Guardando...' : 'Guardar Empleado';
    }

    /** Renderiza la tabla desktop con la lista de empleados. */
    function renderTable(empleados) {
        var tbody = document.getElementById('tabla-body');
        tbody.innerHTML = empleados.map(buildTableRow).join('');
    }

    /** Construye una fila HTML de tabla para un empleado. */
    function buildTableRow(e) {
        return '<tr>' +
            '<td>' + e.id + '</td>' +
            '<td>' + Utils.escapeHtml(e.nombre) + ' ' + Utils.escapeHtml(e.apellido) + '</td>' +
            '<td><span class="badge">' + Utils.escapeHtml(e.rut) + '</span></td>' +
            '<td>' + Utils.escapeHtml(e.cargo) + '</td>' +
            '<td class="salary">' + Utils.formatMoney(e.salario) + '</td>' +
            '<td>' + Utils.formatMoney(e.bono) + '</td>' +
            '<td>' + Utils.formatMoney(e.descuentos) + '</td>' +
            '<td class="salary">' + Utils.formatMoney(e.salarioLiquido) + '</td>' +
            '<td><button class="btn btn-danger" data-id="' + e.id + '">Eliminar</button></td>' +
            '</tr>';
    }

    /** Renderiza las cards mobile con la lista de empleados. */
    function renderMobileCards(empleados) {
        var container = document.getElementById('mobile-cards');
        container.innerHTML = empleados.map(buildMobileCard).join('');
    }

    /** Construye una card HTML mobile para un empleado. */
    function buildMobileCard(e) {
        return '<div class="employee-card">' +
            buildCardHeader(e) +
            buildCardField('Cargo', Utils.escapeHtml(e.cargo), '') +
            buildCardField('Salario', Utils.formatMoney(e.salario), 'salary') +
            buildCardField('Bono', Utils.formatMoney(e.bono), '') +
            buildCardField('Descuentos', Utils.formatMoney(e.descuentos), '') +
            buildCardField('Liquido', Utils.formatMoney(e.salarioLiquido), 'salary') +
            '<div class="card-actions">' +
                '<button class="btn btn-danger" data-id="' + e.id + '">Eliminar</button>' +
            '</div>' +
            '</div>';
    }

    /** Construye el encabezado de una card mobile. */
    function buildCardHeader(e) {
        return '<div class="card-header">' +
            '<h3>' + Utils.escapeHtml(e.nombre) + ' ' + Utils.escapeHtml(e.apellido) + '</h3>' +
            '<span class="badge">' + Utils.escapeHtml(e.rut) + '</span>' +
            '</div>';
    }

    /** Construye una fila label-valor para card mobile. */
    function buildCardField(label, value, cssClass) {
        var cls = cssClass ? ' class="' + cssClass + '"' : '';
        return '<div class="card-row">' +
            '<span>' + label + '</span>' +
            '<span' + cls + '>' + value + '</span>' +
            '</div>';
    }

    return {
        showAlert: showAlert,
        hideAlerts: hideAlerts,
        confirmar: confirmar,
        clearFieldErrors: clearFieldErrors,
        setFieldError: setFieldError,
        showLoading: showLoading,
        showEmpty: showEmpty,
        showTable: showTable,
        showLoadingError: showLoadingError,
        setSubmitState: setSubmitState,
        renderTable: renderTable,
        renderMobileCards: renderMobileCards
    };
})();
