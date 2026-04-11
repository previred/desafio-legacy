package cl.previred.desafio.service;

import cl.previred.desafio.dto.EmpleadoRequest;
import cl.previred.desafio.dto.ValidationError;
import cl.previred.desafio.exception.ValidationExceptionList;
import cl.previred.desafio.model.Empleado;
import cl.previred.desafio.repository.EmpleadoRepository;
import cl.previred.desafio.util.RutValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ValidationService {

    private static final BigDecimal SALARIO_MINIMO = new BigDecimal("400000");
    private static final BigDecimal BONO_MAXIMO_PORCENTAJE = new BigDecimal("0.50");

    private final EmpleadoRepository empleadoRepository;

    public ValidationService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    public void validate(EmpleadoRequest request) {
        List<ValidationError> errores = new ArrayList<>();

        validateRequiredString(request.getNombre(), "nombre", "El nombre es requerido", errores);
        validateRequiredString(request.getApellido(), "apellido", "El apellido es requerido", errores);
        validateRut(request, errores);
        validateRequiredString(request.getCargo(), "cargo", "El cargo es requerido", errores);
        validateSalario(request, errores);
        validateBono(request, errores);
        validateDescuentos(request, errores);

        if (!errores.isEmpty()) {
            throw new ValidationExceptionList(errores);
        }
    }

    private void validateRequiredString(
            String value,
            String fieldName,
            String message,
            List<ValidationError> errores) {
        if (value == null || value.trim().isEmpty()) {
            errores.add(new ValidationError(fieldName, message));
        }
    }

    private void validateRut(EmpleadoRequest request, List<ValidationError> errores) {
        String rut = request.getRut();
        if (rut == null || rut.trim().isEmpty()) {
            errores.add(new ValidationError("rut", "El RUT es requerido"));
            return;
        }
        if (!RutValidator.isValid(rut)) {
            errores.add(new ValidationError("rut", "RUT invalido (digito verificador incorrecto)"));
            return;
        }
        if (empleadoRepository.existsByRut(rut)) {
            errores.add(new ValidationError("rut", "RUT ya existe"));
        }
    }

    private void validateSalario(EmpleadoRequest request, List<ValidationError> errores) {
        if (request.getSalario() == null) {
            errores.add(new ValidationError("salario", "El salario es requerido"));
            return;
        }
        if (request.getSalario().compareTo(SALARIO_MINIMO) < 0) {
            errores.add(new ValidationError("salario", "Salario debe ser >= $400,000"));
        }
    }

    private void validateBono(EmpleadoRequest request, List<ValidationError> errores) {
        if (request.getBono() == null || request.getSalario() == null) {
            return;
        }
        BigDecimal bonoMaximo = request.getSalario().multiply(BONO_MAXIMO_PORCENTAJE);
        if (request.getBono().compareTo(bonoMaximo) > 0) {
            errores.add(new ValidationError("bono", "Bono no puede superar 50% del salario"));
        }
    }

    private void validateDescuentos(EmpleadoRequest request, List<ValidationError> errores) {
        if (request.getDescuentos() == null || request.getSalario() == null) {
            return;
        }
        if (request.getDescuentos().compareTo(request.getSalario()) > 0) {
            errores.add(new ValidationError("descuentos", "Descuentos no pueden superar el salario"));
        }
    }
}
