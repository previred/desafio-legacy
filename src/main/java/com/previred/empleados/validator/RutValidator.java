package com.previred.empleados.validator;

public class RutValidator {

    public static boolean isValidRut(String rut) {
        if (rut == null || rut.trim().isEmpty()) {
            return false;
        }

        String cleanRut = rut.replaceAll("[^0-9kK]", "");

        if (cleanRut.length() < 2) {
            return false;
        }

        String rutNumber = cleanRut.substring(0, cleanRut.length() - 1);
        String verifierDigit = cleanRut.substring(cleanRut.length() - 1).toUpperCase();

        try {
            int number = Integer.parseInt(rutNumber);
            String expectedVerifier = calculateVerifierDigit(number);
            return verifierDigit.equals(expectedVerifier);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String normalizeRut(String rut) {
        if (rut == null || rut.trim().isEmpty()) {
            return rut;
        }

        String cleanRut = rut.replaceAll("[^0-9kK]", "");

        if (cleanRut.length() < 2) {
            return cleanRut;
        }

        String rutNumber = cleanRut.substring(0, cleanRut.length() - 1);
        String verifierDigit = cleanRut.substring(cleanRut.length() - 1).toUpperCase();

        StringBuilder formatted = new StringBuilder();
        int count = 0;

        for (int i = rutNumber.length() - 1; i >= 0; i--) {
            if (count == 3) {
                formatted.insert(0, '.');
                count = 0;
            }
            formatted.insert(0, rutNumber.charAt(i));
            count++;
        }

        return formatted.toString() + "-" + verifierDigit;
    }

    private static String calculateVerifierDigit(int rut) {
        int sum = 0;
        int multiplier = 2;

        while (rut > 0) {
            sum += (rut % 10) * multiplier;
            rut /= 10;
            multiplier = multiplier == 7 ? 2 : multiplier + 1;
        }

        int remainder = 11 - (sum % 11);

        if (remainder == 11) {
            return "0";
        } else if (remainder == 10) {
            return "K";
        } else {
            return String.valueOf(remainder);
        }
    }
}
