package cl.previred.desafio.service;

import cl.previred.desafio.dto.EmpleadoRequest;
import cl.previred.desafio.dto.ValidationError;
import cl.previred.desafio.repository.EmpleadoRepository;
import cl.previred.desafio.util.RutValidator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ValidationService {

    private static final double SALARIO_MINIMO = 400000.0;
    private static final double BONO_MAXIMO_PORCENTAJE = 0.50;

    private final EmpleadoRepository empleadoRepository;

    public ValidationService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    public List<ValidationError> validate(EmpleadoRequest request) {
        List<ValidationError> errores = new ArrayList<>();

        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            errores.add(new ValidationError("nombre", "El nombre es requerido"));
        }

        if (request.getApellido() == null || request.getApellido().trim().isEmpty()) {
            errores.add(new ValidationError("apellido", "El apellido es requerido"));
        }

        if (request.getRut() == null || request.getRut().trim().isEmpty()) {
            errores.add(new ValidationError("rut", "El RUT es requerido"));
        } else if (!RutValidator.isValid(request.getRut())) {
            errores.add(new ValidationError("rut", "RUT invalido (digito verificador incorrecto)"));
        } else if (empleadoRepository.existsByRut(request.getRut())) {
            errores.add(new ValidationError("rut", "RUT ya existe"));
        }

        if (request.getCargo() == null || request.getCargo().trim().isEmpty()) {
            errores.add(new ValidationError("cargo", "El cargo es requerido"));
        }

        if (request.getSalario() == null) {
            errores.add(new ValidationError("salario", "El salario es requerido"));
        } else if (request.getSalario() < SALARIO_MINIMO) {
            errores.add(new ValidationError("salario", "Salario debe ser >= $400,000"));
        }

        if (request.getBono() != null && request.getSalario() != null) {
            double bonoMaximo = request.getSalario() * BONO_MAXIMO_PORCENTAJE;
            if (request.getBono() > bonoMaximo) {
                errores.add(new ValidationError("bono", "Bono no puede superar 50% del salario"));
            }
        }

        if (request.getDescuentos() != null && request.getSalario() != null) {
            if (request.getDescuentos() > request.getSalario()) {
                errores.add(new ValidationError("descuentos", "Descuentos no pueden superar el salario"));
            }
        }

        return errores;
    }
}