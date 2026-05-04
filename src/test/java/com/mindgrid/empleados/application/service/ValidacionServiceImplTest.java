package com.mindgrid.empleados.application.service;

import com.mindgrid.empleados.domain.exception.BusinessException;
import com.mindgrid.empleados.domain.model.Empleado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidacionServiceImplTest {

    private static final String RUT_VALIDO = "12345678-5";

    private ValidacionServiceImpl servicio;

    @BeforeEach
    void setUp() {
        servicio = new ValidacionServiceImpl();
    }

    @Test
    @DisplayName("Salario base exactamente $400.000 debe pasar")
    void salarioBaseMinimoPasa() {
        Empleado e = Empleado.builder()
            .nombre("Ana").apellido("López").rut(RUT_VALIDO)
            .cargo("QA").salarioBase(400_000).bono(0).descuentos(0)
            .build();
        assertDoesNotThrow(() -> servicio.validar(e));
    }

    @Test
    @DisplayName("Salario base $399.999 debe lanzar BusinessException")
    void salarioBaseBajoFalla() {
        Empleado e = Empleado.builder()
            .nombre("Ana").apellido("López").rut(RUT_VALIDO)
            .cargo("QA").salarioBase(399_999).bono(0).descuentos(0)
            .build();
        BusinessException ex = assertThrows(BusinessException.class, () -> servicio.validar(e));
        assertTrue(ex.getErrores().stream().anyMatch(err -> err.contains("salario base")));
    }

    @Test
    @DisplayName("Bono igual al 50% del salario base debe pasar")
    void bonoExactoMitadPasa() {
        Empleado e = Empleado.builder()
            .nombre("Juan").apellido("Pérez").rut(RUT_VALIDO)
            .cargo("Dev").salarioBase(600_000).bono(300_000).descuentos(0)
            .build();
        assertDoesNotThrow(() -> servicio.validar(e));
    }

    @Test
    @DisplayName("Bono que supera el 50% del salario base debe fallar")
    void bonoSuperaMitadFalla() {
        Empleado e = Empleado.builder()
            .nombre("Juan").apellido("Pérez").rut(RUT_VALIDO)
            .cargo("Dev").salarioBase(600_000).bono(300_001).descuentos(0)
            .build();
        BusinessException ex = assertThrows(BusinessException.class, () -> servicio.validar(e));
        assertTrue(ex.getErrores().stream().anyMatch(err -> err.contains("bono")));
    }

    @Test
    @DisplayName("Descuentos iguales al salario base deben pasar")
    void descuentosIgualesSalarioPasan() {
        Empleado e = Empleado.builder()
            .nombre("Juan").apellido("Pérez").rut(RUT_VALIDO)
            .cargo("Dev").salarioBase(500_000).bono(0).descuentos(500_000)
            .build();
        assertDoesNotThrow(() -> servicio.validar(e));
    }

    @Test
    @DisplayName("Descuentos que superan el salario base deben fallar")
    void descuentosMayoresFallan() {
        Empleado e = Empleado.builder()
            .nombre("Juan").apellido("Pérez").rut(RUT_VALIDO)
            .cargo("Dev").salarioBase(500_000).bono(0).descuentos(500_001)
            .build();
        BusinessException ex = assertThrows(BusinessException.class, () -> servicio.validar(e));
        assertTrue(ex.getErrores().stream().anyMatch(err -> err.contains("descuentos")));
    }

    @Test
    @DisplayName("DV calculado para 12345678 debe ser '5'")
    void dvCalculadoCorrecto() {
        assertEquals('5', ValidacionServiceImpl.calcularDigitoVerificador("12345678"));
    }

    @Test
    @DisplayName("DV calculado para 11222333 debe ser '9'")
    void dvCalculadoConNueve() {
        assertEquals('9', ValidacionServiceImpl.calcularDigitoVerificador("11222333"));
    }

    @Test
    @DisplayName("RUT 11222333-9 completo debe ser válido")
    void rutValidoConNuevePasa() {
        Empleado e = Empleado.builder()
            .nombre("Pedro").apellido("Soto").rut("11222333-9")
            .cargo("Dev").salarioBase(500_000).bono(0).descuentos(0)
            .build();
        assertDoesNotThrow(() -> servicio.validar(e));
    }

    @Test
    @DisplayName("RUT con formato inválido debe fallar")
    void rutFormatoInvalidoFalla() {
        Empleado e = Empleado.builder()
            .nombre("Luis").apellido("Díaz").rut("no-es-rut")
            .cargo("Dev").salarioBase(500_000).bono(0).descuentos(0)
            .build();
        BusinessException ex = assertThrows(BusinessException.class, () -> servicio.validar(e));
        assertTrue(ex.getErrores().stream().anyMatch(err -> err.contains("RUT") || err.contains("rut")));
    }

    @Test
    @DisplayName("RUT con dígito verificador incorrecto debe fallar")
    void rutDvIncorrectoFalla() {
        Empleado e = Empleado.builder()
            .nombre("Luis").apellido("Díaz").rut("12345678-0")
            .cargo("Dev").salarioBase(500_000).bono(0).descuentos(0)
            .build();
        BusinessException ex = assertThrows(BusinessException.class, () -> servicio.validar(e));
        assertTrue(ex.getErrores().stream().anyMatch(err -> err.contains("dígito verificador") || err.contains("RUT")));
    }

    @Test
    @DisplayName("Múltiples infracciones deben reportarse juntas en un solo lanzamiento")
    void multiplesErroresReportadosJuntos() {
        Empleado e = Empleado.builder()
            .nombre("X").apellido("Y").rut("12345678-0")
            .cargo("Z").salarioBase(100_000).bono(999_999).descuentos(999_999)
            .build();
        BusinessException ex = assertThrows(BusinessException.class, () -> servicio.validar(e));
        assertTrue(ex.getErrores().size() >= 3, "Debe reportar al menos 3 errores");
    }
}
