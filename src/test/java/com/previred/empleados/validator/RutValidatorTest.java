package com.previred.empleados.validator;

import org.junit.Test;

import static org.junit.Assert.*;

public class RutValidatorTest {

    @Test
    public void testRutValido() {
        assertTrue(RutValidator.isValidRut("12.345.678-5"));
        assertTrue(RutValidator.isValidRut("12345678-5"));
        assertTrue(RutValidator.isValidRut("123456785"));
    }

    @Test
    public void testRutConDigitoVerificadorK() {
        assertTrue(RutValidator.isValidRut("11.111.111-1"));
        assertTrue(RutValidator.isValidRut("111111111"));
    }

    @Test
    public void testRutInvalido() {
        assertFalse(RutValidator.isValidRut("12.345.678-0"));
        assertFalse(RutValidator.isValidRut("12.345.678-9"));
        assertFalse(RutValidator.isValidRut("11.111.111-K"));
    }

    @Test
    public void testRutVacio() {
        assertFalse(RutValidator.isValidRut(""));
        assertFalse(RutValidator.isValidRut(null));
        assertFalse(RutValidator.isValidRut("   "));
    }

    @Test
    public void testRutFormatoInvalido() {
        assertFalse(RutValidator.isValidRut("abc"));
        assertFalse(RutValidator.isValidRut("12-345-678"));
        assertFalse(RutValidator.isValidRut("1"));
    }

    @Test
    public void testNormalizeRut() {
        assertEquals("12.345.678-5", RutValidator.normalizeRut("12345678-5"));
        assertEquals("12.345.678-5", RutValidator.normalizeRut("12.345.678-5"));
        assertEquals("12.345.678-5", RutValidator.normalizeRut("123456785"));
        assertEquals("11.111.111-1", RutValidator.normalizeRut("111111111"));
    }

    @Test
    public void testNormalizeRutInvalido() {
        String rut = "12";
        assertEquals("1-2", RutValidator.normalizeRut(rut));

        rut = "";
        assertEquals(rut, RutValidator.normalizeRut(rut));
    }

    @Test
    public void testRutCasosReales() {
        assertTrue(RutValidator.isValidRut("23.456.789-6"));
        assertTrue(RutValidator.isValidRut("7.654.321-6"));
        assertTrue(RutValidator.isValidRut("18.765.432-7"));
    }
}
