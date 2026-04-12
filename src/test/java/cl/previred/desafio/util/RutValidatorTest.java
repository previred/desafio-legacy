package cl.previred.desafio.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RutValidatorTest {

    @Test
    void isValidRutValidoConGuionRetornaTrue() {
        assertTrue(RutValidator.isValid("11111111-1"));
    }

    @Test
    void isValidRutInvalidoRetornaFalse() {
        assertFalse(RutValidator.isValid("12345678-0"));
    }

    @Test
    void isValidRutNuloRetornaFalse() {
        assertFalse(RutValidator.isValid(null));
    }

    @Test
    void isValidRutVacioRetornaFalse() {
        assertFalse(RutValidator.isValid(""));
    }

    @Test
    void isValidRutMuyCortoRetornaFalse() {
        assertFalse(RutValidator.isValid("1234567-9"));
    }

    @Test
    void isValidRutMinimoValidoRetornaTrue() {
        assertTrue(RutValidator.isValid("1-9"));
    }

    @Test
    void isValidRutMinimoSinGuionRetornaTrue() {
        assertTrue(RutValidator.isValid("19"));
    }

    @Test
    void isValidRutLongitudUnoRetornaFalse() {
        assertFalse(RutValidator.isValid("1"));
    }

    @Test
    void isValidRutMinimoConDvIncorrectoRetornaFalse() {
        assertFalse(RutValidator.isValid("1-8"));
    }
}