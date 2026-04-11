package cl.previred.desafio.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RutValidatorTest {

    @Test
    public void isValid_rutValidoConGuion_retornaTrue() {
        assertTrue(RutValidator.isValid("11111111-1"));
    }

    @Test
    public void isValid_rutInvalido_retornaFalse() {
        assertFalse(RutValidator.isValid("12345678-0"));
    }

    @Test
    public void isValid_rutNulo_retornaFalse() {
        assertFalse(RutValidator.isValid(null));
    }

    @Test
    public void isValid_rutVacio_retornaFalse() {
        assertFalse(RutValidator.isValid(""));
    }

    @Test
    public void isValid_rutMuyCorto_retornaFalse() {
        assertFalse(RutValidator.isValid("1234567-9"));
    }
}