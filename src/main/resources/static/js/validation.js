(function (window) {
    "use strict";

    var MIN_SALARIO_BASE = 400000;
    var RUT_DNI_REGEX = /^[0-9A-Za-z.-]{5,20}$/;
    var BONUS_FACTOR_MAX = 0.5;

    function toNumber(value) {
        if (value === null || value === undefined || value === "") {
            return 0;
        }
        return Number(value);
    }

    function isBlank(value) {
        return !value || !String(value).trim();
    }

    function normalizeRut(rut) {
        return String(rut || "")
            .replace(/\s+/g, "")
            .replace(/\./g, "")
            .replace(/-/g, "")
            .trim()
            .toUpperCase();
    }

    function extractRutParts(rut) {
        var normalized = normalizeRut(rut);
        if (normalized.length < 2) {
            return null;
        }

        var body = normalized.slice(0, -1);
        var dv = normalized.slice(-1);
        if (!/^\d+$/.test(body)) {
            return null;
        }
        if (!/^[0-9K]$/.test(dv)) {
            return null;
        }

        return {
            body: body,
            dv: dv
        };
    }

    function formatRutBody(body) {
        var digits = String(body || "").replace(/\D/g, "");
        var formatted = "";
        var chunkCount = 0;
        for (var i = digits.length - 1; i >= 0; i -= 1) {
            formatted = digits.charAt(i) + formatted;
            chunkCount += 1;
            if (chunkCount === 3 && i > 0) {
                formatted = "." + formatted;
                chunkCount = 0;
            }
        }
        return formatted;
    }

    function formatRutForInput(rut) {
        var normalized = normalizeRut(rut);
        if (!normalized) {
            return "";
        }
        if (normalized.length === 1) {
            return normalized;
        }

        var parts = extractRutParts(normalized);
        if (!parts) {
            return normalized;
        }

        return formatRutBody(parts.body) + "-" + parts.dv;
    }

    function normalizeRutForApi(rut) {
        var parts = extractRutParts(rut);
        if (!parts) {
            return String(rut || "").trim();
        }
        return parts.body + "-" + parts.dv;
    }

    function calculateRutDv(body) {
        var factor = 2;
        var sum = 0;
        for (var i = body.length - 1; i >= 0; i -= 1) {
            sum += Number(body.charAt(i)) * factor;
            factor = factor === 7 ? 2 : factor + 1;
        }
        var remainder = 11 - (sum % 11);
        if (remainder === 11) {
            return "0";
        }
        if (remainder === 10) {
            return "K";
        }
        return String(remainder);
    }

    function isValidChileanRut(rut) {
        var parts = extractRutParts(rut);
        if (!parts || parts.body.length < 7 || parts.body.length > 8) {
            return false;
        }

        return calculateRutDv(parts.body) === parts.dv;
    }

    function validateEmpleado(payload) {
        return validateEmpleadoDetailed(payload).errors;
    }

    function validateEmpleadoDetailed(payload) {
        var errors = [];
        var fieldErrors = {
            nombre: [],
            apellido: [],
            rutDni: [],
            cargo: [],
            salarioBase: [],
            bono: [],
            descuentos: []
        };

        function addError(field, message) {
            errors.push(message);
            if (fieldErrors[field]) {
                fieldErrors[field].push(message);
            }
        }

        if (isBlank(payload.nombre)) {
            addError("nombre", "El nombre es obligatorio");
        }
        if (isBlank(payload.apellido)) {
            addError("apellido", "El apellido es obligatorio");
        }
        if (isBlank(payload.cargo)) {
            addError("cargo", "El cargo es obligatorio");
        }
        if (isBlank(payload.rutDni)) {
            addError("rutDni", "El RUT/DNI es obligatorio");
        } else if (!RUT_DNI_REGEX.test(payload.rutDni.trim())) {
            addError("rutDni", "El formato de RUT/DNI es invalido");
        } else if (!isValidChileanRut(payload.rutDni)) {
            addError("rutDni", "El RUT ingresado no es valido");
        }

        var salarioBase = toNumber(payload.salarioBase);
        var bono = toNumber(payload.bono);
        var descuentos = toNumber(payload.descuentos);

        if (!payload.salarioBase && payload.salarioBase !== 0) {
            addError("salarioBase", "El salario base es obligatorio");
            return {
                errors: errors,
                fieldErrors: fieldErrors
            };
        }
        if (Number.isNaN(salarioBase)) {
            addError("salarioBase", "El salario base debe ser numerico");
            return {
                errors: errors,
                fieldErrors: fieldErrors
            };
        }
        if (salarioBase < MIN_SALARIO_BASE) {
            addError("salarioBase", "El salario base debe ser mayor o igual a 400000");
        }

        if (payload.bono !== null && payload.bono !== undefined && payload.bono !== "" && Number.isNaN(bono)) {
            addError("bono", "El bono debe ser numerico");
        }
        if (!Number.isNaN(bono) && bono < 0) {
            addError("bono", "El bono no puede ser negativo");
        }
        if (!Number.isNaN(bono) && !Number.isNaN(salarioBase) && bono > salarioBase * BONUS_FACTOR_MAX) {
            addError("bono", "El bono no puede superar el 50% del salario base");
        }

        if (
            payload.descuentos !== null
            && payload.descuentos !== undefined
            && payload.descuentos !== ""
            && Number.isNaN(descuentos)
        ) {
            addError("descuentos", "Los descuentos deben ser numericos");
        }
        if (!Number.isNaN(descuentos) && descuentos < 0) {
            addError("descuentos", "Los descuentos no pueden ser negativos");
        }
        if (!Number.isNaN(descuentos) && !Number.isNaN(salarioBase) && descuentos > salarioBase) {
            addError("descuentos", "El total de descuentos no puede ser mayor al salario base");
        }

        return {
            errors: errors,
            fieldErrors: fieldErrors
        };
    }

    window.empleadoValidation = {
        validateEmpleado: validateEmpleado,
        validateEmpleadoDetailed: validateEmpleadoDetailed,
        formatRutForInput: formatRutForInput,
        normalizeRutForApi: normalizeRutForApi
    };
})(window);
