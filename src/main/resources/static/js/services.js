const EmpleadoService = {
    baseUrl: '/desafio-legacy/api/empleados',

    async obtenerTodos() {
        const response = await fetch(this.baseUrl);
        if (!response.ok) throw new Error("Error al obtener empleados");
        return await response.json();
    },

    async guardar(datos) {
        return await fetch(this.baseUrl, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(datos)
        });
    },

    async eliminar(id) {
        const response = await fetch(`${this.baseUrl}/${id}`, { method: 'DELETE' });
        return response.ok;
    }
};