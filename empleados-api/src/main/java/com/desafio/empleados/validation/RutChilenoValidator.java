package com.desafio.empleados.validation;

import java.util.regex.Pattern;

/**
 * Valida RUT chileno (cuerpo numérico + dígito verificador).
 * El desafío menciona RUT/DNI; aquí se documenta soporte explícito para RUT CL.
 */
public final class RutChilenoValidator {

    private static final Pattern SOLO_DIGITOS_DV = Pattern.compile("^\\d{7,8}[\\dKk]$");

    private RutChilenoValidator() {
    }

    /**
     * Quita puntos y guión, deja cuerpo + dv en mayúsculas (K).
     */
    public static String normalizar(String rut) {
        if (rut == null) {
            return "";
        }
        String t = rut.trim().replace(".", "").replace("-", "");
        if (t.isEmpty()) {
            return "";
        }
        char dv = Character.toUpperCase(t.charAt(t.length() - 1));
        String cuerpo = t.substring(0, t.length() - 1);
        return cuerpo + "-" + dv;
    }

    public static boolean esFormatoPlausible(String normalizado) {
        if (normalizado == null || normalizado.isEmpty()) {
            return false;
        }
        String sinGuion = normalizado.replace("-", "");
        return SOLO_DIGITOS_DV.matcher(sinGuion).matches();
    }

    public static boolean esValido(String rutIngreso) {
        String n = normalizar(rutIngreso);
        if (!esFormatoPlausible(n)) {
            return false;
        }
        int guion = n.lastIndexOf('-');
        String cuerpo = n.substring(0, guion);
        char dvEsperado = digitoVerificador(Integer.parseInt(cuerpo));
        char dvRecibido = n.charAt(guion + 1);
        return dvEsperado == dvRecibido;
    }

    private static char digitoVerificador(int rut) {
        int m = 0;
        int s = 1;
        int t = rut;
        for (; t != 0; t /= 10) {
            s = (s + t % 10 * (9 - (m++ % 6))) % 11;
        }
        return (char) (s != 0 ? s + 47 : 75);
    }
}
