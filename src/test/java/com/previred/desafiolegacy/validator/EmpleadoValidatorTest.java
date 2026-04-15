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

    static Stream<Double> salariosInvalidos() {
        return Stream.of(0.0, 100000.0, 399999.0);
    }

    static Stream<Double> salariosValidos() {
        return Stream.of(400000.0, 500000.0, 1000000.0);
    }

    @ParameterizedTest
    @MethodSource("salariosInvalidos")
    void validar_lanzaExcepcion_cuandoSalarioMenorA400000(double salario) {
        Empleado empleado = new Empleado(null, NOMBRE, APELLIDO, RUT,
                CARGO, salario, 0, 0);
        when(empleadoRepository.existsByRut(RUT)).thenReturn(false);

        ValidacionException ex = assertThrows(ValidacionException.class,
                () -> empleadoValidator.validar(empleado));

        assertEquals("salarioBase", ex.getCampo());
    }

    @ParameterizedTest
    @MethodSource("salariosValidos")
    void validar_noLanzaExcepcion_cuandoSalarioValido(double salario) {
        Empleado empleado = new Empleado(null, NOMBRE, APELLIDO, RUT,
                CARGO, salario, 0, 0);
        when(empleadoRepository.existsByRut(RUT)).thenReturn(false);

        assertDoesNotThrow(() -> empleadoValidator.validar(empleado));
    }

    @Test
    void validar_lanzaExcepcion_cuandoBonoSuperaEl50Porciento() {
        Empleado empleado = new Empleado(null, NOMBRE, APELLIDO, RUT,
                CARGO, 1000000, 600000, 0);
        when(empleadoRepository.existsByRut(RUT)).thenReturn(false);

        ValidacionException ex = assertThrows(ValidacionException.class,
                () -> empleadoValidator.validar(empleado));

        assertEquals("bono", ex.getCampo());
    }

    @Test
    void validar_lanzaExcepcion_cuandoDescuentosSuperanSalario() {
        Empleado empleado = new Empleado(null, NOMBRE, APELLIDO, RUT,
                CARGO, 1000000, 0, 1500000);
        when(empleadoRepository.existsByRut(RUT)).thenReturn(false);

        ValidacionException ex = assertThrows(ValidacionException.class,
                () -> empleadoValidator.validar(empleado));

        assertEquals("descuentos", ex.getCampo());
    }

    @Test
    void validar_lanzaExcepcion_cuandoRutDuplicado() {
        Empleado empleado = new Empleado(null, NOMBRE, APELLIDO, RUT,
                CARGO, 1000000, 0, 0);
        when(empleadoRepository.existsByRut(RUT)).thenReturn(true);

        ValidacionException ex = assertThrows(ValidacionException.class,
                () -> empleadoValidator.validar(empleado));

        assertEquals("rut", ex.getCampo());
    }

    @Test
    void validar_noLanzaExcepcion_cuandoEmpleadoValido() {
        Empleado empleado = new Empleado(null, NOMBRE, APELLIDO, RUT,
                CARGO, 1000000, 400000, 500000);
        when(empleadoRepository.existsByRut(RUT)).thenReturn(false);

        assertDoesNotThrow(() -> empleadoValidator.validar(empleado));
    }
}
