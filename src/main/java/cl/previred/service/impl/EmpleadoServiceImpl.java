package cl.previred.service.impl;

import cl.previred.dto.EmpleadoRequest;
import cl.previred.dto.EmpleadoResponse;
import cl.previred.exception.AppException;
import cl.previred.exception.ResourceNotFoundException;
import cl.previred.exception.ValidationException;
import cl.previred.mapper.EmpleadoMapper;
import cl.previred.model.Empleado;
import cl.previred.repository.EmpleadoRepository;
import cl.previred.service.EmpleadoService;
import cl.previred.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpleadoServiceImpl implements EmpleadoService {

    private static final Logger log = LoggerFactory.getLogger(EmpleadoServiceImpl.class);
    private final EmpleadoRepository empleadoRepository;
    private final EmpleadoMapper empleadoMapper;

    public EmpleadoServiceImpl(EmpleadoRepository empleadoRepository, EmpleadoMapper empleadoMapper) {
        this.empleadoRepository = empleadoRepository;
        this.empleadoMapper = empleadoMapper;
    }

    @Override
    public List<EmpleadoResponse> listar() {
        log.info("Listando empleados");
        return empleadoRepository.findAll().stream().map(empleadoMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public EmpleadoResponse crear(EmpleadoRequest request) {
        List<String> errors = ValidationUtils.validateEmpleadoRequest(request);
        if (!errors.isEmpty()) throw new ValidationException("Error de validación", errors);
        if (empleadoRepository.findByRutDni(request.getRutDni()).isPresent()) throw new AppException("Ya existe un empleado con ese RUT/DNI");
        Empleado saved = empleadoRepository.save(empleadoMapper.toEntity(request));
        return empleadoMapper.toResponse(saved);
    }

    @Override
    public void eliminar(Long id) {
        if (!empleadoRepository.findById(id).isPresent()) throw new ResourceNotFoundException("Empleado no encontrado");
        empleadoRepository.deleteById(id);
    }
}
