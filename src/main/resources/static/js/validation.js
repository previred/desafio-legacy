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
        validateEmpleadoDetailed: validateEmpleadoDetailed
    };
})(window);
