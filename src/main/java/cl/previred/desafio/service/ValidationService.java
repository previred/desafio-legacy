package cl.previred.desafio.service;

import cl.previred.desafio.dto.EmpleadoRequest;
import cl.previred.desafio.dto.ValidationError;
import cl.previred.desafio.exception.ValidationExceptionList;
import cl.previred.desafio.repository.EmpleadoRepositoryPort;
import cl.previred.desafio.util.RutValidator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio especializado en la validacion de datos de empleados.
 *
 * <p>Implementa todas las reglas de validacion de negocio para empleados,
 * incluyendo:</p>
 * <ul>
 *   <li>Validacion de campos requeridos (nombre, apellido, RUT, cargo, salario)</li>
 *   <li>Validacion de formato de RUT chileno</li>
 *   <li>Validacion de rango de salario minimo ($400,000)</li>
 *   <li>Validacion de porcentaje maximo de bono (50% del salario)</li>
 *   <li>Validacion de que descuentos no superen el salario</li>
 *   <li>Validacion de unicidad de RUT</li>
 * </ul>
 *
 * <p>Todas las validaciones se ejecutan en conjunto y los errores se retornan
 * como una lista para dar feedback completo al usuario.</p>
 *
 * @see EmpleadoRequest
 * @see ValidationExceptionList
 * @see cl.previred.desafio.util.RutValidator
 * @since 1.0
 */
@Service
public class ValidationService implements EmpleadoValidator {

    /** Salario minimo legal en Chile. */
    private static final BigDecimal SALARIO_MINIMO = new BigDecimal("400000");

    /** Porcentaje maximo permitido para bonos respecto al salario. */
    private static final BigDecimal BONO_MAXIMO_PORCENTAJE = new BigDecimal("0.50");

    /** Repositorio para verificar existencia de RUT. */
    private final EmpleadoRepositoryPort empleadoRepository;

    /**
     * Constructor con dependencia inyectada.
     *
     * @param empleadoRepository repositorio para verificar duplicados
     */
    public ValidationService(EmpleadoRepositoryPort empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    /**
     * Valida un request de empleado aplicando todas las reglas de negocio.
     *
     * <p>Si alguna validacion falla, se lanza {@link ValidationExceptionList}
     * contendo todas los errores encontrados para dar feedback completo.</p>
     *
     * @param request el DTO de empleado a validar
     * @throws ValidationExceptionList si al menos una validacion falla
     */
    @Override
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

    /**
     * Valida que un campo de texto no sea null ni vacio.
     *
     * @param value    valor a validar
     * @param fieldName nombre del campo para el mensaje de error
     * @param message  mensaje de error a agregar si falla la validacion
     * @param errores  lista de errores donde agregar si hay falla
     */
    private void validateRequiredString(
            String value,
            String fieldName,
            String message,
            List<ValidationError> errores) {
        if (value == null || value.trim().isEmpty()) {
            errores.add(new ValidationError(fieldName, message));
        }
    }

    /**
     * Valida el RUT del empleado: formato, digito verificador y unicidad.
     *
     * @param request request conteniendo el RUT
     * @param errores lista de errores donde agregar si hay falla
     */
    private void validateRut(EmpleadoRequest request, List<ValidationError> errores) {
        String rut = request.getRut();
        if (rut == null || rut.trim().isEmpty()) {
            errores.add(new ValidationError("rut", "El RUT es requerido"));
            return;
        }
        if (!RutValidator.isValid(rut)) {
            errores.add(new ValidationError("rut", "RUT invalido"));
            return;
        }
        String canonicalRut = RutValidator.toCanonicalFormat(rut);
        if (empleadoRepository.existsByRut(canonicalRut)) {
            errores.add(new ValidationError("rut", "RUT ya existe"));
        }
    }

    /**
     * Valida el salario: requerido y mayor o igual al minimo legal.
     *
     * @param request request conteniendo el salario
     * @param errores lista de errores donde agregar si hay falla
     */
    private void validateSalario(EmpleadoRequest request, List<ValidationError> errores) {
        if (request.getSalario() == null) {
            errores.add(new ValidationError("salario", "El salario es requerido"));
            return;
        }
        if (request.getSalario().compareTo(SALARIO_MINIMO) < 0) {
            errores.add(new ValidationError("salario", "Salario debe ser >= $400,000"));
        }
    }

    /**
     * Valida que el bono no supere el 50% del salario.
     *
     * @param request request conteniendo el bono y salario
     * @param errores lista de errores donde agregar si hay falla
     */
    private void validateBono(EmpleadoRequest request, List<ValidationError> errores) {
        if (request.getBono() == null || request.getSalario() == null) {
            return;
        }
        BigDecimal bonoMaximo = request.getSalario().multiply(BONO_MAXIMO_PORCENTAJE);
        if (request.getBono().compareTo(bonoMaximo) > 0) {
            errores.add(new ValidationError("bono", "Bono no puede superar 50% del salario"));
        }
    }

    /**
     * Valida que los descuentos no superen el salario.
     *
     * @param request request conteniendo los descuentos y salario
     * @param errores lista de errores donde agregar si hay falla
     */
    private void validateDescuentos(EmpleadoRequest request, List<ValidationError> errores) {
        if (request.getDescuentos() == null || request.getSalario() == null) {
            return;
        }
        if (request.getDescuentos().compareTo(request.getSalario()) > 0) {
            errores.add(new ValidationError("descuentos", "Descuentos no pueden superar el salario"));
        }
    }
}
