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
import java.util.regex.Pattern;

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
 *   <li>Validacion de formato de texto para nombre y apellido (solo letras y espacios)</li>
 *   <li>Validacion de longitud minima y maxima de campos de texto</li>
 *   <li>Validacion de no negativos en campos numericos opcionales</li>
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

    /** Patrón para validar nombre y apellido: solo letras, espacios, tildes y ñ. */
    private static final Pattern PATTERN_NOMBRE_APELLIDO = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,50}$");

    /** Patrón para validar cargo: letras, espacios, tildes, ñ, numeros, puntos y guiones. */
    private static final Pattern PATTERN_CARGO = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s0-9.-]{2,100}$");

    /** Patrón para detectar cadenas compuestas solo de simbolos. */
    private static final Pattern PATTERN_SIMBOLOS_SOLO = Pattern.compile("^[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?~`]+$");

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

        validateNombre(request.getNombre(), errores);
        validateApellido(request.getApellido(), errores);
        validateRut(request, errores);
        validateCargo(request.getCargo(), errores);
        validateSalario(request, errores);
        validateBono(request, errores);
        validateDescuentos(request, errores);

        if (!errores.isEmpty()) {
            throw new ValidationExceptionList(errores);
        }
    }

    /**
     * Valida el campo nombre: requerido, longitud 2-50, solo letras y espacios.
     *
     * @param nombre   valor a validar
     * @param errores  lista de errores donde agregar si hay falla
     */
    private void validateNombre(String nombre, List<ValidationError> errores) {
        if (nombre == null || nombre.trim().isEmpty()) {
            errores.add(new ValidationError("nombre", "El nombre es requerido"));
            return;
        }
        String sanitized = sanitizeText(nombre);
        if (sanitized.length() < 2) {
            errores.add(new ValidationError("nombre", "El nombre debe tener al menos 2 caracteres"));
        }
        if (sanitized.length() > 50) {
            errores.add(new ValidationError("nombre", "El nombre no puede exceder 50 caracteres"));
        }
        if (!PATTERN_NOMBRE_APELLIDO.matcher(sanitized).matches() || isOnlySymbols(sanitized)) {
            errores.add(new ValidationError("nombre", "El nombre solo puede contener letras y espacios"));
        }
    }

    /**
     * Valida el campo apellido: requerido, longitud 2-50, solo letras y espacios.
     *
     * @param apellido valor a validar
     * @param errores  lista de errores donde agregar si hay falla
     */
    private void validateApellido(String apellido, List<ValidationError> errores) {
        if (apellido == null || apellido.trim().isEmpty()) {
            errores.add(new ValidationError("apellido", "El apellido es requerido"));
            return;
        }
        String sanitized = sanitizeText(apellido);
        if (sanitized.length() < 2) {
            errores.add(new ValidationError("apellido", "El apellido debe tener al menos 2 caracteres"));
        }
        if (sanitized.length() > 50) {
            errores.add(new ValidationError("apellido", "El apellido no puede exceder 50 caracteres"));
        }
        if (!PATTERN_NOMBRE_APELLIDO.matcher(sanitized).matches() || isOnlySymbols(sanitized)) {
            errores.add(new ValidationError("apellido", "El apellido solo puede contener letras y espacios"));
        }
    }

    /**
     * Valida el campo cargo: requerido, longitud 2-100, caracteres alfanumericos y especiales permitidos.
     *
     * @param cargo    valor a validar
     * @param errores  lista de errores donde agregar si hay falla
     */
    private void validateCargo(String cargo, List<ValidationError> errores) {
        if (cargo == null || cargo.trim().isEmpty()) {
            errores.add(new ValidationError("cargo", "El cargo es requerido"));
            return;
        }
        String sanitized = sanitizeText(cargo);
        if (sanitized.length() < 2) {
            errores.add(new ValidationError("cargo", "El cargo debe tener al menos 2 caracteres"));
        }
        if (sanitized.length() > 100) {
            errores.add(new ValidationError("cargo", "El cargo no puede exceder 100 caracteres"));
        }
        if (isOnlySymbols(sanitized)) {
            errores.add(new ValidationError("cargo", "El cargo no puede ser solo simbolos"));
        }
        if (!PATTERN_CARGO.matcher(sanitized).matches()) {
            errores.add(new ValidationError("cargo", "El cargo contiene caracteres invalidos"));
        }
    }

    /**
     * Normaliza texto: elimina espacios redundantes y trimea.
     *
     * @param text texto a sanitizar
     * @return texto sanitizado
     */
    private String sanitizeText(String text) {
        if (text == null) return "";
        return text.trim().replaceAll("\\s+", " ");
    }

    /**
     * Detecta si una cadena esta compuesta solo por simbolos.
     *
     * @param text texto a verificar
     * @return true si solo contiene simbolos
     */
    private boolean isOnlySymbols(String text) {
        return PATTERN_SIMBOLOS_SOLO.matcher(text).matches();
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
     * Valida el salario: requerido, no negativo y mayor o igual al minimo legal.
     *
     * @param request request conteniendo el salario
     * @param errores lista de errores donde agregar si hay falla
     */
    private void validateSalario(EmpleadoRequest request, List<ValidationError> errores) {
        if (request.getSalario() == null) {
            errores.add(new ValidationError("salario", "El salario es requerido"));
            return;
        }
        if (request.getSalario().compareTo(BigDecimal.ZERO) < 0) {
            errores.add(new ValidationError("salario", "El salario no puede ser negativo"));
            return;
        }
        if (request.getSalario().compareTo(SALARIO_MINIMO) < 0) {
            errores.add(new ValidationError("salario", "Salario debe ser >= $400,000"));
        }
    }

    /**
     * Valida que el bono no sea negativo y no supere el 50% del salario.
     *
     * @param request request conteniendo el bono y salario
     * @param errores lista de errores donde agregar si hay falla
     */
    private void validateBono(EmpleadoRequest request, List<ValidationError> errores) {
        if (request.getBono() == null) {
            return;
        }
        if (request.getBono().compareTo(BigDecimal.ZERO) < 0) {
            errores.add(new ValidationError("bono", "El bono no puede ser negativo"));
            return;
        }
        if (request.getSalario() == null) {
            return;
        }
        BigDecimal bonoMaximo = request.getSalario().multiply(BONO_MAXIMO_PORCENTAJE);
        if (request.getBono().compareTo(bonoMaximo) > 0) {
            errores.add(new ValidationError("bono", "Bono no puede superar 50% del salario"));
        }
    }

    /**
     * Valida que los descuentos no sean negativos y no superen el salario.
     *
     * @param request request conteniendo los descuentos y salario
     * @param errores lista de errores donde agregar si hay falla
     */
    private void validateDescuentos(EmpleadoRequest request, List<ValidationError> errores) {
        if (request.getDescuentos() == null) {
            return;
        }
        if (request.getDescuentos().compareTo(BigDecimal.ZERO) < 0) {
            errores.add(new ValidationError("descuentos", "Los descuentos no pueden ser negativos"));
            return;
        }
        if (request.getSalario() == null) {
            return;
        }
        if (request.getDescuentos().compareTo(request.getSalario()) > 0) {
            errores.add(new ValidationError("descuentos", "Descuentos no pueden superar el salario"));
        }
    }
}
