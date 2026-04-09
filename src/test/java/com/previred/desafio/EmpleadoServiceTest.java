package com.previred.desafio;

import com.previred.desafio.dto.EmpleadoRequest;
import com.previred.desafio.exception.EmpleadoNotFoundException;
import com.previred.desafio.exception.RutDuplicadoException;
import com.previred.desafio.model.Empleado;
import com.previred.desafio.repository.EmpleadoRepository;
import com.previred.desafio.service.EmpleadoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmpleadoServiceTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @InjectMocks
    private EmpleadoService empleadoService;

    @Test
    void deberiaRetornarListaDeEmpleados() {
        // Given
        List<Empleado> esperados = Arrays.asList(
                new Empleado(1L, "Juan", "Pérez", "12345678-9", "Dev",
                        new BigDecimal("500000"), new BigDecimal("50000"), new BigDecimal("30000")),
                new Empleado(2L, "Ana", "Gómez", "98765432-1", "QA",
                        new BigDecimal("600000"), BigDecimal.ZERO, new BigDecimal("20000"))
        );
        given(empleadoRepository.findAll()).willReturn(esperados);

        // When
        List<Empleado> resultado = empleadoService.obtenerTodos();

        // Then
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Juan");
    }

    @Test
    void deberiaCrearEmpleadoCorrectamente() {
        // Given
        EmpleadoRequest request = crearRequest("12345678-9", new BigDecimal("500000"),
                new BigDecimal("100000"), new BigDecimal("50000"));
        Empleado guardado = new Empleado(1L, "Juan", "Pérez", "12345678-9", "Dev",
                new BigDecimal("500000"), new BigDecimal("100000"), new BigDecimal("50000"));

        given(empleadoRepository.existsByRut("12345678-9")).willReturn(false);
        given(empleadoRepository.save(any(Empleado.class))).willReturn(guardado);

        // When
        Empleado resultado = empleadoService.crear(request);

        // Then
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getBono()).isEqualByComparingTo(new BigDecimal("100000"));
    }

    @Test
    void deberiaLanzarExcepcionCuandoRutDuplicado() {
        // Given
        EmpleadoRequest request = crearRequest("12345678-9", new BigDecimal("500000"),
                BigDecimal.ZERO, BigDecimal.ZERO);
        given(empleadoRepository.existsByRut("12345678-9")).willReturn(true);

        // When / Then
        assertThatThrownBy(() -> empleadoService.crear(request))
                .isInstanceOf(RutDuplicadoException.class)
                .hasMessageContaining("12345678-9");
    }

    @Test
    void deberiaLanzarExcepcionCuandoSalarioBajoMinimo() {
        // Given
        EmpleadoRequest request = crearRequest("12345678-9", new BigDecimal("300000"),
                BigDecimal.ZERO, BigDecimal.ZERO);

        // When / Then
        assertThatThrownBy(() -> empleadoService.crear(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("400.000");
    }

    @Test
    void deberiaLanzarExcepcionCuandoBonoSuperaLimite() {
        // Given — bono = 300000 > 50% de 500000 (250000)
        EmpleadoRequest request = crearRequest("12345678-9", new BigDecimal("500000"),
                new BigDecimal("300000"), BigDecimal.ZERO);

        // When / Then
        assertThatThrownBy(() -> empleadoService.crear(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("50%");
    }

    @Test
    void deberiaLanzarExcepcionCuandoDescuentoSuperaSalario() {
        // Given — descuento = 600000 > salario 500000
        EmpleadoRequest request = crearRequest("12345678-9", new BigDecimal("500000"),
                BigDecimal.ZERO, new BigDecimal("600000"));

        // When / Then
        assertThatThrownBy(() -> empleadoService.crear(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("descuentos");
    }

    @Test
    void deberiaEliminarEmpleadoExistente() {
        // Given
        given(empleadoRepository.deleteById(1L)).willReturn(true);

        // When
        empleadoService.eliminar(1L);

        // Then
        verify(empleadoRepository).deleteById(1L);
    }

    @Test
    void deberiaLanzarExcepcionAlEliminarEmpleadoInexistente() {
        // Given
        given(empleadoRepository.deleteById(99L)).willReturn(false);

        // When / Then
        assertThatThrownBy(() -> empleadoService.eliminar(99L))
                .isInstanceOf(EmpleadoNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private EmpleadoRequest crearRequest(String rut, BigDecimal salario,
                                          BigDecimal bono, BigDecimal descuento) {
        EmpleadoRequest req = new EmpleadoRequest();
        req.setNombre("Juan");
        req.setApellido("Pérez");
        req.setRut(rut);
        req.setCargo("Desarrollador");
        req.setSalario(salario);
        req.setBono(bono);
        req.setDescuento(descuento);
        return req;
    }
}
