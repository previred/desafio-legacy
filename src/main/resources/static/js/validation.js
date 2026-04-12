/**
 * Modulo de validaciones del formulario (frontend).
 * Replica las reglas del backend para dar feedback inmediato al usuario.
 */
var Validation = (function () {
    'use strict';

    var RUT_REGEX = /^\d{1,2}\.\d{3}\.\d{3}-[\dkK]$/;
    var SALARIO_MINIMO = 400000;

    /** Valida todos los campos del formulario. Retorna true si es valido. */
    function validarFormulario() {
        UI.clearFieldErrors();
        var errores = [];

        validarTexto('nombre', 'El nombre es obligatorio.', errores);
        validarTexto('apellido', 'El apellido es obligatorio.', errores);
        validarRut(errores);
        validarTexto('cargo', 'El cargo es obligatorio.', errores);
        validarSalario(errores);
        validarBono(errores);
        validarDescuentos(errores);

        return errores.length === 0;
    }

    /** Valida que un campo de texto no este vacio. */
    function validarTexto(campo, mensaje, errores) {
        if (!Utils.getFieldValue(campo)) {
            UI.setFieldError(campo, mensaje);
            errores.push(campo);
        }
    }

    /** Valida formato y presencia del RUT. */
    function validarRut(errores) {
        var rut = Utils.getFieldValue('rut');
        if (!rut) {
            UI.setFieldError('rut', 'El RUT es obligatorio.');
            errores.push('rut');
        } else if (!RUT_REGEX.test(rut)) {
            UI.setFieldError('rut', 'Formato invalido. Use XX.XXX.XXX-X.');
            errores.push('rut');
        }
    }

    /** Valida presencia y monto minimo del salario. */
    function validarSalario(errores) {
        var raw = document.getElementById('salario').value;
        var salario = parseInt(raw) || 0;

        if (!raw) {
            UI.setFieldError('salario', 'El salario es obligatorio.');
            errores.push('salario');
        } else if (salario < SALARIO_MINIMO) {
            UI.setFieldError('salario', 'El salario minimo es $400,000.');
            errores.push('salario');
        }
    }

    /** Valida que el bono no sea negativo ni supere el 50% del salario. */
    function validarBono(errores) {
        var salario = Utils.getNumericValue('salario');
        var bono = Utils.getNumericValue('bono');

        if (bono < 0) {
            UI.setFieldError('bono', 'El bono no puede ser negativo.');
            errores.push('bono');
        } else if (salario > 0 && bono > salario / 2) {
            UI.setFieldError('bono', 'El bono no puede superar el 50% del salario.');
            errores.push('bono');
        }
    }

    /** Valida que los descuentos no sean negativos ni superen el salario. */
    function validarDescuentos(errores) {
        var salario = Utils.getNumericValue('salario');
        var descuentos = Utils.getNumericValue('descuentos');

        if (descuentos < 0) {
            UI.setFieldError('descuentos', 'Los descuentos no pueden ser negativos.');
            errores.push('descuentos');
        } else if (descuentos > salario) {
            UI.setFieldError('descuentos', 'Los descuentos no pueden superar el salario.');
            errores.push('descuentos');
        }
    }

    return {
        validarFormulario: validarFormulario
    };
})();
