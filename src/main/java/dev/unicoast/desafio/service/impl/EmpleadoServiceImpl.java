package dev.unicoast.desafio.service.impl;

import java.math.BigDecimal;

import dev.unicoast.desafio.exception.BusinessValidationException;
import dev.unicoast.desafio.exception.ResourceNotFoundException;
import dev.unicoast.desafio.model.dto.EmpleadoDTO;
import dev.unicoast.desafio.model.entity.EmpleadoEntity;
import dev.unicoast.desafio.repository.EmpleadoRepository;
import dev.unicoast.desafio.service.EmpleadoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpleadoServiceImpl implements EmpleadoService {
    private static final Logger LOG = LoggerFactory.getLogger(EmpleadoServiceImpl.class);
    private static final BigDecimal SALARIO_MINIMO = new BigDecimal("400000");
    private static final BigDecimal PORCENTAJE_MAXIMO_BONOS = new BigDecimal("0.5");
    
    // Límites correspondientes a las columnas de base de datos (schema.sql)
    private static final int MAX_LENGTH_NOMBRE_APELLIDO = 100;
    private static final int MAX_LENGTH_CARGO = 50;
    private static final BigDecimal LIMITE_DECIMAL_15_2 = new BigDecimal("9999999999999.99");
    
    private static final String MSG_LIMITE_CARACTERES = " caracteres";
    private static final String RUT_REGEX = "^\\d{7,8}-[0-9Kk]$";

    private final EmpleadoRepository repository;

    public EmpleadoServiceImpl(EmpleadoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void registrarEmpleado(EmpleadoDTO dto) {
        if (dto == null) {
            throw new BusinessValidationException("El cuerpo de la solicitud está vacío o no es un JSON válido");
        }
        validarCamposObligatorios(dto);
        validarReglasNegocio(dto);

        EmpleadoEntity entity = toEntity(dto);
        repository.guardar(entity);
        LOG.debug("Empleado guardado en BD (RUT: {})", entity.getRut());
    }

    @Override
    public List<EmpleadoDTO> listarEmpleados() {
        return repository.obtenerTodos().stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public void eliminarEmpleado(String idParam) {
        if (idParam == null || idParam.trim().isEmpty()) {
            throw new BusinessValidationException("El parámetro 'id' es requerido para la eliminación");
        }

        Long id;
        try {
            id = Long.parseLong(idParam.trim());
        } catch (NumberFormatException e) {
            throw new BusinessValidationException("El parámetro 'id' debe ser un número válido");
        }

        LOG.debug("Eliminando empleado con ID: {}", id);
        int filasAfectadas = repository.eliminar(id);
        if (filasAfectadas == 0) {
            throw new ResourceNotFoundException("No se encontró un empleado con ID " + id);
        }
    }

    private void validarCamposObligatorios(EmpleadoDTO dto) {
        List<String> errores = new ArrayList<>();
        
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            errores.add("nombre es requerido");
        } else if (dto.getNombre().length() > MAX_LENGTH_NOMBRE_APELLIDO) {
            errores.add("nombre excede límite de " + MAX_LENGTH_NOMBRE_APELLIDO + MSG_LIMITE_CARACTERES);
        }

        if (dto.getApellido() == null || dto.getApellido().trim().isEmpty()) {
            errores.add("apellido es requerido");
        } else if (dto.getApellido().length() > MAX_LENGTH_NOMBRE_APELLIDO) {
            errores.add("apellido excede límite de " + MAX_LENGTH_NOMBRE_APELLIDO + MSG_LIMITE_CARACTERES);
        }

        if (dto.getRut() == null || dto.getRut().trim().isEmpty()) {
            errores.add("rut es requerido");
        } else if (!dto.getRut().matches(RUT_REGEX)) {
            errores.add("rut debe tener un formato válido (Ej: 12345678-9 o 1234567-K)");
        }

        if (dto.getCargo() == null || dto.getCargo().trim().isEmpty()) {
            errores.add("cargo es requerido");
        } else if (dto.getCargo().length() > MAX_LENGTH_CARGO) {
            errores.add("cargo excede límite de " + MAX_LENGTH_CARGO + MSG_LIMITE_CARACTERES);
        }

        if (!errores.isEmpty()) {
            throw new BusinessValidationException("Error: " + String.join(", ", errores));
        }
    }

    private void validarReglasNegocio(EmpleadoDTO dto) {
        if (dto.getSalarioBase() == null) {
            throw new BusinessValidationException("El sueldo base es requerido");
        }
        
        if (dto.getSalarioBase().compareTo(LIMITE_DECIMAL_15_2) > 0) {
            throw new BusinessValidationException("El sueldo base excede el límite numérico permitido");
        }

        if (dto.getSalarioBase().compareTo(SALARIO_MINIMO) < 0) {
            throw new BusinessValidationException("El sueldo base no puede ser inferior a 400.000");
        }

        validarBonos(dto, LIMITE_DECIMAL_15_2);
        validarDescuentos(dto, LIMITE_DECIMAL_15_2);

        if (repository.existeRut(dto.getRut())) {
            throw new BusinessValidationException("El RUT ingresado ya se encuentra registrado en el sistema");
        }
    }

    private void validarBonos(EmpleadoDTO dto, BigDecimal limiteMaximo) {
        if (dto.getBonos() == null) return;

        if (dto.getBonos().compareTo(limiteMaximo) > 0) {
            throw new BusinessValidationException("Los bonos exceden el límite numérico permitido");
        }
        if (dto.getBonos().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessValidationException("Los bonos no pueden ser montos negativos");
        }
        if (dto.getBonos().compareTo(dto.getSalarioBase().multiply(PORCENTAJE_MAXIMO_BONOS)) > 0) {
            throw new BusinessValidationException("Los bonos no pueden superar el 50% del salario base");
        }
    }

    private void validarDescuentos(EmpleadoDTO dto, BigDecimal limiteMaximo) {
        if (dto.getDescuentos() == null) return;

        if (dto.getDescuentos().compareTo(limiteMaximo) > 0) {
            throw new BusinessValidationException("Los descuentos exceden el límite numérico permitido");
        }
        if (dto.getDescuentos().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessValidationException("Los descuentos no pueden ser montos negativos");
        }
        if (dto.getDescuentos().compareTo(dto.getSalarioBase()) > 0) {
            throw new BusinessValidationException("El total de descuentos no puede ser mayor al salario base");
        }
    }

    private EmpleadoEntity toEntity(EmpleadoDTO dto) {
        EmpleadoEntity entity = new EmpleadoEntity();
        entity.setNombre(dto.getNombre());
        entity.setApellido(dto.getApellido());
        entity.setRut(dto.getRut());
        entity.setCargo(dto.getCargo());
        entity.setSalarioBase(dto.getSalarioBase());
        entity.setBonos(dto.getBonos() != null ? dto.getBonos() : BigDecimal.ZERO);
        entity.setDescuentos(dto.getDescuentos() != null ? dto.getDescuentos() : BigDecimal.ZERO);
        return entity;
    }

    private EmpleadoDTO toDto(EmpleadoEntity entity) {
        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setApellido(entity.getApellido());
        dto.setRut(entity.getRut());
        dto.setCargo(entity.getCargo());
        dto.setSalarioBase(entity.getSalarioBase());
        dto.setBonos(entity.getBonos());
        dto.setDescuentos(entity.getDescuentos());
        return dto;
    }
}
