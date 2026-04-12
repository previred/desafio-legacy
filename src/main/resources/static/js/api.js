/**
 * Modulo de comunicacion con el backend via Fetch API (AJAX).
 * Centraliza todas las llamadas HTTP al endpoint /api/empleados.
 */
var Api = (function () {
    'use strict';

    var BASE_URL = '/api/empleados';

    /** Obtiene la lista de empleados (GET). */
    function listar() {
        return fetch(BASE_URL).then(parseJson);
    }

    /** Crea un nuevo empleado (POST). Retorna {status, body}. */
    function crear(data) {
        return fetch(BASE_URL, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        }).then(parseResponse);
    }

    /** Elimina un empleado por ID (DELETE). Retorna {status, body}. */
    function eliminar(id) {
        return fetch(BASE_URL + '/' + id, {
            method: 'DELETE'
        }).then(parseResponse);
    }

    /** Parsea la respuesta JSON directamente. */
    function parseJson(res) {
        return res.json();
    }

    /** Parsea la respuesta incluyendo el status HTTP. */
    function parseResponse(res) {
        return res.json().then(function (body) {
            return { status: res.status, body: body };
        });
    }

    return {
        listar: listar,
        crear: crear,
        eliminar: eliminar
    };
})();
