function validarRut(rut) {
    rut = rut.replace(".", "").replace("-", "");
    let cuerpo = rut.slice(0, -1);
    let dv = rut.slice(-1).toUpperCase();

    let suma = 0;
    let multiplo = 2;

    for (let i = cuerpo.length - 1; i >= 0; i--) {
        suma += multiplo * Number.parseInt(cuerpo.charAt(i));
        multiplo = multiplo < 7 ? multiplo + 1 : 2;
    }

    let dvEsperado = 11 - (suma % 11);
    let sdv;
    switch(dvEsperado){
        case 11: sdv="0"; break;
        case 10: sdv="K"; break;
        default: sdv=dvEsperado.toString();
    }
    return dv === sdv;
}

function desplegarMensajeError(msg){
    $("#response").css("color", "red");
    $("#response").css("font-weight", "bold");
    $("#response").text(msg);
    $("#response").stop(true, true).fadeIn("300").delay(5000).fadeOut(300);
}

function desplegarMensajeExito(msg){
    $("#response").css("color", "green");
    $("#response").css("font-weight", "bold");
    $("#response").text(msg);
    $("#response").stop(true, true).fadeIn("300").delay(5000).fadeOut(300);
}

function desplegarMensajeEspera(msg){
    $("#response2").css("color", "blue");
    $("#response2").css("font-weight", "bold");
    $("#response2").text(msg);
}

function validarFormulario(){
    const regex = /^\d{7,8}-[0-9Kk]$/;
    const campos = ["nombre", "apellido", "rut", "cargo", "salario"];
    const faltantes = [];
    campos.forEach(c => {
        if($("#" + c).val().trim().length == 0){
            faltantes.push(c);
        }
    });
    if(faltantes.length > 0){
        desplegarMensajeError("Todos los campos son requeridos. Faltan los siguientes campos: " + faltantes.join(","));
        return false;
    }else if(!regex.test($("#rut").val())){
        desplegarMensajeError("El rut ingresado tiene formato invalido");
        return false;
    }else if (!validarRut($("#rut").val())){
        desplegarMensajeError("El numero del rut no concuerda con su digito verificador");
        return false;
    }else if(Number.parseInt($("#salario").val(), 10) < 400000){
        desplegarMensajeError("El salario debe ser mayor o igual que 400000");
        return false;
    }
    return true;
}

function buscarUsuarios(){
    $.ajax({
        method: "GET",
        url: "/api/empleados",
        success: function(data){
            if(data.usuarios !== undefined && data.usuarios.length > 0){
                $("#tabla").css("display", "none");
                $("#resultados").html("");
                desplegarMensajeEspera("Por favor, espere...");
                data.usuarios.forEach(u => {
                    let row="<tr>";
                    row+="<td>" + u.nombre + "</td>";
                    row+="<td>" + u.apellido + "</td>";
                    row+="<td>" + u.rut + "</td>";
                    row+="<td>" + u.cargo + "</td>";
                    row+="<td>" + u.salario + "</td>";
                    row+="<td>" + u.monto + "</td>";
                    row+="<td><a href='#' onclick='iniciarBono(" + u.id + "," + u.salario + ")'>Abonar</a></td>";
                    row+="<td><a href='#' onclick='iniciarDescuento(" + u.id + "," + u.salario + ")'>Descontar</a></td>";
                    row+="<td><a href='#' onclick='eliminarUsuario(" + u.id + ")'>Eliminar</a></td>";
                    row+="</tr>";
                    $("#resultados").append(row);
                });
                $("#response2").text("");
                $("#tabla").css("display", "block");
            }else if(data.usuarios !== undefined){
                desplegarMensajeError("No se encontraron resultados");
            }
        },
        error: function(data){
            desplegarMensajeError(data.responseJSON.msg);
        }
    });
}

function crearUsuario(){
    if(validarFormulario()){
        $.ajax({
            url: "/api/empleados",
            method: "POST",
            contentType: "application/json",
            data: JSON.stringify({
                nombre: $("#nombre").val(),
                apellido: $("#apellido").val(),
                rut: $("#rut").val(),
                cargo: $("#cargo").val(),
                salario: $("#salario").val()
            }),
            success: function(data){
                desplegarMensajeExito(data.msg);
                $("#nombre").val("");
                $("#apellido").val("");
                $("#rut").val("");
                $("#cargo").val("");
                $("#salario").val("400000");
                buscarUsuarios();
            },
            error: function(data){
                console.log(data.responseJSON);
                desplegarMensajeError(data.responseJSON.msg);
            }
        });
    }
}

