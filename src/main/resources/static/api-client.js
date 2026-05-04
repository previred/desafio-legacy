const EmpleadosAPI = (() => {
    const BASE_URL = "/api/empleados";

    function request({ method, url, body, onSuccess, onError }) {
        const xhr = new XMLHttpRequest();
        xhr.open(method, url, true);
        xhr.setRequestHeader("Content-Type", "application/json");

        xhr.onreadystatechange = function () {
            if (xhr.readyState !== XMLHttpRequest.DONE) return;

            let responseData = null;
            try {
                responseData = JSON.parse(xhr.responseText);
            } catch (_) {
                responseData = xhr.responseText;
            }

            if (xhr.status >= 200 && xhr.status < 300) {
                onSuccess && onSuccess(responseData);
            } else {
                onError && onError(xhr.status, responseData);
            }
        };

        xhr.onerror = function () {
            onError && onError(0, { error: "Error de red o conexión rechazada" });
        };

        if (body) {
            xhr.send(JSON.stringify(body));
        } else {
            xhr.send();
        }
    }

    function getAll(onSuccess, onError) {
        request({
            method: "GET",
            url: BASE_URL,
            onSuccess,
            onError,
        });
    }

    function create(employeeData, onSuccess, onError) {
        request({
            method: "POST",
            url: BASE_URL,
            body: employeeData,
            onSuccess,
            onError,
        });
    }

    function remove(id, onSuccess, onError) {
        request({
            method: "DELETE",
            url: `${BASE_URL}/${id}`,
            onSuccess,
            onError,
        });
    }

    return { getAll, create, remove };
})();
