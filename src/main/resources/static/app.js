const form = document.getElementById('empleado-form');
const errorsContainer = document.getElementById('form-errors');
const feedbackContainer = document.getElementById('table-feedback');
const empleadosBody = document.getElementById('empleados-body');
const reloadButton = document.getElementById('reload-button');

const rutDniPattern = /^(\d{7,8}-?[\dkK]|\d{7,10}|[A-Za-z0-9]{7,12})$/;

document.addEventListener('DOMContentLoaded', () => {
    loadEmpleados();
});

form.addEventListener('submit', async (event) => {
    event.preventDefault();
    clearMessages();

    const payload = buildPayload();
    const validationErrors = validatePayload(payload);
    if (validationErrors.length > 0) {
        renderErrors(validationErrors);
        return;
    }

    try {
        const response = await fetch('/api/empleados', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            const data = await response.json();
            renderErrors(extractErrorMessages(data, 'No fue posible crear el empleado'));
            return;
        }

        form.reset();
        setFeedback('Empleado creado correctamente.');
        await loadEmpleados();
    } catch (error) {
        renderErrors(['No fue posible conectar con el servidor']);
    }
});

reloadButton.addEventListener('click', () => {
    clearMessages();
    loadEmpleados();
});

async function loadEmpleados() {
    empleadosBody.innerHTML = '<tr><td colspan="9">Cargando...</td></tr>';

    try {
        const response = await fetch('/api/empleados');
        if (!response.ok) {
            throw new Error('Error al cargar empleados');
        }

        const empleados = await response.json();
        renderEmpleados(empleados);
    } catch (error) {
        empleadosBody.innerHTML = '<tr><td colspan="9">No fue posible cargar los empleados.</td></tr>';
    }
}

async function deleteEmpleado(id) {
    clearMessages();

    try {
        const response = await fetch(`/api/empleados?id=${id}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            const data = await response.json();
            setFeedback(extractFirstErrorMessage(data, 'No fue posible eliminar el empleado'));
            return;
        }

        setFeedback('Empleado eliminado correctamente.');
        await loadEmpleados();
    } catch (error) {
        setFeedback('No fue posible conectar con el servidor');
    }
}

function buildPayload() {
    const formData = new FormData(form);
    return {
        nombre: (formData.get('nombre') || '').toString().trim(),
        apellido: (formData.get('apellido') || '').toString().trim(),
        rutDni: (formData.get('rutDni') || '').toString().trim(),
        cargo: (formData.get('cargo') || '').toString().trim(),
        salarioBase: parseNumber(formData.get('salarioBase')),
        bono: parseNumber(formData.get('bono')),
        descuentos: parseNumber(formData.get('descuentos'))
    };
}

function validatePayload(payload) {
    const errors = [];

    if (!payload.nombre) {
        errors.push('El nombre es obligatorio');
    }
    if (!payload.apellido) {
        errors.push('El apellido es obligatorio');
    }
    if (!payload.rutDni) {
        errors.push('El RUT/DNI es obligatorio');
    } else if (!rutDniPattern.test(payload.rutDni)) {
        errors.push('El formato del RUT/DNI no es válido');
    }
    if (!payload.cargo) {
        errors.push('El cargo es obligatorio');
    }
    if (payload.salarioBase === null) {
        errors.push('El salario base es obligatorio');
    } else if (payload.salarioBase < 400000) {
        errors.push('El salario base no puede ser menor a 400000');
    }
    if (payload.bono === null) {
        errors.push('El bono es obligatorio');
    } else if (payload.bono < 0) {
        errors.push('El bono no puede ser negativo');
    }
    if (payload.descuentos === null) {
        errors.push('Los descuentos son obligatorios');
    } else if (payload.descuentos < 0) {
        errors.push('Los descuentos no pueden ser negativos');
    }
    if (payload.salarioBase !== null && payload.bono !== null && payload.bono > payload.salarioBase * 0.5) {
        errors.push('El bono no puede superar el 50% del salario base');
    }
    if (payload.salarioBase !== null && payload.descuentos !== null && payload.descuentos > payload.salarioBase) {
        errors.push('El total de descuentos no puede ser mayor al salario base');
    }

    return errors;
}

function renderErrors(errors) {
    errorsContainer.innerHTML = `<ul>${errors.map((error) => `<li>${error}</li>`).join('')}</ul>`;
}

function extractErrorMessages(data, fallbackMessage) {
    if (!data || !Array.isArray(data.errors) || !data.errors.length) {
        return [fallbackMessage];
    }

    const messages = data.errors
        .map((error) => (typeof error === 'string' ? error : error.message))
        .filter(Boolean);

    return messages.length ? messages : [fallbackMessage];
}

function extractFirstErrorMessage(data, fallbackMessage) {
    return extractErrorMessages(data, fallbackMessage)[0];
}

function renderEmpleados(empleados) {
    if (!empleados.length) {
        empleadosBody.innerHTML = '<tr><td colspan="9">No hay empleados registrados.</td></tr>';
        return;
    }

    empleadosBody.innerHTML = empleados.map((empleado) => `
        <tr>
            <td data-label="ID">${empleado.id}</td>
            <td data-label="Nombre">${empleado.nombre}</td>
            <td data-label="Apellido">${empleado.apellido}</td>
            <td data-label="RUT/DNI">${empleado.rutDni}</td>
            <td data-label="Cargo">${empleado.cargo}</td>
            <td data-label="Salario base">${formatCurrency(empleado.salarioBase)}</td>
            <td data-label="Bono">${formatCurrency(empleado.bono)}</td>
            <td data-label="Descuentos">${formatCurrency(empleado.descuentos)}</td>
            <td data-label="Acción">
                <button type="button" class="delete-button" data-id="${empleado.id}">
                    Eliminar
                </button>
            </td>
        </tr>
    `).join('');

    document.querySelectorAll('.delete-button').forEach((button) => {
        button.addEventListener('click', () => deleteEmpleado(button.dataset.id));
    });
}

function setFeedback(message) {
    feedbackContainer.textContent = message;
}

function clearMessages() {
    errorsContainer.innerHTML = '';
    feedbackContainer.textContent = '';
}

function parseNumber(value) {
    if (value === null || value === undefined || value === '') {
        return null;
    }

    const parsed = Number(value);
    return Number.isNaN(parsed) ? null : parsed;
}

function formatCurrency(value) {
    const number = Number(value);
    return Number.isNaN(number)
        ? value
        : new Intl.NumberFormat('es-CL', {
            style: 'currency',
            currency: 'CLP',
            maximumFractionDigits: 0
        }).format(number);
}
