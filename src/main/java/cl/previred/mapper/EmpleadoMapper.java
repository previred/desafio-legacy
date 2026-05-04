package cl.previred.mapper;

import cl.previred.dto.EmpleadoRequest;
import cl.previred.dto.EmpleadoResponse;
import cl.previred.model.Empleado;
import org.springframework.stereotype.Component;

@Component
public class EmpleadoMapper {

    public Empleado toEntity(EmpleadoRequest request) {
        Empleado empleado = new Empleado();
        empleado.setNombre(request.getNombre());
        empleado.setApellido(request.getApellido());
        empleado.setRutDni(request.getRutDni());
        empleado.setCargo(request.getCargo());
        empleado.setSalario(request.getSalario());
        return empleado;
    }

    public EmpleadoResponse toResponse(Empleado empleado) {
        EmpleadoResponse response = new EmpleadoResponse();
        response.setId(empleado.getId());
        response.setNombre(empleado.getNombre());
        response.setApellido(empleado.getApellido());
        response.setRutDni(empleado.getRutDni());
        response.setCargo(empleado.getCargo());
        response.setSalario(empleado.getSalario());
        return response;
    }
}
