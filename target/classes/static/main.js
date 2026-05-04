const tabla = document.getElementById('lista-empleados');
const btnGuardar = document.getElementById('btn-guardar');
const apiMessage = document.getElementById('api-message');

const getVal = (id) => document.getElementById(id).value;

function manejarErrores(responseData) {
    let errorTexto = "";

    if (responseData.message) {
        errorTexto += responseData.message + "\n";
    }

    if (responseData.errors && Object.keys(responseData.errors).length > 0) {
        for (const [campo, mensaje] of Object.entries(responseData.errors)) {
            errorTexto += `${mensaje}. `;

            const input = document.getElementById(campo);
            if (input) input.classList.add('invalid');
        }
    }

    if (errorTexto) {
        apiMessage.innerText = errorTexto;
        apiMessage.style.display = "block";
    }
}

function limpiarEstilos() {
    apiMessage.style.display = "none";
    apiMessage.innerText = "";
    const inputs = document.querySelectorAll('input');
    inputs.forEach(i => {
        i.classList.remove('invalid');
        i.style.borderColor = "#ccc";
    });
}

function limpiarFormulario() {
    const ids = ['name', 'surname', 'fiscalId', 'role', 'salary', 'bonus', 'discounts'];
    ids.forEach(id => document.getElementById(id).value = '');
}

function renderizarTabla() {
    EmpleadosAPI.getAll(
        (empleados) => {
            const lista = Array.isArray(empleados) ? empleados : (empleados.data || []);

            tabla.innerHTML = lista.map(emp => `
                <tr>
                    <td>${emp.fiscalId || ''}</td>
                    <td>${emp.name || ''} ${emp.surname || ''}</td>
                    <td>${emp.role || ''}</td>
                    <td>${emp.salary !== undefined ? emp.salary : '0'}</td>
                    <td>${emp.bonus !== undefined ? emp.bonus : '0'}</td>
                    <td>${emp.discounts !== undefined ? emp.discounts : '0'}</td>
                    <td>
                        <button onclick="borrar('${emp.id}')" style="background:#dc3545; color:white; border:none; padding:4px 8px; border-radius:3px; cursor:pointer;">Eliminar</button>
                    </td>
                </tr>
            `).join('');
        },
        (status, err) => console.error("Error al cargar lista", status, err)
    );
}

function guardar() {
    limpiarEstilos();

    const data = {
        name: getVal('name'),
        surname: getVal('surname'),
        fiscalId: getVal('fiscalId'),
        role: getVal('role'),
        salary: parseFloat(getVal('salary')) || 0,
        bonus: parseFloat(getVal('bonus')) || 0,
        discounts: parseFloat(getVal('discounts')) || 0
    };

    EmpleadosAPI.create(data,
        (response) => {
            if (response.status === "SUCCESS") {
                limpiarFormulario();
                renderizarTabla();
            } else {
                manejarErrores(response);
            }
        },
        (status, response) => {
            manejarErrores(response || { message: "Error de conexión con el servidor" });
        }
    );
}

window.borrar = (id) => {
    if (confirm("¿Eliminar este empleado?")) {
        limpiarEstilos();
        EmpleadosAPI.remove(id,
            () => renderizarTabla(),
            (status, response) => manejarErrores(response)
        );
    }
};

document.addEventListener('DOMContentLoaded', renderizarTabla);
btnGuardar.addEventListener('click', guardar);
