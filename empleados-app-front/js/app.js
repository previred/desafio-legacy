window.onload = function () {

    EmpleadosList.load();
    EmpleadosCreate.init();

    // FORMATO MONEDA
    FormatUtils.formatearInput("salario");
    FormatUtils.formatearInput("bono");
    FormatUtils.formatearInput("descuentos");
    
};