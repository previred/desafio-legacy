package com.previred.desafiolegacy.service;

import com.previred.desafiolegacy.model.Empleado;
import com.previred.desafiolegacy.repository.EmpleadoRepository;
import com.previred.desafiolegacy.validator.EmpleadoValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmpleadoService {

    private static final Logger logger = LoggerFactory.getLogger(EmpleadoService.class);

    private final EmpleadoRepository empleadoRepository;
    private final EmpleadoValidator empleadoValidator;

    public EmpleadoService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
        this.empleadoValidator = new EmpleadoValidator(empleadoRepository);
    }

    public List<Empleado> obtenerTodos() {
        logger.info("Obteniendo lista de empleados");
        return empleadoRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Empleado::getNombre))
                .collect(Collectors.toList());
    }

    public Empleado crear(Empleado empleado) {
        logger.info("Creando empleado con RUT: {}", empleado.getRut());
        empleadoValidator.validar(empleado);
        return empleadoRepository.save(empleado);
    }

    public boolean eliminar(Long id) {
        logger.info("Eliminando empleado con id: {}", id);
        Optional<Empleado> empleado = empleadoRepository.findById(id);
        if (empleado.isEmpty()) {
            logger.warn("Empleado con id {} no encontrado", id);
            return false;
        }
        return empleadoRepository.deleteById(id);
    }

}
