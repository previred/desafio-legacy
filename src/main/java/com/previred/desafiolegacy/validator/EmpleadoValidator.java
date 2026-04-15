package com.previred.desafiolegacy.validator;

import com.previred.desafiolegacy.exception.ValidacionException;
import com.previred.desafiolegacy.model.Empleado;
import com.previred.desafiolegacy.repository.EmpleadoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmpleadoValidator {

    private static final Logger logger = LoggerFactory.getLogger(EmpleadoValidator.class);

    private final EmpleadoRepository empleadoRepository;

    public EmpleadoValidator(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    public void validar(Empleado empleado) {
        logger.info("Validando empleado");
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

    private void validarSalarioBase(double salarioBase) {
        if (salarioBase < 400000) {
            throw new ValidacionException("salarioBase",
                    "El salario base no puede ser menor a $400.000");
        }
    }

    private void validarBono(double bono, double salarioBase) {
        if (bono > salarioBase * 0.5) {
            throw new ValidacionException("bono",
                    "El bono no puede superar el 50% del salario base");
        }
    }

    private void validarDescuentos(double descuentos, double salarioBase) {
        if (descuentos > salarioBase) {
            throw new ValidacionException("descuentos",
                    "El total de descuentos no puede ser mayor al salario base");
        }
    }
}
