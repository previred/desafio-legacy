package com.previred.desafiolegacy.validator;

import com.previred.desafiolegacy.exception.ValidacionException;
import com.previred.desafiolegacy.model.Empleado;
import com.previred.desafiolegacy.repository.EmpleadoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class EmpleadoValidator {

    private static final Logger logger = LoggerFactory.getLogger(EmpleadoValidator.class);

    private final EmpleadoRepository empleadoRepository;

    public EmpleadoValidator(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    public void validar(Empleado empleado) {
        logger.info("Validando empleado");
        validarCamposObligatorios(empleado);
        validarRutDuplicado(empleado.getRut());
        validarSalarioBase(empleado.getSalarioBase());
        validarBono(empleado.getBono(), empleado.getSalarioBase());
        validarDescuentos(empleado.getDescuentos(), empleado.getSalarioBase());
    }

    private void validarRutDuplicado(String rut) {
        if (empleadoRepository.existsByRut(rut)) {
            throw new ValidacionException("rut",
                    "Ya existe un empleado con ese RUT");
        }
    }

    private void validarSalarioBase(BigDecimal salarioBase) {
        if (salarioBase.compareTo(new BigDecimal("400000")) < 0) {
            throw new ValidacionException("salarioBase",
                    "El salario base no puede ser menor a $400.000");
        }
    }

    private void validarBono(BigDecimal bono, BigDecimal salarioBase) {
        if (bono.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacionException("bono",
                    "El bono no puede ser negativo");
        }
        if (bono.compareTo(salarioBase.multiply(new BigDecimal("0.5"))) > 0) {
            throw new ValidacionException("bono",
                    "El bono no puede superar el 50% del salario base");
        }
    }

    private void validarDescuentos(BigDecimal descuentos, BigDecimal salarioBase) {
        if (descuentos.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidacionException("descuentos",
                    "Los descuentos no pueden ser negativos");
        }
        if (descuentos.compareTo(salarioBase) > 0) {
            throw new ValidacionException("descuentos",
                    "El total de descuentos no puede ser mayor al salario base");
        }
    }

    private void validarCamposObligatorios(Empleado empleado) {
        if (empleado.getNombre() == null || empleado.getNombre().trim().isEmpty()) {
            throw new ValidacionException("nombre", "El nombre es obligatorio");
        }
        if (empleado.getApellido() == null || empleado.getApellido().trim().isEmpty()) {
            throw new ValidacionException("apellido", "El apellido es obligatorio");
        }
        if (empleado.getRut() == null || empleado.getRut().trim().isEmpty()) {
            throw new ValidacionException("rut", "El RUT es obligatorio");
        }
        if (empleado.getCargo() == null || empleado.getCargo().trim().isEmpty()) {
            throw new ValidacionException("cargo", "El cargo es obligatorio");
        }
    }
}
