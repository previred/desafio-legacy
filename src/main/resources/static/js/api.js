(function (window) {
    "use strict";

    var BASE_URL = "/api/empleados";
    var DEBUG = false;

    function debugLog(message, payload) {
        if (!DEBUG) {
            return;
        }
        if (payload === undefined) {
            console.log("[empleadoApi] " + message);
            return;
        }
        console.log("[empleadoApi] " + message, payload);
    }

    function parseJsonSafe(response) {
        return response.text().then(function (raw) {
            if (!raw) {
                return null;
            }

            try {
                return JSON.parse(raw);
            } catch (e) {
                return null;
            }
        });
    }

    function buildHttpError(response, data) {
        var error = new Error("HTTP " + response.status);
        error.status = response.status;
        error.url = response.url;
        error.data = data;
        return error;
    }

    function fetchEmpleados(filters) {
        var query = new URLSearchParams();
        if (filters && filters.q) {
            query.set("q", String(filters.q));
        }
        if (filters && filters.cargo) {
            query.set("cargo", String(filters.cargo));
        }

        var url = query.toString() ? BASE_URL + "?" + query.toString() : BASE_URL;

        debugLog("GET " + url + " - request");
        return fetch(url, {
            cache: "no-store",
            headers: {
                "Cache-Control": "no-cache"
            }
        })
            .then(function (response) {
                return parseJsonSafe(response).then(function (data) {
                    debugLog("GET " + url + " - response", {
                        status: response.status,
                        ok: response.ok,
                        body: data
                    });
                    if (!response.ok) {
                        throw buildHttpError(response, data);
                    }
                    return data || [];
                });
            });
    }

    function createEmpleado(payload) {
        debugLog("POST " + BASE_URL + " - request", payload);
        return fetch(BASE_URL, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        }).then(function (response) {
            return parseJsonSafe(response).then(function (data) {
                debugLog("POST " + BASE_URL + " - response", {
                    status: response.status,
                    ok: response.ok,
                    body: data
                });
                if (!response.ok) {
                    throw buildHttpError(response, data);
                }
                return data;
            });
        });
    }

    function deleteEmpleado(id) {
        debugLog("DELETE " + BASE_URL + "/" + id + " - request");
        return fetch(BASE_URL + "/" + id, {
            method: "DELETE"
        }).then(function (response) {
            return parseJsonSafe(response).then(function (data) {
                debugLog("DELETE " + BASE_URL + "/" + id + " - response", {
                    status: response.status,
                    ok: response.ok,
                    body: data
                });
                if (!response.ok) {
                    throw buildHttpError(response, data);
                }
                return true;
            });
        });
    }

    window.empleadoApi = {
        fetchEmpleados: fetchEmpleados,
        createEmpleado: createEmpleado,
        deleteEmpleado: deleteEmpleado
    };
})(window);
