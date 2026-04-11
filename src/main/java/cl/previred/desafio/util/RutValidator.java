package cl.previred.desafio.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilidad para la validacion de RUT chileno.
 *
 * <p>El RUT (Rol Único Tributario) es el identificador unico que se asigna
 * a cada persona o empresa en Chile. Esta clase implementa el algoritmo
 * oficial de validacion del digito verificador.</p>
 *
 * <p>El formato valido es: XX.XXX.XXX-Y donde:</p>
 * <ul>
 *   <li>XX.XXX.XXX es el numero correlativo</li>
 *   <li>Y es el digito verificador (0-9 o K)</li>
 * </ul>
 *
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * boolean valido = RutValidator.isValid("12.345.678-9");
 * boolean valido2 = RutValidator.isValid("12345678k");
 * }</pre>
 *
 * <p>Esta clase es final e inmutable (utilidad estatica).</p>
 *
 * @since 1.0
 */
public final class RutValidator {

    /** Logger para debug (opcional, puede removerse en produccion). */
    private static final Logger LOG = LoggerFactory.getLogger(RutValidator.class);

    /**
     * Constructor privado para prevenir instanciacion.
     * Pattern Utility Class.
     */
    private RutValidator() {
    }

    /**
     * Valida si un RUT es valido segun el algoritmo chileno.
     *
     * <p>La validacion incluye:</p>
     * <ul>
     *   <li>Verificacion de null o vacio</li>
     *   <li>Longitud valida (8-9 caracteres sin puntos ni guion)</li>
     *   <li>El cuerpo debe ser numerico</li>
     *   <li>Calculo del digito verificador</li>
     * </ul>
     *
     * @param rut RUT a validar, puede tener o sin puntos y guion
     * @return true si el RUT es valido, false en caso contrario
     */
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

    /**
     * Normaliza el RUT eliminando puntos y guiones, y convirtiendo a mayusculas.
     *
     * @param rut RUT original
     * @return RUT normalizado
     */
    private static String normalize(String rut) {
        return rut.trim()
                .replace(".", "")
                .replace("-", "")
                .toUpperCase();
    }

    /**
     * Verifica que la longitud del RUT normalizado sea valida.
     *
     * @param rut RUT ya normalizado
     * @return true si la longitud es 8 o 9
     */
    private static boolean isValidLength(String rut) {
        int length = rut.length();
        return length >= 8 && length <= 9;
    }

    /**
     * Extrae el cuerpo numerico del RUT (sin digito verificador).
     *
     * @param normalized RUT normalizado
     * @return cuerpo del RUT
     */
    private static String getBody(String normalized) {
        return normalized.substring(0, normalized.length() - 1);
    }

    /**
     * Obtiene el digito verificador del RUT.
     *
     * @param normalized RUT normalizado
     * @return caracter del digito verificador
     */
    private static char getVerifier(String normalized) {
        return normalized.charAt(normalized.length() - 1);
    }

    /**
     * Verifica si una cadena contiene solo digitos numericos.
     *
     * @param str cadena a verificar
     * @return true si es numerica, false en caso contrario
     */
    private static boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Calcula el digito verificador para un cuerpo de RUT dado.
     *
     * <p>Implementa el algoritmo oficial chileno:</p>
     * <ol>
     *   <li>Multiplicar cada digito por 2,3,4,...,7,2,3,... (ciclico)</li>
     *   <li>Sumar todos los productos</li>
     *   <li>Calcular: 11 - (suma mod 11)</li>
     *   <li>Si resultado es 10, retorna K; si es 11, retorna 0</li>
     * </ol>
     *
     * @param body cuerpo del RUT (solo digitos)
     * @return digito verificador calculado
     */
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
