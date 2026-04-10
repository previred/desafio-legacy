package com.desafio.service;

import com.desafio.dto.EmpleadoDTO;
import com.desafio.model.Empleado;
import com.desafio.repository.EmpleadoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio de lógica de negocio para Empleados.
 * Aplica validaciones estrictas antes de persistir datos.
 * Principio de Responsabilidad Única (SRP) — solo reglas de negocio.
 */
@Service
public class EmpleadoService {

    private static final double SALARIO_MINIMO = 400000;
    private static final double PORCENTAJE_MAXIMO_BONOS = 0.50;

    private final EmpleadoRepository empleadoRepository;

    public EmpleadoService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    /**
     * Lista todos los empleados y los convierte a DTO.
     */
    public List<EmpleadoDTO> listarTodos() {
        return empleadoRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca un empleado por ID y lo retorna como DTO.
     */
    public Optional<EmpleadoDTO> buscarPorId(Long id) {
        return empleadoRepository.findById(id)
                .map(this::toDTO);
    }

    /**
     * Crea un nuevo empleado aplicando todas las reglas de negocio.
     *
     * @param dto datos del empleado a crear
     * @return DTO del empleado creado
     * @throws ValidacionException si alguna regla de negocio falla
     */
    public EmpleadoDTO crearEmpleado(EmpleadoDTO dto) {
        List<String> errores = validar(dto);

        if (!errores.isEmpty()) {
            throw new ValidacionException(errores);
        }

        Empleado empleado = toEntity(dto);
        Empleado guardado = empleadoRepository.save(empleado);
        return toDTO(guardado);
    }

    /**
     * Elimina un empleado por su ID.
     *
     * @return true si se eliminó correctamente
     */
    public boolean eliminarEmpleado(Long id) {
        return empleadoRepository.deleteById(id);
    }

    /**
     * Aplica todas las validaciones de negocio al DTO del empleado.
     * Usa programación funcional con Streams y Lambdas.
     *
     * Reglas de negocio implementadas:
     *   1. Campos obligatorios (nombre, apellido, RUT, cargo, salario).
     *   2. Formato válido de RUT/DNI (defensa en profundidad).
     *   3. Salario base >= $400,000.
     *   4. Bonificaciones < 50% del salario base.
     *   5. Descuentos no pueden superar el salario base.
     *   6. Valores numéricos no negativos.
     *   7. RUT/DNI no duplicado en la base de datos.
     */
    private List<String> validar(EmpleadoDTO dto) {
        List<String> errores = new ArrayList<>();

        // ── Campos obligatorios ──────────────────────────────────────────
        validarCamposObligatorios(dto, errores);

        // ── Formato de RUT (defensa en profundidad — el frontend también valida) ──
        if (dto.getRut() != null && !dto.getRut().trim().isEmpty()) {
            String rutLimpio = dto.getRut().trim();
            if (!rutLimpio.matches("^\\d{1,2}\\.?\\d{3}\\.?\\d{3}-[\\dkK]$")) {
                errores.add(String.format("El formato del RUT/DNI '%s' no es válido. Ejemplo: 12.345.678-9.", rutLimpio));
            }
        }

        // ── Regla 1: Salario mínimo $400,000 ────────────────────────────
        if (dto.getSalarioBase() < SALARIO_MINIMO) {
            errores.add(String.format("El salario base debe ser al menos $%,.0f. Valor recibido: $%,.0f.",
                    SALARIO_MINIMO, dto.getSalarioBase()));
        }

        // ── Regla 2: Bonificaciones no pueden superar el 50% del salario base ──
        if (dto.getBonificaciones() < 0) {
            errores.add("Las bonificaciones no pueden ser negativas.");
        } else {
            double limiteBonos = dto.getSalarioBase() * PORCENTAJE_MAXIMO_BONOS;
            if (dto.getBonificaciones() >= limiteBonos) {
                errores.add(String.format(
                        "Las bonificaciones ($%,.0f) no pueden superar el 50%% del salario base ($%,.0f). Límite: $%,.0f.",
                        dto.getBonificaciones(), dto.getSalarioBase(), limiteBonos));
            }
        }

        // ── Regla 3: Descuentos no pueden ser mayores al salario base ───
        if (dto.getDescuentos() < 0) {
            errores.add("Los descuentos no pueden ser negativos.");
        } else if (dto.getDescuentos() > dto.getSalarioBase()) {
            errores.add(String.format(
                    "El total de descuentos ($%,.0f) no puede ser mayor al salario base ($%,.0f).",
                    dto.getDescuentos(), dto.getSalarioBase()));
        }

        // ── Regla 4: RUT/DNI no duplicado ───────────────────────────────
        if (dto.getRut() != null && !dto.getRut().trim().isEmpty()) {
            if (empleadoRepository.existsByRut(dto.getRut().trim())) {
                errores.add(String.format("Ya existe un empleado registrado con el RUT/DNI '%s'.", dto.getRut().trim()));
            }
        }

        return errores;
    }

    /**
     * Valida que todos los campos obligatorios estén presentes.
     * Usa un enfoque funcional con Streams para iterar las validaciones.
     */
    private void validarCamposObligatorios(EmpleadoDTO dto, List<String> errores) {
        // Pares: (valor, nombre del campo) — validación funcional con Streams
        java.util.Map<String, String> campos = new java.util.LinkedHashMap<>();
        campos.put("nombre", dto.getNombre());
        campos.put("apellido", dto.getApellido());
        campos.put("RUT/DNI", dto.getRut());
        campos.put("cargo", dto.getCargo());

        campos.entrySet().stream()
                .filter(entry -> entry.getValue() == null || entry.getValue().trim().isEmpty())
                .map(entry -> String.format("El campo '%s' es obligatorio.", entry.getKey()))
                .forEach(errores::add);

        if (dto.getSalarioBase() <= 0) {
            errores.add("El salario base es obligatorio y debe ser mayor a cero.");
        }
    }

    // --- Mappers: Principio de Separación de Responsabilidades ---

    private EmpleadoDTO toDTO(Empleado empleado) {
        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setId(empleado.getId());
        dto.setNombre(empleado.getNombre());
        dto.setApellido(empleado.getApellido());
        dto.setRut(empleado.getRut());
        dto.setCargo(empleado.getCargo());
        dto.setSalarioBase(empleado.getSalarioBase());
        dto.setBonificaciones(empleado.getBonificaciones());
        dto.setDescuentos(empleado.getDescuentos());
        dto.setSalarioLiquido(empleado.getSalarioLiquido());
        return dto;
    }

    private Empleado toEntity(EmpleadoDTO dto) {
        Empleado empleado = new Empleado();
        empleado.setNombre(dto.getNombre().trim());
        empleado.setApellido(dto.getApellido().trim());
        empleado.setRut(dto.getRut().trim());
        empleado.setCargo(dto.getCargo().trim());
        empleado.setSalarioBase(dto.getSalarioBase());
        empleado.setBonificaciones(dto.getBonificaciones());
        empleado.setDescuentos(dto.getDescuentos());
        return empleado;
    }

    /**
     * Excepción personalizada para errores de validación de negocio.
     */
    public static class ValidacionException extends RuntimeException {

        private final List<String> errores;

        public ValidacionException(List<String> errores) {
            super("Errores de validación: " + String.join(", ", errores));
            this.errores = errores;
        }

        public List<String> getErrores() {
            return errores;
        }
    }
}

