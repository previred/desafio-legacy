package cl.previred.desafio.util;

public final class RutUtil {

    private RutUtil() {
    }

    public static String normalize(String rut) {
        if (rut == null) {
            return null;
        }
        return rut.replace(".", "").replace("-", "").trim().toUpperCase();
    }

    public static boolean isValid(String rut) {
        String normalized = normalize(rut);
        if (normalized == null || normalized.length() < 2) {
            return false;
        }
        String body = normalized.substring(0, normalized.length() - 1);
        char dv = normalized.charAt(normalized.length() - 1);

        if (!body.chars().allMatch(Character::isDigit)) {
            return false;
        }

        int sum = 0;
        int multiplier = 2;
        for (int i = body.length() - 1; i >= 0; i--) {
            sum += Character.digit(body.charAt(i), 10) * multiplier;
            multiplier = multiplier == 7 ? 2 : multiplier + 1;
        }
        int remainder = 11 - (sum % 11);
        char expected;
        if (remainder == 11) {
            expected = '0';
        } else if (remainder == 10) {
            expected = 'K';
        } else {
            expected = Character.forDigit(remainder, 10);
        }
        return expected == dv;
    }
}
