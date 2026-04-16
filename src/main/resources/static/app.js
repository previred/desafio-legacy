var URL = '/api/empleados';

function formatMoney(value) {
    return '$' + Number(value || 0).toLocaleString('es-CL');
}

function limpiarRut(rut) {
    return rut.replace(/[^\dkK]/g, '');
}

function formatearRut(rut) {
    let limpio = limpiarRut(rut);

    if (limpio.length < 2) return limpio;

    let cuerpo = limpio.slice(0, -1);
    let dv = limpio.slice(-1).toUpperCase();

    return cuerpo + "-" + dv;
}


document.getElementById("rut").addEventListener("blur", function() {
    if (this.value.trim() !== "") {
        this.value = formatearRut(this.value);
    }
});

function calcularDV(rut) {
    let suma = 0;
    let multiplo = 2;

    for (let i = rut.length - 1; i >= 0; i--) {
        suma += rut[i] * multiplo;
        multiplo = multiplo < 7 ? multiplo + 1 : 2;
    }

    let dv = 11 - (suma % 11);
    if (dv === 11) return '0';
    if (dv === 10) return 'K';
    return dv.toString();
}

function validarRut(rutCompleto) {
    let limpio = limpiarRut(rutCompleto);

    if (limpio.length < 2) return false;

    let cuerpo = limpio.slice(0, -1);
    let dv = limpio.slice(-1).toUpperCase();

    return calcularDV(cuerpo) === dv;
}

document.getElementById("salarioBase").addEventListener("input", function() {
    let valor = this.value.replace(/[^0-9]/g, '');
    if (valor === "") {
        this.value = "";
        return;
    }
    let numero = parseInt(valor, 10).toLocaleString("es-CL");
    this.value = "$" + numero;
});

document.getElementById("bonos").addEventListener("input", function() {
    let valor = this.value.replace(/[^0-9]/g, '');
    if (valor === "") {
        this.value = "";
        return;
    }
    let numero = parseInt(valor, 10).toLocaleString("es-CL");
    this.value = "$" + numero;
});

document.getElementById("descuentos").addEventListener("input", function() {
    let valor = this.value.replace(/[^0-9]/g, '');
    if (valor === "") {
        this.value = "";
        return;
    }
    let numero = parseInt(valor, 10).toLocaleString("es-CL");
    this.value = "$" + numero;
});

function mostrarErrores(errores) {
    var ul = document.getElementById('errores');
    ul.innerHTML = '';
    if (!errores || errores.length === 0) {
        ul.style.display = 'none';
        return;
    }
    errores.forEach(function(err) {
        var li = document.createElement('li');
        li.textContent = err;
        ul.appendChild(li);
    });
    ul.style.display = 'block';
}

function limpiarFormulario() {
    document.getElementById('formEmpleado').reset();
    mostrarErrores([]);
}

function cargarEmpleados() {
    fetch(URL)
        .then(function(res) { return res.json(); })
        .then(function(empleados) {
            var tbody = document.getElementById('tablaEmpleados');
            if (empleados.length === 0) {
                tbody.innerHTML = '<tr><td colspan="9" class="empty-msg">No hay empleados registrados</td></tr>';
                return;
            }
            tbody.innerHTML = empleados.map(function(e) {
                return '<tr>' +
                    '<td>' + e.id + '</td>' +
                    '<td>' + e.nombre + '</td>' +
                    '<td>' + e.apellido + '</td>' +
                    '<td>' + e.rut + '</td>' +
                    '<td>' + e.cargo + '</td>' +
                    '<td class="salary">' + formatMoney(e.salario) + '</td>' +
                    '<td><button class="btn btn-danger" onclick="abrirModal(' + e.id + ')">Eliminar</button></td>' +
                    '</tr>';
            }).join('');
        })
        .catch(function(err) {
            console.error('Error al cargar empleados:', err);
        });
}

function eliminarEmpleado(id) {
    fetch(URL + '/' + id, { method: 'DELETE' })
        .then(function(res) {
            if (!res.ok) {
                return res.json().then(function(err) { throw err; });
            }
            cargarEmpleados();
        })
        .catch(function(err) {
            mostrarErrores([err.mensaje || 'Error al eliminar empleado']);
        });
}

document.getElementById('formEmpleado').addEventListener('submit', function(e) {
    e.preventDefault();
    mostrarErrores([]);
    let valido = true;

    const campos = this.querySelectorAll("input");

    campos.forEach(input => {
        const errorDiv = input.nextElementSibling;

        if (input.value.trim() === "") {
            errorDiv.textContent = "* Este campo es obligatorio";
            input.classList.add("input-error");
            valido = false;
        } else if (input.id === "rut"){
            if (!validarRut(input.value)) {
                errorDiv.textContent = "RUT inválido";
                input.classList.add("input-error");
                valido = false;
            }else {
                errorDiv.textContent = "";
                input.classList.remove("input-error");
            }
        } else {
            errorDiv.textContent = "";
            input.classList.remove("input-error");
        }
    });

    if (valido) {

        var data = {
            nombre: document.getElementById('nombre').value,
            apellido: document.getElementById('apellido').value,
            rut: document.getElementById('rut').value,
            cargo: document.getElementById('cargo').value,
            salario: obtenerNumero('salarioBase'),
            bonos: obtenerNumero('bonos'),
            descuentos: obtenerNumero('descuentos')
        };

        fetch(URL, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        })
            .then(function(res) {
                if (!res.ok) {
                    return res.json().then(function(err) { throw err; });
                }
                return res.json();
            })
            .then(function() {
                limpiarFormulario();
                cargarEmpleados();
            })
            .catch(function(err) {
                if (err.errores) {
                    mostrarErrores(err.errores);
                } else {
                    mostrarErrores([err.mensaje || 'Error al crear empleado']);
                }
            });
    }
});

function obtenerNumero(id) {
    let valor = document.getElementById(id).value;

    return parseFloat(
        valor.replace(/\./g, '').replace(',', '.').replace(/[^0-9.]/g, '')
    ) || 0;
}

let idSeleccionado = null;

function abrirModal(id) {
    idSeleccionado = id;
    document.getElementById("mensaje").textContent =
        "¿Seguro que deseas eliminar el registro " + id + "?";
    document.getElementById("modal").style.display = "block";
}

function cerrarModal() {
    document.getElementById("modal").style.display = "none";
}

function confirmarEliminar() {
    eliminarEmpleado(idSeleccionado);
    cerrarModal();
}

cargarEmpleados();