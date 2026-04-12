const EmpleadosList = {

    load: async function () {

        const data = await EmpleadosAPI.getAll();

        const tbody = document.getElementById("tablaEmpleados");

   function formatearUSDCL(valor) {
    return new Intl.NumberFormat('es-CL', {
        style: 'currency',
        currency: 'USD'
    })
    .format(valor || 0)
    .replace('US$', '$');
}

        tbody.innerHTML = "";

        data.forEach(emp => {
            tbody.innerHTML += `
                <tr>
                    <td>${emp.nombre}</td>
                    <td>${emp.apellido}</td>
                    <td>${emp.dni}</td>
                    <td>${emp.cargo}</td>
                    <td>${formatearUSDCL(emp.salarioBase)}</td>
                    <td>${formatearUSDCL(emp.bono || 0)}</td>
                    <td>${formatearUSDCL(emp.descuentos || 0)}</td>
                      <td>${formatearUSDCL(emp.salarioNeto)}</td>
                    <td>
                        <button class="btn-delete" onclick="EmpleadosDelete.remove(${emp.id})">
                            Eliminar
                        </button>
                    </td>
                </tr>
            `;
        });
    }
};