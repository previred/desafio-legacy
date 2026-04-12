package com.desafio.legacy.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.desafio.legacy.dto.EmpleadoRequest;
import com.desafio.legacy.dto.EmpleadoResponse;
import com.desafio.legacy.exception.BusinessValidationException;
import com.desafio.legacy.exception.ResourceNotFoundException;
import com.desafio.legacy.model.Empleado;
import com.desafio.legacy.repository.EmpleadoRepository;

@Service
public class EmpleadoServiceImpl implements EmpleadoService {

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private final EmpleadoRepository empleadoRepository;
    private final EmpleadoValidator empleadoValidator;

    public EmpleadoServiceImpl(EmpleadoRepository empleadoRepository, EmpleadoValidator empleadoValidator) {
        this.empleadoRepository = empleadoRepository;
        this.empleadoValidator = empleadoValidator;
    }

    @Override
    public List<EmpleadoResponse> obtenerEmpleados() {
        return obtenerEmpleados(null, null);
    }

    @Override
    public List<EmpleadoResponse> obtenerEmpleados(String term, String cargo) {
        return empleadoRepository.findByFilters(term, cargo).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    public EmpleadoResponse crearEmpleado(EmpleadoRequest request) {
        List<String> details = empleadoValidator.validate(request);
        if (!details.isEmpty()) {
            throw new BusinessValidationException("VALIDATION_ERROR", "Hay errores de validacion", details);
        }

        Empleado empleado = toEmpleado(request);
        String normalizedRutDni = empleado.getRutDni();
        if (empleadoRepository.existsByRutDni(normalizedRutDni)) {
            List<String> duplicateDetails = new ArrayList<String>();
            duplicateDetails.add("El RUT/DNI ya existe");
            throw new BusinessValidationException("VALIDATION_ERROR", "Hay errores de validacion", duplicateDetails);
        }

        Empleado created = empleadoRepository.save(empleado);
        return toResponse(created);
    }

    @Override
    public void eliminarEmpleado(Long id) {
        if (id == null || id <= 0) {
            List<String> details = new ArrayList<String>();
            details.add("El id debe ser un numero positivo");
            throw new BusinessValidationException("VALIDATION_ERROR", "Hay errores de validacion", details);
        }

        boolean deleted = empleadoRepository.deleteById(id);
        if (!deleted) {
            throw new ResourceNotFoundException("EMPLEADO_NOT_FOUND", "No existe empleado para el id " + id);
        }
    }

    private BigDecimal normalizeAmount(BigDecimal amount) {
        return amount == null ? ZERO : amount;
    }

    private Empleado toEmpleado(EmpleadoRequest request) {
        Empleado empleado = new Empleado();
        empleado.setNombre(request.getNombre().trim());
        empleado.setApellido(request.getApellido().trim());
        empleado.setRutDni(request.getRutDni().trim());
        empleado.setCargo(request.getCargo().trim());
        empleado.setSalarioBase(request.getSalarioBase());
        empleado.setBono(normalizeAmount(request.getBono()));
        empleado.setDescuentos(normalizeAmount(request.getDescuentos()));
        return empleado;
    }

    private EmpleadoResponse toResponse(Empleado empleado) {
        return new EmpleadoResponse(
            empleado.getId(),
            empleado.getNombre(),
            empleado.getApellido(),
            empleado.getRutDni(),
            empleado.getCargo(),
            empleado.getSalarioBase(),
            empleado.getBono(),
            empleado.getDescuentos()
        );
    }
}
