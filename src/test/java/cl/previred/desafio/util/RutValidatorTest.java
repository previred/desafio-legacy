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

    @Test
    void toCanonicalFormatConRutCortoSinGuionRetornaFormatoCanonico() {
        assertEquals("1-9", RutValidator.toCanonicalFormat("19"));
    }

    @Test
    void toCanonicalFormatConRutCortoConGuionMantieneFormatoCanonico() {
        assertEquals("1-9", RutValidator.toCanonicalFormat("1-9"));
    }

    @Test
    void toCanonicalFormatConPuntosYGuionRetornaSinPuntos() {
        assertEquals("12345678-5", RutValidator.toCanonicalFormat("12.345.678-5"));
    }

    @Test
    void toCanonicalFormatSinPuntosNiGuionRetornaConGuion() {
        assertEquals("12345678-5", RutValidator.toCanonicalFormat("123456785"));
    }

    @Test
    void toCanonicalFormatConDigitoKMayusculaRetornaKMayuscula() {
        assertEquals("12345678-K", RutValidator.toCanonicalFormat("12345678-K"));
    }

    @Test
    void toCanonicalFormatConDigitoKMinusculaRetornaKMayuscula() {
        assertEquals("12345678-K", RutValidator.toCanonicalFormat("12345678-k"));
    }

    @Test
    void toCanonicalFormatConRutNuloLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> RutValidator.toCanonicalFormat(null));
    }

    @Test
    void toCanonicalFormatConRutVacioLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> RutValidator.toCanonicalFormat(""));
    }
}