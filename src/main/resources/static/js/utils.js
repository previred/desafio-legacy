const Utils = {
    /**
     * Formatea un string como RUT chileno (12.345.678-9)
     */
    formatearRut: (rut) => {
        let valor = rut.replace(/[^0-9kK]/g, '');
        if (valor.length <= 1) return valor;
        let cuerpo = valor.slice(0, -1);
        let dv = valor.slice(-1).toUpperCase();
        return cuerpo.replace(/\B(?=(\d{3})+(?!\d))/g, ".") + "-" + dv;
    },

    /**
     * Formatea números a moneda CLP
     */
    formatoMoneda: (valor) => {
        return `$${valor.toLocaleString('es-CL')}`;
    }
};