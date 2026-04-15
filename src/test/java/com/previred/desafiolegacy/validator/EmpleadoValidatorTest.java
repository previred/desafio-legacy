package com.previred.desafiolegacy.validator;

import com.previred.desafiolegacy.exception.ValidacionException;
import com.previred.desafiolegacy.model.Empleado;
import com.previred.desafiolegacy.repository.EmpleadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmpleadoValidatorTest {

    private static final String RUT = "11111111-1";
    private static final String NOMBRE = "Wilson";
    private static final String APELLIDO = "Fisk";
    private static final String CARGO = "Developer";

    @Mock
    private EmpleadoRepository empleadoRepository;

    private EmpleadoValidator empleadoValidator;

    @BeforeEach
    void setUp() {
        empleadoValidator = new EmpleadoValidator(empleadoRepository);
    }

    static Stream<BigDecimal> salariosInvalidos() {
        return Stream.of(
                new BigDecimal("0"),
                new BigDecimal("100000"),
                new BigDecimal("399999")
        );
    }

    static Stream<BigDecimal> salariosValidos() {
        return Stream.of(
                new BigDecimal("400000"),
                new BigDecimal("500000"),
                new BigDecimal("1000000")
        );
    }

    @ParameterizedTest
    @MethodSource("salariosInvalidos")
    void validar_lanzaExcepcion_cuandoSalarioMenorA400000(BigDecimal salario) {
        Empleado empleado = new Empleado(null, NOMBRE, APELLIDO, RUT, CARGO,
                salario, BigDecimal.ZERO, BigDecimal.ZERO);
        when(empleadoRepository.existsByRut(RUT)).thenReturn(false);

        ValidacionException ex = assertThrows(ValidacionException.class,
                () -> empleadoValidator.validar(empleado));

        assertEquals("salarioBase", ex.getCampo());
    }

    @ParameterizedTest
    @MethodSource("salariosValidos")
    void validar_noLanzaExcepcion_cuandoSalarioValido(BigDecimal salario) {
        Empleado empleado = new Empleado(null, NOMBRE, APELLIDO, RUT, CARGO,
                salario, BigDecimal.ZERO, BigDecimal.ZERO);
        when(empleadoRepository.existsByRut(RUT)).thenReturn(false);

        assertDoesNotThrow(() -> empleadoValidator.validar(empleado));
    }

    @Test
    void validar_lanzaExcepcion_cuandoBonoSuperaEl50Porciento() {
        Empleado empleado = new Empleado(null, NOMBRE, APELLIDO, RUT, CARGO,
                new BigDecimal("1000000"), new BigDecimal("600000"), BigDecimal.ZERO);
        when(empleadoRepository.existsByRut(RUT)).thenReturn(false);

        ValidacionException ex = assertThrows(ValidacionException.class,
                () -> empleadoValidator.validar(empleado));

        assertEquals("bono", ex.getCampo());
    }

    @Test
    void validar_lanzaExcepcion_cuandoDescuentosSuperanSalario() {
        Empleado empleado = new Empleado(null, NOMBRE, APELLIDO, RUT, CARGO,
                new BigDecimal("1000000"), BigDecimal.ZERO, new BigDecimal("1500000"));
        when(empleadoRepository.existsByRut(RUT)).thenReturn(false);

        ValidacionException ex = assertThrows(ValidacionException.class,
                () -> empleadoValidator.validar(empleado));

        assertEquals("descuentos", ex.getCampo());
    }

    @Test
    void validar_lanzaExcepcion_cuandoRutDuplicado() {
        Empleado empleado = new Empleado(null, NOMBRE, APELLIDO, RUT, CARGO,
                new BigDecimal("1000000"), BigDecimal.ZERO, BigDecimal.ZERO);
        when(empleadoRepository.existsByRut(RUT)).thenReturn(true);

        ValidacionException ex = assertThrows(ValidacionException.class,
                () -> empleadoValidator.validar(empleado));

        assertEquals("rut", ex.getCampo());
    }

    @Test
    void validar_noLanzaExcepcion_cuandoEmpleadoValido() {
        Empleado empleado = new Empleado(null, NOMBRE, APELLIDO, RUT, CARGO,
                new BigDecimal("1000000"), new BigDecimal("400000"), new BigDecimal("500000"));
        when(empleadoRepository.existsByRut(RUT)).thenReturn(false);

        assertDoesNotThrow(() -> empleadoValidator.validar(empleado));
    }

    @Test
    void validar_lanzaExcepcion_cuandoNombreEsNulo() {
        Empleado empleado = new Empleado(null, null, APELLIDO, RUT, CARGO,
                new BigDecimal("800000"), BigDecimal.ZERO, BigDecimal.ZERO);

        ValidacionException ex = assertThrows(ValidacionException.class,
                () -> empleadoValidator.validar(empleado));

        assertEquals("nombre", ex.getCampo());
    }

    @Test
    void validar_lanzaExcepcion_cuandoNombreEsVacio() {
        Empleado empleado = new Empleado(null, "   ", APELLIDO, RUT, CARGO,
                new BigDecimal("800000"), BigDecimal.ZERO, BigDecimal.ZERO);

        ValidacionException ex = assertThrows(ValidacionException.class,
                () -> empleadoValidator.validar(empleado));

        assertEquals("nombre", ex.getCampo());
    }

    @Test
    void validar_lanzaExcepcion_cuandoCargoEsNulo() {
        Empleado empleado = new Empleado(null, NOMBRE, APELLIDO, RUT, null,
                new BigDecimal("800000"), BigDecimal.ZERO, BigDecimal.ZERO);

        ValidacionException ex = assertThrows(ValidacionException.class,
                () -> empleadoValidator.validar(empleado));

        assertEquals("cargo", ex.getCampo());
    }

    @Test
    void validar_lanzaExcepcion_cuandoBonoEsNegativo() {
        Empleado empleado = new Empleado(null, NOMBRE, APELLIDO, RUT, CARGO,
                new BigDecimal("800000"), new BigDecimal("-100000"), BigDecimal.ZERO);
        when(empleadoRepository.existsByRut(RUT)).thenReturn(false);

        ValidacionException ex = assertThrows(ValidacionException.class,
                () -> empleadoValidator.validar(empleado));

        assertEquals("bono", ex.getCampo());
    }

    @Test
    void validar_lanzaExcepcion_cuandoDescuentosSONNegativos() {
        Empleado empleado = new Empleado(null, NOMBRE, APELLIDO, RUT, CARGO,
                new BigDecimal("800000"), BigDecimal.ZERO, new BigDecimal("-50000"));
        when(empleadoRepository.existsByRut(RUT)).thenReturn(false);

        ValidacionException ex = assertThrows(ValidacionException.class,
                () -> empleadoValidator.validar(empleado));

        assertEquals("descuentos", ex.getCampo());
    }
}
