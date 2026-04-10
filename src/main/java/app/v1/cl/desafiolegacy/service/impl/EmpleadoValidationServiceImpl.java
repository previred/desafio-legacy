package app.v1.cl.desafiolegacy.service.impl;

import app.v1.cl.desafiolegacy.repository.EmpleadoRepository;
import app.v1.cl.desafiolegacy.model.Empleado;
import app.v1.cl.desafiolegacy.service.EmpleadoValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpleadoValidationServiceImpl implements EmpleadoValidationService {

    private static final BigDecimal SALARIO_MINIMO = new BigDecimal("400000");
    private static final BigDecimal PORCENTAJE_BONO_MAXIMO = new BigDecimal("0.50");
    private static final String RUT_PATTERN = "^\\d{1,2}\\.?\\d{3}\\.?\\d{3}-[\\dkK]$";

    private final EmpleadoRepository empleadoRepository;

    @Override
    public List<String> validar(Empleado empleado) {
        List<String> errores = new ArrayList<>();

        validarCamposObligatorios(empleado, errores);
        if (!errores.isEmpty()) {
            return errores;
        }

        validarFormatoRut(empleado.getRut(), errores);
        validarRutDuplicado(empleado.getRut(), errores);
        validarSalarioMinimo(empleado.getSalario(), errores);
        validarBonos(empleado, errores);
        validarDescuentos(empleado, errores);

        return errores;
    }

    private void validarCamposObligatorios(Empleado empleado, List<String> errores) {
        if (isBlank(empleado.getNombre())) {
            errores.add("El nombre es obligatorio.");
        }
        if (isBlank(empleado.getApellido())) {
            errores.add("El apellido es obligatorio.");
        }
        if (isBlank(empleado.getRut())) {
            errores.add("El RUT/DNI es obligatorio.");
        }
        if (isBlank(empleado.getCargo())) {
            errores.add("El cargo es obligatorio.");
        }
        if (empleado.getSalario() == null) {
            errores.add("El salario es obligatorio.");
        }
    }

    private void validarFormatoRut(String rut, List<String> errores) {
        if (rut != null && !rut.matches(RUT_PATTERN)) {
            errores.add("El formato del RUT/DNI es inválido. Formato esperado: 12.345.678-9");
        }
    }

    private void validarRutDuplicado(String rut, List<String> errores) {
        if (rut != null && empleadoRepository.existsByRut(rut)) {
            errores.add("Ya existe un empleado con el RUT/DNI ingresado.");
        }
    }

    private void validarSalarioMinimo(BigDecimal salario, List<String> errores) {
        if (salario != null && salario.compareTo(SALARIO_MINIMO) < 0) {
            errores.add("El salario base no puede ser menor a $400,000.");
        }
    }

    private void validarBonos(Empleado empleado, List<String> errores) {
        BigDecimal bonos = empleado.getBonos();
        BigDecimal salario = empleado.getSalario();
        if (bonos != null && salario != null) {
            BigDecimal bonoMaximo = salario.multiply(PORCENTAJE_BONO_MAXIMO);
            if (bonos.compareTo(bonoMaximo) > 0) {
                errores.add("Los bonos no pueden superar el 50% del salario base.");
            }
        }
    }

    private void validarDescuentos(Empleado empleado, List<String> errores) {
        BigDecimal descuentos = empleado.getDescuentos();
        BigDecimal salario = empleado.getSalario();
        if (descuentos != null && salario != null && descuentos.compareTo(salario) > 0) {
            errores.add("El total de descuentos no puede ser mayor al salario base.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
