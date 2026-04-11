package cl.previred.desafio.service;

import cl.previred.desafio.dto.EmpleadoRequest;
import cl.previred.desafio.dto.ValidationError;
import cl.previred.desafio.model.Empleado;
import cl.previred.desafio.repository.EmpleadoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final ValidationService validationService;

    public EmpleadoService(EmpleadoRepository empleadoRepository, ValidationService validationService) {
        this.empleadoRepository = empleadoRepository;
        this.validationService = validationService;
    }

    public List<Empleado> getAllEmpleados() {
        return empleadoRepository.findAll();
    }

    public Empleado getEmpleadoById(Long id) {
        return empleadoRepository.findById(id);
    }

    public List<ValidationError> crearEmpleado(EmpleadoRequest request) {
        List<ValidationError> errores = validationService.validate(request);
        if (!errores.isEmpty()) {
            return errores;
        }

        Empleado empleado = new Empleado();
        empleado.setNombre(request.getNombre());
        empleado.setApellido(request.getApellido());
        empleado.setRut(request.getRut());
        empleado.setCargo(request.getCargo());
        empleado.setSalario(request.getSalario());
        empleado.setBono(request.getBono() != null ? request.getBono() : 0.0);
        empleado.setDescuentos(request.getDescuentos() != null ? request.getDescuentos() : 0.0);

        empleadoRepository.save(empleado);
        return errores;
    }

    public boolean eliminarEmpleado(Long id) {
        return empleadoRepository.deleteById(id);
    }
}