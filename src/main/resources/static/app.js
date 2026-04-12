(function () {
    'use strict';

    var API_URL = '/api/empleados';
    var SALARIO_MINIMO = 400000;

    var form = document.getElementById('empleado-form');
    var tbody = document.querySelector('#empleados-table tbody');
    var formErrors = document.getElementById('form-errors');
    var listMessage = document.getElementById('list-message');

    document.addEventListener('DOMContentLoaded', loadEmpleados);
    form.addEventListener('submit', onSubmit);

    function loadEmpleados() {
        listMessage.textContent = 'Cargando...';
        fetch(API_URL)
            .then(function (r) { return r.json().then(function (b) { return { ok: r.ok, body: b }; }); })
            .then(function (res) {
                if (!res.ok) {
                    listMessage.textContent = 'No se pudo cargar la lista de empleados.';
                    return;
                }
                renderTable(res.body);
                listMessage.textContent = res.body.length === 0 ? 'No hay empleados registrados.' : '';
            })
            .catch(function () {
                listMessage.textContent = 'Error de red al cargar empleados.';
            });
    }

    function renderTable(empleados) {
        tbody.innerHTML = '';
        empleados.forEach(function (e) {
            var tr = document.createElement('tr');
            tr.appendChild(td(e.id));
            tr.appendChild(td(e.nombre));
            tr.appendChild(td(e.apellido));
            tr.appendChild(td(e.rut));
            tr.appendChild(td(e.cargo));
            tr.appendChild(td(formatMoney(e.salarioBase)));
            tr.appendChild(td(formatMoney(e.bonos)));
            tr.appendChild(td(formatMoney(e.descuentos)));

            var actionTd = document.createElement('td');
            var btn = document.createElement('button');
            btn.textContent = 'Eliminar';
            btn.className = 'danger';
            btn.addEventListener('click', function () { deleteEmpleado(e.id); });
            actionTd.appendChild(btn);
            tr.appendChild(actionTd);

            tbody.appendChild(tr);
        });
    }

    function td(value) {
        var cell = document.createElement('td');
        cell.textContent = value == null ? '' : String(value);
        return cell;
    }

    function formatMoney(value) {
        if (value == null) return '';
        var n = Number(value);
        if (isNaN(n)) return String(value);
        return '$' + n.toLocaleString('es-CL');
    }

    function onSubmit(ev) {
        ev.preventDefault();
        clearErrors();

        var data = readForm();
        var clientErrors = validateClient(data);
        if (Object.keys(clientErrors).length > 0) {
            showFieldErrors(clientErrors);
            return;
        }

        fetch(API_URL, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        })
            .then(function (r) { return r.json().then(function (b) { return { ok: r.ok, status: r.status, body: b }; }); })
            .then(function (res) {
                if (res.ok) {
                    form.reset();
                    loadEmpleados();
                    return;
                }
                var msgs = (res.body && res.body.errors) || ['Error desconocido.'];
                showGlobalErrors(msgs);
            })
            .catch(function () {
                showGlobalErrors(['Error de red al agregar empleado.']);
            });
    }

    function deleteEmpleado(id) {
        fetch(API_URL + '?id=' + encodeURIComponent(id), { method: 'DELETE' })
            .then(function (r) {
                if (r.status === 204) {
                    loadEmpleados();
                    return;
                }
                return r.json().then(function (b) {
                    var msgs = (b && b.errors) || ['No se pudo eliminar el empleado.'];
                    listMessage.textContent = msgs.join(' ');
                });
            })
            .catch(function () {
                listMessage.textContent = 'Error de red al eliminar.';
            });
    }

    function readForm() {
        var fd = new FormData(form);
        return {
            nombre: (fd.get('nombre') || '').trim(),
            apellido: (fd.get('apellido') || '').trim(),
            rut: (fd.get('rut') || '').trim(),
            cargo: (fd.get('cargo') || '').trim(),
            salarioBase: toNumber(fd.get('salarioBase')),
            bonos: toNumber(fd.get('bonos')),
            descuentos: toNumber(fd.get('descuentos'))
        };
    }

    function toNumber(v) {
        if (v === null || v === undefined || v === '') return null;
        var n = Number(v);
        return isNaN(n) ? null : n;
    }

    function validateClient(d) {
        var errs = {};
        if (!d.nombre) errs.nombre = 'Campo requerido.';
        if (!d.apellido) errs.apellido = 'Campo requerido.';
        if (!d.cargo) errs.cargo = 'Campo requerido.';
        if (!d.rut) {
            errs.rut = 'Campo requerido.';
        } else if (!isValidRutFormat(d.rut)) {
            errs.rut = 'Formato de RUT invalido (ej: 12345678-9).';
        }
        if (d.salarioBase == null) {
            errs.salarioBase = 'Campo requerido.';
        } else if (d.salarioBase < SALARIO_MINIMO) {
            errs.salarioBase = 'Debe ser mayor o igual a $' + SALARIO_MINIMO.toLocaleString('es-CL') + '.';
        }
        return errs;
    }

    function isValidRutFormat(rut) {
        var clean = rut.replace(/\./g, '').replace(/-/g, '').toUpperCase();
        return /^[0-9]{7,8}[0-9K]$/.test(clean);
    }

    function clearErrors() {
        formErrors.innerHTML = '';
        var spans = document.querySelectorAll('.field-error');
        spans.forEach(function (s) { s.textContent = ''; });
        var inputs = form.querySelectorAll('input');
        inputs.forEach(function (i) { i.classList.remove('invalid'); });
    }

    function showFieldErrors(errs) {
        Object.keys(errs).forEach(function (field) {
            var span = document.querySelector('.field-error[data-for="' + field + '"]');
            if (span) span.textContent = errs[field];
            var input = form.querySelector('[name="' + field + '"]');
            if (input) input.classList.add('invalid');
        });
    }

    function showGlobalErrors(messages) {
        var ul = document.createElement('ul');
        messages.forEach(function (m) {
            var li = document.createElement('li');
            li.textContent = m;
            ul.appendChild(li);
        });
        formErrors.innerHTML = '';
        formErrors.appendChild(ul);
    }
})();
