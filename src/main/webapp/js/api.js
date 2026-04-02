const EmpleadoAPI = {
    BASE_URL: '/api/empleados',

    obtenerEmpleados: async function() {
        try {
            const response = await fetch(this.BASE_URL);

            if (!response.ok) {
                throw new Error('Error al obtener empleados');
            }

            return await response.json();
        } catch (error) {
            console.error('Error:', error);
            throw error;
        }
    },

    crearEmpleado: async function(empleadoData) {
        try {
            const response = await fetch(this.BASE_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(empleadoData)
            });

            const data = await response.json();

            if (!response.ok) {
                return {
                    success: false,
                    errors: data.errors || ['Error al crear empleado']
                };
            }

            return {
                success: true,
                data: data
            };
        } catch (error) {
            console.error('Error:', error);
            return {
                success: false,
                errors: ['Error de conexión con el servidor']
            };
        }
    },

    eliminarEmpleado: async function(id) {
        try {
            const response = await fetch(`${this.BASE_URL}/${id}`, {
                method: 'DELETE'
            });

            if (!response.ok) {
                throw new Error('Error al eliminar empleado');
            }

            return true;
        } catch (error) {
            console.error('Error:', error);
            throw error;
        }
    }
};
