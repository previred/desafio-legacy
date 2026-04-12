(function (window, document) {
    "use strict";

    var DEBUG = false;
    var TOAST_DURATION_MS = 4000;
    var toastTimer = null;

    function debugLog(message, payload) {
        if (!DEBUG) {
            return;
        }
        if (payload === undefined) {
            console.log("[empleadoUi] " + message);
            return;
        }
        console.log("[empleadoUi] " + message, payload);
    }

    function formatMoney(value) {
        var number = Number(value || 0);
        return "$ " + number.toLocaleString("es-CL", {
            minimumFractionDigits: 0,
            maximumFractionDigits: 2
        });
    }

    function clearMessages(element) {
        element.style.display = "none";
        while (element.firstChild) {
            element.removeChild(element.firstChild);
        }
    }

    function renderMessages(element, messages) {
        if (!messages || messages.length === 0) {
            clearMessages(element);
            return;
        }

        debugLog("Renderizando mensajes", messages);

        var list = document.createElement("ul");
        messages.forEach(function (message) {
            var item = document.createElement("li");
            item.textContent = String(message);
            list.appendChild(item);
        });

        clearMessages(element);
        element.appendChild(list);
        element.style.display = "block";
    }

    function clearToast(toastContainer) {
        if (toastTimer) {
            window.clearTimeout(toastTimer);
            toastTimer = null;
        }
        if (!toastContainer) {
            return;
        }
        toastContainer.classList.remove("toast-show");
        toastContainer.classList.remove("toast-success");
        toastContainer.classList.remove("toast-error");
        toastContainer.classList.remove("toast-warn");
        toastContainer.textContent = "";
    }

    function showToast(toastContainer, type, message) {
        if (!toastContainer) {
            return;
        }

        clearToast(toastContainer);

        var normalizedType = type === "error" || type === "warn" ? type : "success";
        toastContainer.classList.add("toast-" + normalizedType);
        toastContainer.classList.add("toast-show");
        toastContainer.textContent = String(message || "Operacion completada");

        toastTimer = window.setTimeout(function () {
            clearToast(toastContainer);
        }, TOAST_DURATION_MS);
    }

    function createCell(row, value) {
        var cell = document.createElement("td");
        cell.textContent = value;
        row.appendChild(cell);
    }

    function renderEmpleados(tbody, empleados, onDelete) {
        tbody.innerHTML = "";
        debugLog("Render empleados", empleados);

        if (!empleados || empleados.length === 0) {
            var emptyRow = document.createElement("tr");
            var emptyCell = document.createElement("td");
            emptyCell.setAttribute("colspan", "9");
            emptyCell.textContent = "No hay empleados registrados.";
            emptyRow.appendChild(emptyCell);
            tbody.appendChild(emptyRow);
            return;
        }

        empleados.forEach(function (empleado) {
            var row = document.createElement("tr");

            createCell(row, String(empleado.id));
            createCell(row, String(empleado.nombre));
            createCell(row, String(empleado.apellido));
            createCell(row, String(empleado.rutDni));
            createCell(row, String(empleado.cargo));
            createCell(row, formatMoney(empleado.salarioBase));
            createCell(row, formatMoney(empleado.bono));
            createCell(row, formatMoney(empleado.descuentos));

            var actionsCell = document.createElement("td");
            var deleteButton = document.createElement("button");
            deleteButton.className = "btn-danger";
            deleteButton.setAttribute("data-id", String(empleado.id));
            deleteButton.textContent = "Eliminar";
            actionsCell.appendChild(deleteButton);
            row.appendChild(actionsCell);

            tbody.appendChild(row);
        });

        var buttons = tbody.querySelectorAll("button[data-id]");
        Array.prototype.forEach.call(buttons, function (button) {
            button.addEventListener("click", function () {
                var id = Number(button.getAttribute("data-id"));
                onDelete(id);
            });
        });
    }

    window.empleadoUi = {
        clearMessages: clearMessages,
        renderMessages: renderMessages,
        renderEmpleados: renderEmpleados,
        showToast: showToast,
        clearToast: clearToast
    };
})(window, document);
