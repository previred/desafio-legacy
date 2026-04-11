package cl.previred.desafio.util;

public final class RutValidator {

    private RutValidator() {
    }

    public static boolean isValid(String rut) {
        if (rut == null || rut.trim().isEmpty()) {
            return false;
        }

        String normalized = normalize(rut);
        if (!isValidLength(normalized)) {
            return false;
        }

        String body = getBody(normalized);
        char verifier = getVerifier(normalized);

        if (!isNumeric(body)) {
            return false;
        }

        char expectedVerifier = calculateVerifier(body);
        return verifier == expectedVerifier;
    }

    private static String normalize(String rut) {
        return rut.trim()
                  .replace(".", "")
                  .replace("-", "")
                  .toUpperCase();
    }

    private static boolean isValidLength(String rut) {
        int length = rut.length();
        return length >= 8 && length <= 9;
    }

    private static String getBody(String normalized) {
        return normalized.substring(0, normalized.length() - 1);
    }

    private static char getVerifier(String normalized) {
        return normalized.charAt(normalized.length() - 1);
    }

    private static boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    private static char calculateVerifier(String body) {
        int sum = 0;
        int multiplier = 2;

        for (int i = body.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(body.charAt(i));
            sum += digit * multiplier;
            multiplier++;
            if (multiplier == 8) {
                multiplier = 2;
            }
        }

        int remainder = sum % 11;
        int verifier = 11 - remainder;

        if (verifier == 10) {
            return 'K';
        } else if (verifier == 11) {
            return '0';
        } else {
            return Character.forDigit(verifier, 10);
        }
    }
}
