
document.addEventListener('DOMContentLoaded', () => {
	const API_URL = '/api/empleados';
	function validaRut(rut) {
		console.log(rut);
	    if (!/^[0-9]+-[0-9kK]{1}$/.test(rut)) return false;    
	    var tmp = rut.split('-');
	    var digv = tmp[1];
	    var rut = tmp[0];    
	    if (digv == 'K') digv = 'k';
	    return (calculateDV(rut) == digv);
	}

	function calculateDV(rut) {
	    var M = 0, S = 1;
	    for (; rut; rut = Math.floor(rut / 10))
	        S = (S + rut % 10 * (9 - M++ % 6)) % 11;
	    return S ? S - 1 + '' : 'k';
	}
	window.cargarEmpleados = function () {
	           fetch(API_URL)
	               .then(response => response.json())
	               .then(data => {
	                   const tabla = document.getElementById('tablaEmpleados');
	                   tabla.innerHTML = ''; // Limpiar tabla
	                   data.forEach(emp => {
	                       tabla.innerHTML += `
	                           <tr>
								<td>${emp.id}</td>
								<td>${emp.name}</td>
								<td>${emp.last_name}</td>
								<td>${emp.rut}</td>
								<td>${emp.charget}</td>
								<td>${emp.salary}</td>
	                               <td>
	                                   <button class="btn-del" onclick="eliminarEmpleado(${emp.id})">Eliminar</button>
	                               </td>
	                           </tr>
	                       `;
	                   });
	               })
	               .catch(error => console.error('Error al cargar:', error));
	}
		   
		   window.agregarEmpleado= function (){
		   			name= document.getElementById('nombre').value
		    			if (name===''){
		   				document.getElementById('messages').innerHTML="El nombre es obligatorio."
		   				return
		   			} 
		   			lastName= document.getElementById('apellido').value
		   			if (lastName===''){
		   				document.getElementById('messages').innerHTML="El Apellido es obligatorio."
		   				return
		   			} 
		   			rut= document.getElementById('rut').value
		   			if (rut==='' || !validaRut(rut)){
		   				document.getElementById('messages').innerHTML="Este debe ser sin punto y con guion."
		   				return
		   			} 

		   			charget= document.getElementById('cargo').value
		   			if (charget===''){
		   				document.getElementById('messages').innerHTML="El cargo es obligatorio."
		   				return
		   			} 
		   			salary= document.getElementById('salario').value
		   			if (salary===''){
		   				document.getElementById('messages').innerHTML="El salario es obligatorio."
		   				return
		   			} 
		               const nuevoEmp = {
		                   name: name,
		                   lastName: lastName,
		                   rut: rut,
		                   charget: charget,
		                   salary: salary
		               };

		               fetch(API_URL, {
		                   method: 'POST',
		                   headers: { 'Content-Type': 'application/json' },
		                   body: JSON.stringify(nuevoEmp)
		               })
		               .then(async response => {
		                   if (response.ok) {
		                       cargarEmpleados();
		                       limpiarFormulario();
		                   }else{
		   					json =await response.json()
		   					document.getElementById('messages').innerHTML=json.messages;
		   				}
		   				
		               })
		               .catch(error => console.log('Error al agregar:', error));
		           }

		           window.eliminarEmpleado =function(id) {
		               if (confirm('¿Seguro que desea eliminar este empleado?')) {
		                   fetch(`${API_URL}/${id}`, {
		                       method: 'DELETE'
		                   })
		                   .then(response => {
		                       if (response.ok) cargarEmpleados();
		                   })
		                   .catch(error => console.error('Error al eliminar:', error));
		               }
		           }

		           window.limpiarFormulario= function() {
		               document.querySelectorAll('input').forEach(input => input.value = '');
		           }	

				   window.onload = cargarEmpleados;
});
