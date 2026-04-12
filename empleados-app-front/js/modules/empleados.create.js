const MessageUtils = {

    timeout: null,

    show: function (texto, tipo = "success") {
        const msg = document.getElementById("msg");
        msg.innerHTML = `<div class="alert alert-${tipo}">${texto}</div>`;

        this.resetTimer();
    },

    showList: function (errores = []) {
        const msg = document.getElementById("msg");
        msg.innerHTML = errores
            .map(err => `<div class="alert alert-error">${err}</div>`)
            .join("");

        this.resetTimer();
    },

    resetTimer: function () {
        if (this.timeout) {
            clearTimeout(this.timeout);
        }

        this.timeout = setTimeout(() => {
            this.clear();
        }, 6000);
    },

    clear: function () {
        document.getElementById("msg").innerHTML = "";
    }
};

const EmpleadosCreate = {

    init: function () {
        document.getElementById("btnAdd")
            .addEventListener("click", this.create);
    },
    

    create: async function () {

        const empleado = {
            nombre: document.getElementById("nombre").value,
            apellido: document.getElementById("apellido").value,
            dni: document.getElementById("dni").value,
            cargo: document.getElementById("cargo").value,
            salario: FormatUtils.limpiarParaEnviar(
             document.getElementById("salario").value
         ),

             bono: FormatUtils.limpiarParaEnviar(
            document.getElementById("bono").value
         ),

            descuentos: FormatUtils.limpiarParaEnviar(
         document.getElementById("descuentos").value
    )
        };

        if (!EmpleadoValidator.validar(empleado)) return;

        try {
            await EmpleadosAPI.create(empleado);

             MessageUtils.show("Empleado creado correctamente", "success");

            EmpleadosCreate.clearForm();
            EmpleadosList.load();

        } catch (e) {

    const msg = document.getElementById("msg");
    msg.innerHTML = ""; // limpiar

  MessageUtils.clear();

if (e.errors && Array.isArray(e.errors)) {
    MessageUtils.showList(e.errors);
} else if (e.message) {
    MessageUtils.show(e.message, "error");
} else {
    MessageUtils.show("Error inesperado", "error");
}
}
    },

    clearForm: function () {
        document.querySelectorAll("input").forEach(i => i.value = "");
    }
};