package com.previred.jaguilar.util;

public class RutUtil {

    private RutUtil() {
    }

    public static boolean formatoValido(String rutDni) {
        if (rutDni == null || rutDni.trim().isEmpty()) {
            return false;
        }

        String rutNormalizado = normalizar(rutDni);

        if (!rutNormalizado.matches("^\\d{7,8}[0-9K]$")) {
            return false;
        }

        String cuerpo = rutNormalizado.substring(0, rutNormalizado.length() - 1);
        String digitoVerificadorIngresado = rutNormalizado.substring(rutNormalizado.length() - 1);

        return digitoVerificadorIngresado.equals(calcularDigitoVerificador(cuerpo));
    }

    public static String normalizar(String rutDni) {
        if (rutDni == null) {
            return null;
        }

        return rutDni.trim()
                .replace(".", "")
                .replace("-", "")
                .toUpperCase();
    }

    private static String calcularDigitoVerificador(String cuerpo) {
        int suma = 0;
        int multiplicador = 2;

        for (int i = cuerpo.length() - 1; i >= 0; i--) {
            suma += Character.getNumericValue(cuerpo.charAt(i)) * multiplicador;
            multiplicador = multiplicador == 7 ? 2 : multiplicador + 1;
        }

        int resultado = 11 - (suma % 11);

        if (resultado == 11) {
            return "0";
        }

        if (resultado == 10) {
            return "K";
        }

        return String.valueOf(resultado);
    }
}