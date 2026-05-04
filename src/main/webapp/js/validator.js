const Validator = {
    validarRut: function(rut) {
        if (!rut || rut.trim() === '') {
            return { valido: false, mensaje: 'El RUT es obligatorio' };
        }

        const cleanRut = rut.replace(/[^0-9kK]/g, '');

        if (cleanRut.length < 2) {
            return { valido: false, mensaje: 'El RUT no es válido' };
        }

        const rutNumber = cleanRut.substring(0, cleanRut.length - 1);
        const verifierDigit = cleanRut.substring(cleanRut.length - 1).toUpperCase();

        const expectedVerifier = this.calcularDigitoVerificador(parseInt(rutNumber));

        if (verifierDigit !== expectedVerifier) {
            return { valido: false, mensaje: 'El RUT no es válido (dígito verificador incorrecto)' };
        }

        return { valido: true, mensaje: '' };
    },

    calcularDigitoVerificador: function(rut) {
        let sum = 0;
        let multiplier = 2;

        while (rut > 0) {
            sum += (rut % 10) * multiplier;
            rut = Math.floor(rut / 10);
            multiplier = multiplier === 7 ? 2 : multiplier + 1;
        }

        const remainder = 11 - (sum % 11);

        if (remainder === 11) {
            return '0';
        } else if (remainder === 10) {
            return 'K';
        } else {
            return remainder.toString();
        }
    },

    formatearRut: function(rut) {
        const cleanRut = rut.replace(/[^0-9kK]/g, '');

        if (cleanRut.length < 2) {
            return cleanRut;
        }

        const rutNumber = cleanRut.substring(0, cleanRut.length - 1);
        const verifierDigit = cleanRut.substring(cleanRut.length - 1).toUpperCase();

        let formatted = '';
        let count = 0;

        for (let i = rutNumber.length - 1; i >= 0; i--) {
            if (count === 3) {
                formatted = '.' + formatted;
                count = 0;
            }
            formatted = rutNumber.charAt(i) + formatted;
            count++;
        }

        return formatted + '-' + verifierDigit;
    },

    validarSalarioMinimo: function(salario) {
        const SALARIO_MINIMO = 400000;
        const SALARIO_MAXIMO = 100000000;

        if (!salario || salario < SALARIO_MINIMO) {
            return { valido: false, mensaje: 'El salario base debe ser al menos $400.000' };
        }

        if (salario > SALARIO_MAXIMO) {
            return { valido: false, mensaje: 'El salario base no puede superar $100.000.000' };
        }

        return { valido: true, mensaje: '' };
    },

    validarBonos: function(salarioBase, bonos) {
        if (!bonos || bonos === 0) {
            return { valido: true, mensaje: '' };
        }

        if (bonos < 0) {
            return { valido: false, mensaje: 'Los bonos no pueden ser negativos' };
        }

        if (!salarioBase) {
            return { valido: true, mensaje: '' };
        }

        const maxBonos = salarioBase * 0.5;

        if (bonos > maxBonos) {
            return { valido: false, mensaje: 'Los bonos no pueden superar el 50% del salario base' };
        }

        return { valido: true, mensaje: '' };
    },

    validarDescuentos: function(salarioBase, descuentos) {
        if (!descuentos || descuentos === 0) {
            return { valido: true, mensaje: '' };
        }

        if (descuentos < 0) {
            return { valido: false, mensaje: 'Los descuentos no pueden ser negativos' };
        }

        if (!salarioBase) {
            return { valido: true, mensaje: '' };
        }

        if (descuentos > salarioBase) {
            return { valido: false, mensaje: 'Los descuentos no pueden superar el salario base' };
        }

        return { valido: true, mensaje: '' };
    },

    mostrarError: function(inputId, mensaje) {
        const input = document.getElementById(inputId);
        const errorSpan = document.getElementById('error-' + inputId);

        if (mensaje) {
            input.classList.add('invalid');
            errorSpan.textContent = mensaje;
        } else {
            input.classList.remove('invalid');
            errorSpan.textContent = '';
        }
    },

    limpiarErrores: function() {
        const inputs = document.querySelectorAll('input');
        inputs.forEach(input => {
            input.classList.remove('invalid');
        });

        const errorSpans = document.querySelectorAll('.error-message');
        errorSpans.forEach(span => {
            span.textContent = '';
        });

        const errorBox = document.getElementById('formErrors');
        if (errorBox) {
            errorBox.style.display = 'none';
            errorBox.innerHTML = '';
        }
    }
};
