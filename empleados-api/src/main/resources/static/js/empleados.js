(function () {
    'use strict';

    var API = '/api/empleados';
    var SALARIO_MIN = 400000;
    var MSG_LISTA_VACIA =
        'No hay empleados registrados. Puede agregar uno con el formulario superior o volver a intentar más tarde.';

    var form = document.getElementById('form-empleado');
    var tbody = document.getElementById('tbody-empleados');
    var estadoCarga = document.getElementById('estado-carga');
    var mensajeVacio = document.getElementById('mensaje-vacio');
    var wrapTabla = document.getElementById('wrap-tabla-empleados');
    var btnListar = document.getElementById('btn-listar');
    var errCliente = document.getElementById('errores-cliente');
    var errServidor = document.getElementById('errores-servidor');

    function mostrarErrores(contenedor, mensajes) {
        if (!mensajes || mensajes.length === 0) {
            contenedor.classList.add('oculto');
            contenedor.innerHTML = '';
            return;
        }
        contenedor.classList.remove('oculto');
        var ul = document.createElement('ul');
        mensajes.forEach(function (m) {
            var li = document.createElement('li');
            li.textContent = m;
            ul.appendChild(li);
        });
        contenedor.innerHTML = '';
        contenedor.appendChild(ul);
    }

    function limpiarErrores() {
        mostrarErrores(errCliente, []);
        mostrarErrores(errServidor, []);
    }

    /**
     * Validación de RUT chileno (misma lógica conceptual que el backend).
     */
    function rutNormalizado(valor) {
        if (!valor) {
            return '';
        }
        var t = String(valor).trim().replace(/\./g, '').replace(/-/g, '');
        if (t.length < 2) {
            return '';
        }
        var dv = t.charAt(t.length - 1).toUpperCase();
        var cuerpo = t.slice(0, -1);
        return cuerpo + '-' + dv;
    }

    function digitoVerificador(rutNum) {
        var m = 0;
        var s = 1;
        var t = rutNum;
        for (; t !== 0; t = Math.floor(t / 10)) {
            s = (s + (t % 10) * (9 - (m++ % 6))) % 11;
        }
        return String.fromCharCode(s !== 0 ? s + 47 : 75);
    }

    function rutEsValido(valor) {
        var n = rutNormalizado(valor);
        if (!/^\d{7,8}-[\dK]$/.test(n)) {
            return false;
        }
        var parts = n.split('-');
        var cuerpo = parseInt(parts[0], 10);
        var dvEsperado = digitoVerificador(cuerpo);
        return dvEsperado === parts[1];
    }

    function validarFormulario() {
        var msgs = [];
        var nombre = document.getElementById('nombre').value.trim();
        var apellido = document.getElementById('apellido').value.trim();
        var rut = document.getElementById('rutDni').value.trim();
        var cargo = document.getElementById('cargo').value.trim();
        var salStr = document.getElementById('salarioBase').value;
        var bonoStr = document.getElementById('bono').value || '0';
        var descStr = document.getElementById('descuentos').value || '0';

        if (!nombre) {
            msgs.push('El nombre es obligatorio.');
        }
        if (!apellido) {
            msgs.push('El apellido es obligatorio.');
        }
        if (!rut) {
            msgs.push('El RUT es obligatorio.');
        } else if (!rutEsValido(rut)) {
            msgs.push('El RUT chileno no es válido (revisar dígito verificador).');
        }
        if (!cargo) {
            msgs.push('El cargo es obligatorio.');
        }
        var salario = parseInt(salStr, 10);
        if (salStr === '' || isNaN(salario)) {
            msgs.push('El salario base es obligatorio.');
        } else if (salario < SALARIO_MIN) {
            msgs.push('El salario base no puede ser menor a $400.000.');
        }
        var bono = parseInt(bonoStr, 10) || 0;
        var desc = parseInt(descStr, 10) || 0;
        if (!isNaN(salario) && salario >= SALARIO_MIN) {
            var maxBono = Math.round(salario * 0.5);
            if (bono > maxBono) {
                msgs.push('Los bonos no pueden superar el 50% del salario base.');
            }
            if (desc > salario) {
                msgs.push('El total de descuentos no puede ser mayor al salario base.');
            }
        }
        return msgs;
    }

    function formatoMoneda(n) {
        if (n == null) {
            return '—';
        }
        return '$' + Number(n).toLocaleString('es-CL');
    }

    function pintarFilas(empleados) {
        tbody.innerHTML = '';
        if (!empleados || empleados.length === 0) {
            mensajeVacio.textContent = MSG_LISTA_VACIA;
            mensajeVacio.classList.remove('oculto');
            wrapTabla.classList.add('oculto');
            return;
        }
        mensajeVacio.classList.add('oculto');
        wrapTabla.classList.remove('oculto');
        empleados.forEach(function (e) {
            var tr = document.createElement('tr');
            tr.innerHTML =
                '<td>' + e.id + '</td>' +
                '<td>' + escapeHtml(e.nombre + ' ' + e.apellido) + '</td>' +
                '<td>' + escapeHtml(e.rutDni) + '</td>' +
                '<td>' + escapeHtml(e.cargo) + '</td>' +
                '<td class="num">' + formatoMoneda(e.salarioBase) + '</td>' +
                '<td class="num">' + formatoMoneda(e.bono) + '</td>' +
                '<td class="num">' + formatoMoneda(e.descuentos) + '</td>' +
                '<td><button type="button" class="btn peligro" data-id="' + e.id + '">Eliminar</button></td>';
            tbody.appendChild(tr);
        });
        tbody.querySelectorAll('button[data-id]').forEach(function (btn) {
            btn.addEventListener('click', function () {
                var id = btn.getAttribute('data-id');
                eliminarEmpleado(id);
            });
        });
    }

    function escapeHtml(s) {
        var d = document.createElement('div');
        d.textContent = s;
        return d.innerHTML;
    }

    function cargarLista() {
        estadoCarga.textContent = 'Cargando…';
        estadoCarga.classList.remove('oculto');
        mensajeVacio.classList.add('oculto');
        btnListar.disabled = true;
        fetch(API, { method: 'GET', headers: { Accept: 'application/json' } })
            .then(function (res) {
                if (!res.ok) {
                    throw new Error('Error al cargar la lista');
                }
                return res.json();
            })
            .then(function (data) {
                estadoCarga.classList.add('oculto');
                btnListar.disabled = false;
                pintarFilas(data);
            })
            .catch(function () {
                estadoCarga.textContent = 'No se pudo cargar el listado.';
                btnListar.disabled = false;
                if (!tbody.querySelector('tr')) {
                    mensajeVacio.textContent =
                        'No se pudo obtener el listado. Compruebe la conexión con el servidor y pulse «Actualizar listado».';
                    mensajeVacio.classList.remove('oculto');
                    wrapTabla.classList.add('oculto');
                }
            });
    }

    btnListar.addEventListener('click', function () {
        cargarLista();
    });

    function eliminarEmpleado(id) {
        limpiarErrores();
        fetch(API + '/' + encodeURIComponent(id), {
            method: 'DELETE',
            headers: { Accept: 'application/json' }
        })
            .then(function (res) {
                if (res.status === 204) {
                    cargarLista();
                    return;
                }
                return res.json().then(function (body) {
                    var msg = body.mensaje ? [body.mensaje] : ['No se pudo eliminar.'];
                    mostrarErrores(errServidor, msg);
                });
            })
            .catch(function () {
                mostrarErrores(errServidor, ['Error de red al eliminar.']);
            });
    }

    form.addEventListener('submit', function (ev) {
        ev.preventDefault();
        limpiarErrores();
        var validacion = validarFormulario();
        if (validacion.length > 0) {
            mostrarErrores(errCliente, validacion);
            return;
        }

        var payload = {
            nombre: document.getElementById('nombre').value.trim(),
            apellido: document.getElementById('apellido').value.trim(),
            rutDni: document.getElementById('rutDni').value.trim(),
            cargo: document.getElementById('cargo').value.trim(),
            salarioBase: parseInt(document.getElementById('salarioBase').value, 10),
            bono: parseInt(document.getElementById('bono').value || '0', 10),
            descuentos: parseInt(document.getElementById('descuentos').value || '0', 10)
        };

        fetch(API, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json; charset=UTF-8',
                Accept: 'application/json'
            },
            body: JSON.stringify(payload)
        })
            .then(function (res) {
                return res.json().then(function (body) {
                    return { status: res.status, body: body };
                });
            })
            .then(function (r) {
                if (r.status === 201) {
                    form.reset();
                    document.getElementById('bono').value = '0';
                    document.getElementById('descuentos').value = '0';
                    cargarLista();
                    return;
                }
                if (r.status === 400 && r.body.errores) {
                    var msgs = r.body.errores.map(function (err) {
                        return (err.campo ? '[' + err.campo + '] ' : '') + err.mensaje;
                    });
                    mostrarErrores(errServidor, msgs);
                    return;
                }
                var m = r.body.mensaje ? [r.body.mensaje] : ['Error al guardar.'];
                mostrarErrores(errServidor, m);
            })
            .catch(function () {
                mostrarErrores(errServidor, ['Error de red al enviar el formulario.']);
            });
    });

    cargarLista();
})();
