const EmpleadoValidator = {

    validar: function (data) {

        let errors = false;

        const dniRegex = /^[0-9A-Za-z-]{5,}$/;

        this.clearErrors();

        if (!data.nombre) {
            this.showError("errNombre", "Nombre obligatorio");
            errors = true;
        }

        if (!data.apellido) {
            this.showError("errApellido", "Apellido obligatorio");
            errors = true;
        }

        if (!data.dni || !dniRegex.test(data.dni)) {
            this.showError("errDni", "DNI inválido");
            errors = true;
        }

        if (!data.cargo) {
            this.showError("errCargo", "Cargo obligatorio");
            errors = true;
        }

        if (!data.salario || data.salario < 400000) {
            this.showError("errSalario", "Salario mínimo $400,000");
            errors = true;
        }

        // OPCIONAL: bono
        if (data.bono && data.bono < 0) {
            this.showError("errBono", "El bono no puede ser negativo");
            errors = true;
        }

        //OPCIONAL: descuento
        if (data.descuentos && data.descuentos < 0) {
            this.showError("errDescuentos", "Descuentos inválidos");
            errors = true;
        }

        return !errors;
    },

    showError: function (id, msg) {
        document.getElementById(id).innerText = msg;
    },

    clearErrors: function () {
        document.querySelectorAll(".error").forEach(e => e.innerText = "");
    }
};