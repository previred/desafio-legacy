const EmpleadosDelete = {

    remove: async function (id) {

        if (!confirm("¿Eliminar empleado?")) return;

        try {
            await EmpleadosAPI.delete(id);
            EmpleadosList.load();
        } catch (e) {
            alert("Error al eliminar");
        }
    }
};