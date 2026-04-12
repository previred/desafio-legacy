package cl.previred.desafio.service;

import cl.previred.desafio.dao.EmpleadoDao;
import cl.previred.desafio.model.Empleado;
import cl.previred.desafio.util.RutUtil;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class EmpleadoValidator {

    public static final BigDecimal SALARIO_MINIMO = new BigDecimal("400000");

    private final EmpleadoDao empleadoDao;

    public EmpleadoValidator(EmpleadoDao empleadoDao) {
        this.empleadoDao = empleadoDao;
    }

    public List<String> validate(Empleado e) throws SQLException {
        List<String> errors = new ArrayList<>();

        if (isBlank(e.getNombre())) {
            errors.add("El nombre es obligatorio.");
        }
        if (isBlank(e.getApellido())) {
            errors.add("El apellido es obligatorio.");
        }
        if (isBlank(e.getCargo())) {
            errors.add("El cargo es obligatorio.");
        }
        if (isBlank(e.getRut())) {
            errors.add("El RUT es obligatorio.");
        } else if (!RutUtil.isValid(e.getRut())) {
            errors.add("El RUT no tiene un formato valido.");
        } else if (empleadoDao.existsByRut(RutUtil.normalize(e.getRut()))) {
            errors.add("Ya existe un empleado con el RUT " + e.getRut() + ".");
        }

        BigDecimal salario = e.getSalarioBase();
        if (salario == null) {
            errors.add("El salario base es obligatorio.");
        } else if (salario.compareTo(SALARIO_MINIMO) < 0) {
            errors.add("El salario base debe ser mayor o igual a $" + SALARIO_MINIMO + ".");
        }

        BigDecimal bonos = e.getBonos() != null ? e.getBonos() : BigDecimal.ZERO;
        BigDecimal descuentos = e.getDescuentos() != null ? e.getDescuentos() : BigDecimal.ZERO;

        if (bonos.signum() < 0) {
            errors.add("Los bonos no pueden ser negativos.");
        }
        if (descuentos.signum() < 0) {
            errors.add("Los descuentos no pueden ser negativos.");
        }

        if (salario != null && salario.signum() > 0) {
            BigDecimal maxBonos = salario.multiply(new BigDecimal("0.5"));
            if (bonos.compareTo(maxBonos) > 0) {
                errors.add("Los bonos no pueden superar el 50% del salario base.");
            }
            if (descuentos.compareTo(salario) > 0) {
                errors.add("Los descuentos no pueden superar el salario base.");
            }
        }

        return errors;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
