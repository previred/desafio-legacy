package com.previred.empleados.service.impl;

import com.previred.empleados.dao.EmpleadoDAO;
import com.previred.empleados.dto.EmpleadoDTO;
import com.previred.empleados.dto.EmpleadoRequestDTO;
import com.previred.empleados.exception.DuplicateRutException;
import com.previred.empleados.exception.ValidationException;
import com.previred.empleados.model.Empleado;
import com.previred.empleados.service.EmpleadoService;
import com.previred.empleados.validator.EmpleadoValidator;
import com.previred.empleados.validator.RutValidator;
import com.previred.empleados.validator.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpleadoServiceImpl implements EmpleadoService {

    private static final Logger logger = LoggerFactory.getLogger(EmpleadoServiceImpl.class);

    private final EmpleadoDAO empleadoDAO;
    private final EmpleadoValidator validator;

    public EmpleadoServiceImpl(EmpleadoDAO empleadoDAO) {
        this.empleadoDAO = empleadoDAO;
        this.validator = new EmpleadoValidator();
    }

    @Override
    public List<EmpleadoDTO> getAllEmpleados() {
        logger.debug("Obteniendo todos los empleados");
        List<Empleado> empleados = empleadoDAO.findAll();
        return empleados.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EmpleadoDTO createEmpleado(EmpleadoRequestDTO request) {
        logger.info("Creando empleado: {}", request.getRut());

        ValidationResult validationResult = validator.validate(request);
        if (!validationResult.isValid()) {
            logger.warn("Validación fallida para RUT: {}", request.getRut());
            throw new ValidationException(validationResult.getErrors());
        }

        String normalizedRut = RutValidator.normalizeRut(request.getRut());

        if (empleadoDAO.existsByRut(normalizedRut)) {
            logger.warn("Intento de crear empleado con RUT duplicado: {}", normalizedRut);
            throw new DuplicateRutException(normalizedRut);
        }

        Empleado empleado = convertToEntity(request);
        empleado.setRut(normalizedRut);

        Empleado savedEmpleado = empleadoDAO.save(empleado);
        logger.info("Empleado creado exitosamente con ID: {}", savedEmpleado.getId());

        return convertToDTO(savedEmpleado);
    }

    @Override
    public void deleteEmpleado(Long id) {
        logger.info("Eliminando empleado con ID: {}", id);
        empleadoDAO.deleteById(id);
    }

    private EmpleadoDTO convertToDTO(Empleado empleado) {
        BigDecimal salarioTotal = empleado.calcularSalarioTotal();

        return new EmpleadoDTO(
                empleado.getId(),
                empleado.getNombre(),
                empleado.getApellido(),
                empleado.getRut(),
                empleado.getCargo(),
                empleado.getSalarioBase(),
                empleado.getBonos(),
                empleado.getDescuentos(),
                salarioTotal
        );
    }

    private Empleado convertToEntity(EmpleadoRequestDTO request) {
        BigDecimal bonos = request.getBonos() != null ? request.getBonos() : BigDecimal.ZERO;
        BigDecimal descuentos = request.getDescuentos() != null ? request.getDescuentos() : BigDecimal.ZERO;

        return new Empleado(
                request.getNombre().trim(),
                request.getApellido().trim(),
                request.getRut(),
                request.getCargo().trim(),
                request.getSalarioBase(),
                bonos,
                descuentos
        );
    }
}
