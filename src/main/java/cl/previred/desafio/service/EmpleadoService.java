package cl.previred.desafio.service;

import cl.previred.desafio.dto.EmpleadoRequest;
import cl.previred.desafio.model.Empleado;
import cl.previred.desafio.repository.EmpleadoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    public void crearEmpleado(EmpleadoRequest request) {
        LOG.debug("Creando empleado con RUT: {}", request.getRut());
        validationService.validate(request);

        Empleado empleado = new Empleado();
        empleado.setNombre(request.getNombre());
        empleado.setApellido(request.getApellido());
        empleado.setRut(request.getRut());
        empleado.setCargo(request.getCargo());
        empleado.setSalario(request.getSalario());
        empleado.setBono(request.getBono() != null ? request.getBono() : BigDecimal.ZERO);
        empleado.setDescuentos(request.getDescuentos() != null ? 
            request.getDescuentos() : BigDecimal.ZERO);

        empleadoRepository.save(empleado);
        LOG.info("Empleado creado exitosamente con RUT: {}", request.getRut());
    }

    public boolean eliminarEmpleado(Long id) {
        LOG.debug("Eliminando empleado con id: {}", id);
        return empleadoRepository.deleteById(id);
    }
}
