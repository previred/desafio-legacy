package com.prueba.desafio.service;

import com.prueba.desafio.config.EmpleadoProperties;
import com.prueba.desafio.util.RutUtil;
import org.springframework.beans.factory.annotation.Value;
import com.prueba.desafio.dao.EmpleadoDao;
import com.prueba.desafio.dto.EmpleadoRequest;
import com.prueba.desafio.dto.ValidationError;
import com.prueba.desafio.model.Empleado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmpleadoService {

    private static final Logger log = LoggerFactory.getLogger(EmpleadoService.class);

    private final EmpleadoDao empleadoDao;
    private final EmpleadoProperties empleadoProperties;
    private final RutUtil rutUtil;

    public EmpleadoService(EmpleadoDao empleadoDao, EmpleadoProperties empleadoProperties, RutUtil rutUtil) {
        this.empleadoDao = empleadoDao;
        this.empleadoProperties = empleadoProperties;
        this.rutUtil = rutUtil;
    }

    public List<Empleado> listar() {
        try {
            return empleadoDao.listar();
        } catch (SQLException e) {
            log.error("Error al listar empleados", e);
            throw new RuntimeException("Error al listar empleados");
        }
    }

    public Empleado guardar(EmpleadoRequest request) {
        List<ValidationError> errors = validarEmpleado(request);

        if (!errors.isEmpty()) {
            throw new BusinessValidationException("Errores de validación", errors);
        }

        Empleado empleado = new Empleado();
        empleado.setNombre(request.getNombre().trim());
        empleado.setApellido(request.getApellido().trim());
        empleado.setRutDni(request.getRutDni().trim());
        empleado.setCargo(request.getCargo().trim());
        empleado.setSalarioBase(request.getSalarioBase());
        empleado.setBono(defaultIfNull(request.getBono()));
        empleado.setDescuentos(defaultIfNull(request.getDescuentos()));

        try {
            Empleado empleadoGuardado = empleadoDao.guardar(empleado);
            log.info("Empleado guardado correctamente con rut/dni={}", empleado.getRutDni());
            return empleadoGuardado;
        } catch (SQLException e) {
            log.error("Error al guardar empleado", e);
            throw new RuntimeException("Error al guardar empleado");
        }
    }

    public boolean eliminar(Long id) {
        try {
            return empleadoDao.eliminarPorId(id);
        } catch (SQLException e) {
            log.error("Error al eliminar empleado id={}", id, e);
            throw new RuntimeException("Error al eliminar empleado");
        }
    }

    private List<ValidationError> validarEmpleado(EmpleadoRequest request) {
        List<ValidationError> errors = new ArrayList<>();

        if (request == null) {
            errors.add(new ValidationError("request", "El cuerpo de la solicitud es obligatorio"));
            return errors;
        }

        if (isBlank(request.getNombre())) {
            errors.add(new ValidationError("nombre", "El nombre es obligatorio"));
        }

        if (isBlank(request.getApellido())) {
            errors.add(new ValidationError("apellido", "El apellido es obligatorio"));
        }

        if (isBlank(request.getRutDni())) {
            errors.add(new ValidationError("rutDni", "El RUT/DNI es obligatorio"));
        }

        if (isBlank(request.getCargo())) {
            errors.add(new ValidationError("cargo", "El cargo es obligatorio"));
        }

        if (request.getSalarioBase() == null) {
            errors.add(new ValidationError("salarioBase", "El salario base es obligatorio"));
        }

        BigDecimal bono = defaultIfNull(request.getBono());
        BigDecimal descuentos = defaultIfNull(request.getDescuentos());

        if (bono.compareTo(BigDecimal.ZERO) < 0) {
            errors.add(new ValidationError("bono", "El bono no puede ser negativo"));
        }

        if (descuentos.compareTo(BigDecimal.ZERO) < 0) {
            errors.add(new ValidationError("descuentos", "Los descuentos no pueden ser negativos"));
        }

        if (request.getSalarioBase() != null) {
            if (request.getSalarioBase().compareTo(empleadoProperties.getSalarioMinimo()) < 0) {
                errors.add(new ValidationError("salarioBase", "El salario base no puede ser menor a "
                        + empleadoProperties.getSalarioMinimo().toPlainString()));
            }

            BigDecimal bonoMaximo = request.getSalarioBase().multiply(empleadoProperties.getBonoMaxPorcentaje());
            if (bono.compareTo(bonoMaximo) > 0) {
                errors.add(new ValidationError("bono", "El bono no puede superar el 50% del salario base"));
            }

            if (descuentos.compareTo(request.getSalarioBase()) > 0) {
                errors.add(new ValidationError("descuentos", "Los descuentos no pueden superar el salario base"));
            }
        }

        if (!isBlank(request.getRutDni())) {
            String rutLimpio = request.getRutDni().trim().replace(".", "").replace("-", "").toUpperCase();

            if (!rutUtil.esRutDniValido(request.getRutDni().trim())) {
                try {
                    String cuerpo = rutLimpio.substring(0, rutLimpio.length() - 1);
                    int rutNumero = Integer.parseInt(cuerpo);

                    String rutCorrecto = rutUtil.generarRutFormateado(rutNumero);

                    errors.add(new ValidationError(
                            "rutDni",
                            "RUT inválido. El RUT correcto es " + rutCorrecto
                    ));
                } catch (Exception e) {
                    errors.add(new ValidationError(
                            "rutDni",
                            "El formato del RUT/DNI no es válido"
                    ));
                }

            } else {
                try {
                    if (empleadoDao.existeRutDni(request.getRutDni().trim())) {
                        errors.add(new ValidationError("rutDni", "Ya existe un empleado con ese RUT/DNI"));
                    }
                } catch (SQLException e) {
                    log.error("Error validando RUT/DNI duplicado", e);
                    throw new RuntimeException("Error validando RUT/DNI duplicado");
                }
            }
        }

        return errors;
    }



    private BigDecimal defaultIfNull(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}