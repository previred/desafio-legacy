const API = "http://localhost:8080/api/empleados";

const EmpleadosAPI = {

    getAll: async function () {
        const res = await fetch(API);
        return await res.json();
    },

    create: async function (empleado) {

        const res = await fetch(API, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(empleado)
        });

        const data = await res.json();

        if (!res.ok) {
            throw data;
        }

        return data;
    },

    delete: async function (id) {

        const res = await fetch(`${API}?id=${id}`, {
            method: "DELETE"
        });

        if (!res.ok) {
            throw { message: "Error al eliminar" };
        }
    }
};