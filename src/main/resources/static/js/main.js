(function (window, document) {
    "use strict";

    var DEBUG = false;

    function debugLog(message, payload) {
        if (!DEBUG) {
            return;
        }
        if (payload === undefined) {
            console.log("[empleadoMain] " + message);
            return;
        }
        console.log("[empleadoMain] " + message, payload);
    }

    var form = document.getElementById("empleado-form");
    var formErrors = document.getElementById("form-errors");
    var formSuccess = document.getElementById("form-success");
    var listErrors = document.getElementById("list-errors");
    var tbody = document.getElementById("empleados-tbody");
    var reloadButton = document.getElementById("btn-recargar");
    var openModalButton = document.getElementById("btn-open-modal");
    var closeModalButton = document.getElementById("btn-close-modal");
    var cancelModalButton = document.getElementById("btn-cancelar-modal");
    var searchButton = document.getElementById("btn-buscar");
    var clearFiltersButton = document.getElementById("btn-limpiar-filtros");
    var modal = document.getElementById("empleado-modal");
    var modalOverlay = document.getElementById("modal-overlay");
    var filterInput = document.getElementById("filtro-empleado");
    var cargoFilterInput = document.getElementById("filtro-cargo");
    var toastContainer = document.getElementById("toast-container");
    var latestRequestId = 0;

    function openModal() {
        if (!modal || !modalOverlay) {
            return;
        }
        modal.removeAttribute("hidden");
        modalOverlay.removeAttribute("hidden");
        modal.style.display = "block";
        modalOverlay.style.display = "block";
    }

    function closeModal() {
        if (!modal || !modalOverlay) {
            return;
        }
        modal.setAttribute("hidden", "hidden");
        modalOverlay.setAttribute("hidden", "hidden");
        modal.style.display = "none";
        modalOverlay.style.display = "none";
        window.empleadoUi.clearMessages(formErrors);
        window.empleadoUi.clearMessages(formSuccess);
        form.reset();
        Array.prototype.forEach.call(form.querySelectorAll("input[aria-invalid='true']"), function (input) {
            input.removeAttribute("aria-invalid");
        });
        Array.prototype.forEach.call(form.querySelectorAll(".field-error"), function (errorNode) {
            errorNode.textContent = "";
            errorNode.style.display = "none";
        });
    }

    function getSearchFilters() {
        return {
            q: filterInput ? String(filterInput.value || "").trim() : "",
            cargo: cargoFilterInput ? String(cargoFilterInput.value || "").trim() : ""
        };
    }

    function loadEmpleados(filters) {
        debugLog("Cargando empleados...");
        window.empleadoUi.clearMessages(listErrors);
        var requestId = latestRequestId + 1;
        latestRequestId = requestId;

        window.empleadoApi.fetchEmpleados(filters || getSearchFilters())
            .then(function (empleados) {
                if (requestId !== latestRequestId) {
                    debugLog("Respuesta descartada por request antigua", { requestId: requestId, latest: latestRequestId });
                    return;
                }
                debugLog("Empleados recibidos", empleados);
                window.empleadoUi.renderEmpleados(tbody, empleados, handleDeleteEmpleado);
            })
            .catch(function (error) {
                if (requestId !== latestRequestId) {
                    return;
                }
                debugLog("Error al cargar empleados", error);
                var errors = window.empleadoEvents.extractErrorMessages(error, "No se pudo cargar la lista de empleados");
                window.empleadoUi.renderMessages(listErrors, errors);
                window.empleadoUi.showToast(toastContainer, "error", errors[0]);
            });
    }

    function handleDeleteEmpleado(id) {
        debugLog("Eliminando empleado id=" + id);
        window.empleadoUi.clearMessages(listErrors);

        window.empleadoApi.deleteEmpleado(id)
            .then(function () {
                debugLog("Eliminacion OK id=" + id);
                window.empleadoUi.showToast(toastContainer, "success", "Empleado eliminado correctamente");
                loadEmpleados();
            })
            .catch(function (error) {
                debugLog("Error al eliminar empleado id=" + id, error);
                var errors = window.empleadoEvents.extractErrorMessages(error, "No se pudo eliminar el empleado");
                window.empleadoUi.renderMessages(listErrors, errors);
                window.empleadoUi.showToast(toastContainer, "error", errors[0]);
            });
    }

    if (openModalButton) {
        openModalButton.addEventListener("click", openModal);
    }
    if (closeModalButton) {
        closeModalButton.addEventListener("click", closeModal);
    }
    if (cancelModalButton) {
        cancelModalButton.addEventListener("click", closeModal);
    }
    if (modalOverlay) {
        modalOverlay.addEventListener("click", closeModal);
    }
    if (searchButton) {
        searchButton.addEventListener("click", function () {
            loadEmpleados(getSearchFilters());
        });
    }

    if (clearFiltersButton) {
        clearFiltersButton.addEventListener("click", function () {
            if (filterInput) {
                filterInput.value = "";
            }
            if (cargoFilterInput) {
                cargoFilterInput.value = "";
            }
            loadEmpleados({ q: "", cargo: "" });
        });
    }

    if (filterInput) {
        filterInput.addEventListener("keydown", function (event) {
            if (event.key === "Enter") {
                loadEmpleados(getSearchFilters());
            }
        });
    }

    if (cargoFilterInput) {
        cargoFilterInput.addEventListener("keydown", function (event) {
            if (event.key === "Enter") {
                loadEmpleados(getSearchFilters());
            }
        });
    }

    window.empleadoEvents.bindEvents({
        form: form,
        formErrors: formErrors,
        formSuccess: formSuccess,
        reloadButton: reloadButton,
        toastContainer: toastContainer,
        onCreated: closeModal,
        api: window.empleadoApi,
        ui: window.empleadoUi,
        validation: window.empleadoValidation,
        loadEmpleados: loadEmpleados
    });

    document.addEventListener("keydown", function (event) {
        if (event.key === "Escape" && modal && !modal.hasAttribute("hidden")) {
            closeModal();
        }
    });

    loadEmpleados();
})(window, document);
