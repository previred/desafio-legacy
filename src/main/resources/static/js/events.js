(function (window, document) {
    "use strict";

    var DEBUG = false;

    function debugLog(message, payload) {
        if (!DEBUG) {
            return;
        }
        if (payload === undefined) {
            console.log("[empleadoEvents] " + message);
            return;
        }
        console.log("[empleadoEvents] " + message, payload);
    }

    function getFormPayload(form) {
        return {
            nombre: form.nombre.value,
            apellido: form.apellido.value,
            rutDni: form.rutDni.value,
            cargo: form.cargo.value,
            salarioBase: form.salarioBase.value,
            bono: form.bono.value,
            descuentos: form.descuentos.value
        };
    }

    function toApiPayload(payload) {
        var salarioBase = Number(payload.salarioBase);
        var bono = payload.bono === "" ? 0 : Number(payload.bono);
        var descuentos = payload.descuentos === "" ? 0 : Number(payload.descuentos);

        if (Number.isNaN(salarioBase) || Number.isNaN(bono) || Number.isNaN(descuentos)) {
            return null;
        }

        return {
            nombre: payload.nombre.trim(),
            apellido: payload.apellido.trim(),
            rutDni: payload.rutDni.trim(),
            cargo: payload.cargo.trim(),
            salarioBase: salarioBase,
            bono: bono,
            descuentos: descuentos
        };
    }

    function extractErrorMessages(error, fallbackMessage) {
        debugLog("Error capturado", error);
        if (error && error.data && Array.isArray(error.data.details) && error.data.details.length > 0) {
            return error.data.details;
        }
        if (error && error.data && error.data.message) {
            return [error.data.message];
        }
        return [fallbackMessage];
    }

    function clearFieldErrors(form) {
        var fields = ["nombre", "apellido", "rutDni", "cargo", "salarioBase", "bono", "descuentos"];
        fields.forEach(function (fieldName) {
            var input = form[fieldName];
            if (input) {
                input.removeAttribute("aria-invalid");
            }
            var errorNode = document.getElementById("error-" + fieldName);
            if (errorNode) {
                errorNode.textContent = "";
                errorNode.style.display = "none";
            }
        });
    }

    function renderFieldErrors(form, fieldErrors) {
        Object.keys(fieldErrors).forEach(function (fieldName) {
            var messages = fieldErrors[fieldName];
            var input = form[fieldName];
            var errorNode = document.getElementById("error-" + fieldName);
            if (!input || !errorNode) {
                return;
            }

            if (messages && messages.length > 0) {
                input.setAttribute("aria-invalid", "true");
                errorNode.textContent = messages[0];
                errorNode.style.display = "block";
                return;
            }

            input.removeAttribute("aria-invalid");
            errorNode.textContent = "";
            errorNode.style.display = "none";
        });
    }

    function emptyFieldErrors() {
        return {
            nombre: [],
            apellido: [],
            rutDni: [],
            cargo: [],
            salarioBase: [],
            bono: [],
            descuentos: []
        };
    }

    function mapApiErrorsToFieldErrors(messages) {
        var fieldErrors = emptyFieldErrors();
        (messages || []).forEach(function (message) {
            var text = String(message || "");
            if (text.indexOf("RUT/DNI") >= 0) {
                fieldErrors.rutDni.push(text);
                return;
            }
            if (text.indexOf("nombre") >= 0) {
                fieldErrors.nombre.push(text);
                return;
            }
            if (text.indexOf("apellido") >= 0) {
                fieldErrors.apellido.push(text);
                return;
            }
            if (text.indexOf("cargo") >= 0) {
                fieldErrors.cargo.push(text);
                return;
            }
            if (text.indexOf("salario") >= 0) {
                fieldErrors.salarioBase.push(text);
                return;
            }
            if (text.indexOf("bono") >= 0) {
                fieldErrors.bono.push(text);
                return;
            }
            if (text.indexOf("descuentos") >= 0 || text.indexOf("descuento") >= 0) {
                fieldErrors.descuentos.push(text);
            }
        });
        return fieldErrors;
    }

    function bindEvents(context) {
        if (context.form && context.form.rutDni && context.validation && context.validation.formatRutForInput) {
            context.form.rutDni.addEventListener("input", function () {
                var formatted = context.validation.formatRutForInput(context.form.rutDni.value);
                context.form.rutDni.value = formatted;
            });

            context.form.rutDni.addEventListener("blur", function () {
                var formatted = context.validation.formatRutForInput(context.form.rutDni.value);
                context.form.rutDni.value = formatted;
            });
        }

        context.form.addEventListener("submit", function (event) {
            event.preventDefault();

            context.ui.clearMessages(context.formErrors);
            context.ui.clearMessages(context.formSuccess);
            clearFieldErrors(context.form);

            var payload = getFormPayload(context.form);
            debugLog("Payload de formulario", payload);
            var validationResult = context.validation.validateEmpleadoDetailed(payload);
            var validationErrors = validationResult.errors;
            if (validationErrors.length > 0) {
                debugLog("Validacion cliente fallo", validationErrors);
                renderFieldErrors(context.form, validationResult.fieldErrors);
                context.ui.renderMessages(context.formErrors, validationErrors);
                return;
            }

            var apiPayload = toApiPayload(payload);
            if (!apiPayload) {
                context.ui.renderMessages(context.formErrors, ["Hay errores de validacion en valores numericos"]);
                return;
            }
            apiPayload.rutDni = context.validation.normalizeRutForApi(apiPayload.rutDni);
            debugLog("Payload a API", apiPayload);

            context.api.createEmpleado(apiPayload)
                .then(function () {
                    debugLog("Creacion de empleado OK");
                    context.form.reset();
                    context.ui.renderMessages(context.formSuccess, ["Empleado creado correctamente"]);
                    context.ui.showToast(context.toastContainer, "success", "Empleado creado correctamente");
                    if (typeof context.onCreated === "function") {
                        context.onCreated();
                    }
                    context.loadEmpleados();
                })
                .catch(function (error) {
                    debugLog("Creacion de empleado fallo", error);
                    var errors = extractErrorMessages(error, "No se pudo crear el empleado");
                    renderFieldErrors(context.form, mapApiErrorsToFieldErrors(errors));
                    context.ui.renderMessages(context.formErrors, errors);
                    context.ui.showToast(context.toastContainer, "error", errors[0]);
                });
        });

        context.reloadButton.addEventListener("click", function () {
            context.loadEmpleados();
        });
    }

    window.empleadoEvents = {
        bindEvents: bindEvents,
        extractErrorMessages: extractErrorMessages
    };
})(window, document);
