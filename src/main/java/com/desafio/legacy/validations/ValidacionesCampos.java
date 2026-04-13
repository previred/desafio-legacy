package com.desafio.legacy.validations;

import com.desafio.legacy.entities.Empleado;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class ValidacionesCampos {
    public static Map<String, String> validacionCampos(Empleado empleado) {
        Map<String, String> errores = new HashMap<>();

        validar(errores, "nombre", empleado.getNombre(), s -> !s.isBlank(), "El campo nombre es obligatorio");
        validar(errores, "apellido", empleado.getApellido(), s -> !s.isBlank(), "El campo apellido es obligatorio");
        validar(errores, "cargo", empleado.getCargo(), s -> !s.isBlank(), "El campo cargo es obligatorio");

        Optional.ofNullable(empleado.getRut_dni())
                .filter(s -> !s.isBlank())
                .ifPresentOrElse(
                        rut -> { if (!validarRut(rut)) errores.put("rut_dni", "Formato inválido (ej: 12345678-9)"); },
                        () -> errores.put("rut_dni", "El campo RUT/DNI es obligatorio")
                );

        // 3. Validaciones Numéricas (Salario, Bono, Descuento)
        Optional.ofNullable(empleado.getSalario())
                .ifPresentOrElse(salario -> {
                            if (salario <= 0) errores.put("salario", "Debe ser un monto positivo");
                            else if (salario < 400000) errores.put("salario", "El salario mínimo es de $400.000");

                            Optional.ofNullable(empleado.getBono())
                                    .filter(b -> b > (salario * 0.5))
                                    .ifPresent(b -> errores.put("bono", "El bono no puede superar el 50% del salario ($" + (salario * 0.5) + ")"));

                            Optional.ofNullable(empleado.getDescuento())
                                    .filter(d -> d > salario)
                                    .ifPresent(d -> errores.put("descuento", "Los descuentos no pueden ser mayores al salario base."));
                        },
                        () -> errores.put("salario", "El campo salario es obligatorio"));

        return errores;
    }

    private static <T> void validar(Map<String, String> errores, String campo, T valor, Predicate<T> condicion, String mensaje) {
        Optional.ofNullable(valor)
                .filter(condicion)
                .ifPresentOrElse(v -> {}, () -> errores.put(campo, mensaje));
    }

    public static boolean validarRut(String rut) {
        return Optional.ofNullable(rut)
                .map(r -> r.replace(".", "").replace("-", "").toUpperCase())
                .filter(r -> r.length() >= 2)
                .map(r -> {
                    String cuerpo = r.substring(0, r.length() - 1);
                    String dvRecibido = r.substring(r.length() - 1);
                    return calcularDV(cuerpo).map(dvCalc -> dvRecibido.equals(dvCalc)).orElse(false);
                })
                .orElse(false);
    }

    private static Optional<String> calcularDV(String cuerpo) {
        try {
            int m = 0, s = 1;
            int t = Integer.parseInt(cuerpo);
            for (; t != 0; t /= 10) {
                s = (s + t % 10 * (9 - m++ % 6)) % 11;
            }
            return Optional.of(String.valueOf((char) (s != 0 ? s + 47 : 75)));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
