package cl.previred.desafio.service;

import cl.previred.desafio.dto.EmpleadoRequest;
import cl.previred.desafio.dto.ValidationError;
import cl.previred.desafio.model.Empleado;
import cl.previred.desafio.repository.EmpleadoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpleadoService {

    private static final Logger LOG = LoggerFactory.getLogger(EmpleadoService.class);

    private final EmpleadoRepository empleadoRepository;
    private final ValidationService validationService;

    public EmpleadoService(EmpleadoRepository empleadoRepository, ValidationService validationService) {
        this.empleadoRepository = empleadoRepository;
        this.validationService = validationService;
    }

    public List<Empleado> getAllEmpleados() {
        LOG.debug("Obteniendo todos los empleados");
        return empleadoRepository.findAll();
    }

    public Empleado getEmpleadoById(Long id) {
        LOG.debug("Obteniendo empleado por id: {}", id);
        return empleadoRepository.findById(id);
    }

    public List<ValidationError> crearEmpleado(EmpleadoRequest request) {
        LOG.debug("Creando empleado con RUT: {}", request.getRut());
        List<ValidationError> errores = validationService.validate(request);
        if (!errores.isEmpty()) {
            LOG.warn("Validacion fallida para RUT {}: {} errores", request.getRut(), errores.size());
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
        LOG.info("Empleado creado exitosamente con RUT: {}", request.getRut());
        return errores;
    }

    public boolean eliminarEmpleado(Long id) {
        LOG.debug("Eliminando empleado con id: {}", id);
        return empleadoRepository.deleteById(id);
    }
}