package com.previred.empleados.validator;

import com.previred.empleados.dto.EmpleadoRequestDTO;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class EmpleadoValidatorTest {

    private EmpleadoValidator validator;
    private EmpleadoRequestDTO request;

    @Before
    public void setUp() {
        validator = new EmpleadoValidator();
        request = new EmpleadoRequestDTO();
        request.setNombre("Juan");
        request.setApellido("Pérez");
        request.setRut("12.345.678-5");
        request.setCargo("Desarrollador");
        request.setSalarioBase(new BigDecimal("1000000"));
        request.setBonos(new BigDecimal("200000"));
        request.setDescuentos(new BigDecimal("50000"));
    }

    @Test
    public void testValidacionExitosa() {
        ValidationResult result = validator.validate(request);
        assertTrue(result.isValid());
        assertEquals(0, result.getErrors().size());
    }

    @Test
    public void testNombreObligatorio() {
        request.setNombre(null);
        ValidationResult result = validator.validate(request);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("nombre")));

        request.setNombre("");
        result = validator.validate(request);
        assertFalse(result.isValid());
    }

    @Test
    public void testApellidoObligatorio() {
        request.setApellido(null);
        ValidationResult result = validator.validate(request);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("apellido")));
    }

    @Test
    public void testRutInvalido() {
        request.setRut("12.345.678-0");
        ValidationResult result = validator.validate(request);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("RUT")));
    }

    @Test
    public void testRutObligatorio() {
        request.setRut(null);
        ValidationResult result = validator.validate(request);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("RUT")));
    }

    @Test
    public void testSalarioMinimoInvalido() {
        request.setSalarioBase(new BigDecimal("300000"));
        ValidationResult result = validator.validate(request);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("400.000")));
    }

    @Test
    public void testSalarioBaseObligatorio() {
        request.setSalarioBase(null);
        ValidationResult result = validator.validate(request);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("salario base")));
    }

    @Test
    public void testBonosExcesivos() {
        request.setSalarioBase(new BigDecimal("1000000"));
        request.setBonos(new BigDecimal("600000"));
        ValidationResult result = validator.validate(request);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("50%")));
    }

    @Test
    public void testBonosMaximosValidos() {
        request.setSalarioBase(new BigDecimal("1000000"));
        request.setBonos(new BigDecimal("500000"));
        ValidationResult result = validator.validate(request);
        assertTrue(result.isValid());
    }

    @Test
    public void testBonosNegativos() {
        request.setBonos(new BigDecimal("-100000"));
        ValidationResult result = validator.validate(request);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("bonos") && e.contains("negativos")));
    }

    @Test
    public void testDescuentosExcesivos() {
        request.setSalarioBase(new BigDecimal("1000000"));
        request.setDescuentos(new BigDecimal("1500000"));
        ValidationResult result = validator.validate(request);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("descuentos")));
    }

    @Test
    public void testDescuentosNegativos() {
        request.setDescuentos(new BigDecimal("-50000"));
        ValidationResult result = validator.validate(request);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream().anyMatch(e -> e.contains("descuentos") && e.contains("negativos")));
    }

    @Test
    public void testMultiplesErrores() {
        request.setNombre(null);
        request.setApellido(null);
        request.setRut("invalido");
        request.setSalarioBase(new BigDecimal("100000"));

        ValidationResult result = validator.validate(request);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().size() >= 3);
    }
}
