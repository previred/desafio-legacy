package com.mindgrid.empleados.application.service;

import com.mindgrid.empleados.domain.exception.BusinessException;
import com.mindgrid.empleados.domain.model.Empleado;
import com.mindgrid.empleados.domain.service.ValidacionService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ValidacionServiceImpl implements ValidacionService {

    private static final double SALARIO_MINIMO = 400_000.0;
    private static final double PORCENTAJE_MAXIMO_BONO = 0.5;

    @Override
    public void validar(Empleado empleado) {
        List<String> errores = new ArrayList<>();

        validarCamposRequeridos(empleado, errores);
        validarSalarioBase(empleado, errores);
        validarBono(empleado, errores);
        validarDescuentos(empleado, errores);
        validarRut(empleado.getRut(), errores);

        if (!errores.isEmpty()) {
            throw new BusinessException(errores);
        }
    }

    private void validarCamposRequeridos(Empleado empleado, List<String> errores) {
        List<String> faltantes = Arrays.asList(
            empleado.getNombre()   == null || empleado.getNombre().trim().isEmpty()   ? "nombre"   : null,
            empleado.getApellido() == null || empleado.getApellido().trim().isEmpty() ? "apellido" : null,
            empleado.getRut()      == null || empleado.getRut().trim().isEmpty()      ? "rut"      : null,
            empleado.getCargo()    == null || empleado.getCargo().trim().isEmpty()    ? "cargo"    : null
        ).stream().filter(c -> c != null).collect(Collectors.toList());

        if (!faltantes.isEmpty()) {
            errores.add("Los siguientes campos son requeridos: " + String.join(", ", faltantes));
        }
    }

    private void validarSalarioBase(Empleado empleado, List<String> errores) {
        if (empleado.getSalarioBase() < SALARIO_MINIMO) {
            errores.add(String.format(
                "El salario base ($%.0f) no puede ser menor a $%.0f",
                empleado.getSalarioBase(), SALARIO_MINIMO
            ));
        }
    }

    private void validarBono(Empleado empleado, List<String> errores) {
        double topeBono = empleado.getSalarioBase() * PORCENTAJE_MAXIMO_BONO;
        if (empleado.getBono() > topeBono) {
            errores.add(String.format(
                "El bono ($%.0f) no puede superar el 50%% del salario base ($%.0f)",
                empleado.getBono(), topeBono
            ));
        }
    }

    private void validarDescuentos(Empleado empleado, List<String> errores) {
        if (empleado.getDescuentos() > empleado.getSalarioBase()) {
            errores.add(String.format(
                "Los descuentos ($%.0f) no pueden superar el salario base ($%.0f)",
                empleado.getDescuentos(), empleado.getSalarioBase()
            ));
        }
    }

    private void validarRut(String rut, List<String> errores) {
        if (rut == null || rut.trim().isEmpty()) {
            return;
        }

        String rutLimpio = rut.trim().replace(".", "").toUpperCase();

        if (!rutLimpio.matches("\\d{7,8}-[\\dK]")) {
            errores.add("El RUT '" + rut + "' tiene un formato inválido. Use el formato: 12345678-9");
            return;
        }

        String[] partes = rutLimpio.split("-");
        String cuerpo = partes[0];
        char dvIngresado = partes[1].charAt(0);
        char dvCalculado = calcularDigitoVerificador(cuerpo);

        if (dvIngresado != dvCalculado) {
            errores.add("El dígito verificador del RUT '" + rut + "' es incorrecto");
        }
    }

    static char calcularDigitoVerificador(String cuerpo) {
        int suma = 0;
        int multiplicador = 2;

        for (int i = cuerpo.length() - 1; i >= 0; i--) {
            suma += Character.getNumericValue(cuerpo.charAt(i)) * multiplicador;
            multiplicador = multiplicador == 7 ? 2 : multiplicador + 1;
        }

        int resto = 11 - (suma % 11);
        if (resto == 11) return '0';
        if (resto == 10) return 'K';
        return (char) ('0' + resto);
    }
}
