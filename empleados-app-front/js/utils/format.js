const FormatUtils = {

    formatoCLP(valor) {
        if (!valor) return "";

        return "$" + parseInt(valor, 10)
            .toLocaleString("es-CL");
    },

    limpiarNumero(valor) {
        return valor.replace(/\D/g, "");
    },

    limpiarParaEnviar(valor) {
        return parseInt(valor.replace(/\D/g, ""), 10) || 0;
    },

    formatearInput(id) {

        const input = document.getElementById(id);

        input.addEventListener("input", (e) => {

            let valor = this.limpiarNumero(e.target.value);

            if (valor === "") {
                e.target.value = "";
                return;
            }

            e.target.value = this.formatoCLP(valor);
        });
    }
};