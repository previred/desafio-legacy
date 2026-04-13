const form = document.getElementById('employeeForm');
const tableBody = document.getElementById('employeeTableBody');
const errorBox = document.getElementById('errorBox');
const successBox = document.getElementById('successBox');

function hideMessages() {
	errorBox.style.display = 'none';
	successBox.style.display = 'none';
}

function showErrors(messages) {
	errorBox.innerHTML = messages.map(m => `<div>${m}</div>`).join('');
	errorBox.style.display = 'block';
}

function showSuccess(msg) {
	successBox.innerHTML = msg;
	successBox.style.display = 'block';
}

function isValidRut(rut) {
	return /^[0-9]{7,8}-[0-9kK]{1}$/.test(rut);
}

function validateForm(data) {
	const errors = [];

	if (!data.name || !data.name.trim()) {
		errors.push('Nombre obligatorio');
	}

	if (!data.lastName || !data.lastName.trim()) {
		errors.push('Apellido obligatorio');
	}

	if (!data.documentNumber || !data.documentNumber.trim()) {
		errors.push('RUT/DNI obligatorio');
	} else if (!isValidRut(data.documentNumber)) {
		errors.push('RUT inválido');
	}

	if (!data.position || !data.position.trim()) {
		errors.push('Cargo obligatorio');
	}

	if (!data.compensation.baseSalary || !data.compensation.baseSalary.trim()) {
		errors.push('Salario base obligatorio');
	} else if (Number(data.compensation.baseSalary) < 400000) {
		errors.push('Salario mínimo 400000');
	}

	if (!data.compensation.bonus || !data.compensation.bonus.trim()) {
		errors.push('Bono obligatorio');
	}

	if (!data.compensation.discounts || !data.compensation.discounts.trim()) {
		errors.push('Descuentos obligatorios');
	}

	return errors;
}

async function loadEmployees() {
	const res = await fetch('/api/empleados');
	const data = await res.json();

	tableBody.innerHTML = '';

	data.forEach(e => {
		const row = document.createElement('tr');

		row.innerHTML = `
            <td>${e.id}</td>
            <td>${e.name}</td>
            <td>${e.lastName}</td>
            <td>${e.documentNumber}</td>
            <td>${e.position}</td>
            <td>${e.compensation.baseSalary}</td>
            <td>${e.compensation.bonus}</td>
            <td>${e.compensation.discounts}</td>
            <td><button class="delete-button" onclick="deleteEmployee(${e.id})">Eliminar</button></td>
        `;

		tableBody.appendChild(row);
	});
}

form.addEventListener('submit', async (e) => {
	e.preventDefault();
	hideMessages();

	const nameValue = document.getElementById('name').value;
	const lastNameValue = document.getElementById('lastName').value;
	const documentNumberValue = document.getElementById('documentNumber').value;
	const positionValue = document.getElementById('position').value;
	const baseSalaryValue = document.getElementById('baseSalary').value;
	const bonusValue = document.getElementById('bonus').value;
	const discountsValue = document.getElementById('discounts').value;

	const data = {
		name: nameValue,
		lastName: lastNameValue,
		documentNumber: documentNumberValue,
		position: positionValue,
		compensation: {
			baseSalary: baseSalaryValue,
			bonus: bonusValue,
			discounts: discountsValue
		}
	};

	const errors = validateForm(data);
	if (errors.length) {
		showErrors(errors);
		return;
	}

	const payload = {
		name: nameValue,
		lastName: lastNameValue,
		documentNumber: documentNumberValue,
		position: positionValue,
		compensation: {
			baseSalary: Number(baseSalaryValue),
			bonus: Number(bonusValue),
			discounts: Number(discountsValue)
		}
	};

	const res = await fetch('/api/empleados', {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify(payload)
	});

	if (!res.ok) {
		const err = await res.json();

		if (err) {
			showErrors(err.errors.map(e => e.message));
		} else {
			showErrors(["Ocurrió un error al guardar el empleado"]);
		}

		return;
	}

	form.reset();
	hideMessages();

	showSuccess("Empleado creado");
	loadEmployees();
});

async function deleteEmployee(id) {
	if (!confirm("¿Eliminar empleado?")) return;

	try {
		const response = await fetch(`/api/empleados?id=${id}`, {
			method: 'DELETE'
		});

		if (!response.ok) {
			hideMessages();
			const err = await response.json();
			showErrors(err.errors.map(e => e.message));
			loadEmployees();
			return;
		}

		hideMessages();
		showSuccess("Empleado eliminado correctamente");
		await loadEmployees();

	} catch (error) {
		console.error(error);
		hideMessages();
		showErrors(["No fue posible eliminar el empleado"]);
	}
}

document.getElementById('resetBtn').addEventListener('click', () => {
	form.reset();
	hideMessages();
});

loadEmployees();