function eliminarUsuario(id){
    $.ajax({
        method: "DELETE",
        url: "/api/empleados?id=" + id,
        success: function(data){
            desplegarMensajeExito(data.msg);
            buscarUsuarios();
        },
        error: function(data){
            desplegarMensajeError(data.responseJSON.msg);
        }
    });
}

function iniciarBono(id, base){
    $("#idbonificar").val(id);
    $("#base").val(base);
    $("#bono").attr("max", base/2);
    $("#seccion_crear").css("display", "none");
    $("#seccion_bono").css("display", "block");
    $("#seccion_descuento").css("display", "none");
}

function formNuevoUsuario(){
    $("#seccion_crear").css("display", "block");
    $("#seccion_bono").css("display", "none");
    $("#seccion_descuento").css("display", "none");
}

function aplicarBono(){
    const bono=Number.parseInt($("#bono").val(), 10);
    const base=Number.parseInt($("#base").val(), 10);
    if(bono > base / 2){
        desplegarMensajeError("El bono no debe ser mayor a la mitad del salario");
    }else{
        $.ajax({
            method: "PATCH",
            url: "/api/empleados",
            contentType: "application/json",
            data: JSON.stringify({
                id: $("#idbonificar").val(),
                monto: $("#bono").val(),
                tipo: 1
            }),
            success: function(data){
                desplegarMensajeExito(data.msg);
                buscarUsuarios();
            },
            error: function(data){
                desplegarMensajeError(data.responseJSON.msg);
            }
        });
    }
}

function iniciarDescuento(id, base){
    $("#iddescontar").val(id);
    $("#based").val(base);
    $("#seccion_crear").css("display", "none");
    $("#seccion_bono").css("display", "none");
    $("#seccion_descuento").css("display", "block");
}

function agregarOtroDescuento(){
     const numd = Number.parseInt($("#numdescuentos").val(), 10) + 1;
     const id = "descuento" + numd;
     let html = "<div class='campo'>";
     html+="<label for='" + id + "'>Descuento " + numd + "</label>";
     html+="<input type='number' class='num' name='" + id + "' id='" + id + "' min='1' max='9950000' step='1000' value='1000' inputmode='numeric' required>";
     html+="</div>";
     $("#numdescuentos").val(numd);
     $("#descuentos").append(html);
}

function aplicarDescuentos(){
    const numd = $("#numdescuentos").val();
    let sum=0;
    const base = Number.parseInt($("#based").val(), 10);
    for(let i=1; i<=numd; i++){
        if($("#descuento" + i).val() == null || $("#descuento" + i).val().length == 0){
            desplegarMensajeError("Todos los descuentos son obligatorios");
            return false;
        }
        sum+=Number.parseInt($("#descuento" + i).val(), 10);
    }
    if(sum >= base){
        desplegarMensajeError("El total de descuentos no puede ser mayor al salario");
    }else{
        $.ajax({
            method: "PATCH",
            url: "/api/empleados",
            contentType: "application/json",
            data: JSON.stringify({
                id: $("#iddescontar").val(),
                monto: sum,
                tipo: -1
            }),
            success: function(data){
                desplegarMensajeExito(data.msg);
                $("#numdescuentos").val(1);
                let html = "<div class='campo'>";
                html+="<label for='descuento1'>Descuento 1</label>";
                html+="<input type='number' class='num' name='descuento1' id='descuento1' min='1' max='9950000' step='1000' value='1000' inputmode='numeric' required>";
                html+="</div>";
                $("#descuentos").html(html);
                buscarUsuarios();
            },
            error: function(data){
                desplegarMensajeError(data.responseJSON.msg);
            }
        });
    }
}

$(document).ready(function(){
   buscarUsuarios();
   $(".num").on("keypress", function(e){
        if (!/\d/.test(e.key)) {
            e.preventDefault();
        }
   });
});
