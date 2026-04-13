function validarFormularioEmpleado(empleado) {
    const errores = [];

    if (!empleado.nombre) {
        errores.push('El nombre es obligatorio.');
    }

    if (!empleado.apellido) {
        errores.push('El apellido es obligatorio.');
    }

    if (!empleado.rutDni) {
        errores.push('El RUT/DNI es obligatorio.');
    } else if (!validarFormatoRutDni(empleado.rutDni)) {
        errores.push('El formato del RUT/DNI no es válido.');
    }

    if (!empleado.cargo) {
        errores.push('El cargo es obligatorio.');
    }

    if (empleado.salario === null) {
        errores.push('El salario es obligatorio.');
    } else if (empleado.salario < 400000) {
        errores.push('El salario no puede ser menor a 400000.');
    }

    return errores;
}

function validarFormatoRutDni(rutDni) {
    if (!rutDni) {
        return false;
    }

    const rutNormalizado = normalizarRutDni(rutDni);

    if (!/^\d{7,8}[0-9K]$/.test(rutNormalizado)) {
        return false;
    }

    const cuerpo = rutNormalizado.slice(0, -1);
    const dvIngresado = rutNormalizado.slice(-1);

    return dvIngresado === calcularDigitoVerificador(cuerpo);
}

function normalizarRutDni(rutDni) {
    return rutDni.trim().replaceAll('.', '').replaceAll('-', '').toUpperCase();
}

function calcularDigitoVerificador(cuerpo) {
    let suma = 0;
    let multiplicador = 2;

    for (let i = cuerpo.length - 1; i >= 0; i--) {
        suma += Number(cuerpo.charAt(i)) * multiplicador;
        multiplicador = multiplicador === 7 ? 2 : multiplicador + 1;
    }

    const resultado = 11 - (suma % 11);

    if (resultado === 11) {
        return '0';
    }

    if (resultado === 10) {
        return 'K';
    }

    return String(resultado);
}