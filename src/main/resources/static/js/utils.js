/**
 * Modulo de utilidades generales.
 * Funciones puras sin dependencia del DOM.
 */
var Utils = (function () {
    'use strict';

    /** Formatea un monto numerico como moneda chilena. */
    function formatMoney(amount) {
        return '$' + Number(amount).toLocaleString('es-CL');
    }

    /** Escapa caracteres HTML para prevenir XSS. */
    function escapeHtml(text) {
        var div = document.createElement('div');
        div.appendChild(document.createTextNode(text));
        return div.innerHTML;
    }

    /** Obtiene el valor de un campo de texto recortando espacios. */
    function getFieldValue(id) {
        return document.getElementById(id).value.trim();
    }

    /** Obtiene el valor numerico de un campo (0 si esta vacio). */
    function getNumericValue(id) {
        return parseInt(document.getElementById(id).value) || 0;
    }

    return {
        formatMoney: formatMoney,
        escapeHtml: escapeHtml,
        getFieldValue: getFieldValue,
        getNumericValue: getNumericValue
    };
})();
