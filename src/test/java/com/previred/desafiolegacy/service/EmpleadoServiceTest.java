package com.previred.desafiolegacy.service;

import com.previred.desafiolegacy.model.Empleado;
import com.previred.desafiolegacy.repository.EmpleadoRepository;
import com.previred.desafiolegacy.validator.EmpleadoValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmpleadoServiceTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private EmpleadoValidator empleadoValidator;

    @InjectMocks
    private EmpleadoService empleadoService;

    @Test
    void obtenerTodos_retornaListaOrdenadaPorNombre() {
        Empleado pedro = new Empleado(1L, "Pedro", "Reyes", "11111111-1",
                "Developer", new BigDecimal("800000"), new BigDecimal("100000"), new BigDecimal("50000"));
        Empleado luisa = new Empleado(2L, "Luisa", "Fernandez", "22222222-2",
                "Designer", new BigDecimal("700000"), new BigDecimal("50000"), new BigDecimal("30000"));
        when(empleadoRepository.findAll()).thenReturn(Arrays.asList(pedro, luisa));

        List<Empleado> resultado = empleadoService.obtenerTodos();

        assertEquals(2, resultado.size());
        assertEquals("Luisa", resultado.get(0).getNombre());
        assertEquals("Pedro", resultado.get(1).getNombre());
    }

    @Test
    void eliminar_retornaFalse_cuandoEmpleadoNoExiste() {
        when(empleadoRepository.deleteById(99L)).thenReturn(false);

        boolean resultado = empleadoService.eliminar(99L);

        assertFalse(resultado);
        verify(empleadoRepository, times(1)).deleteById(99L);
    }

    @Test
    void eliminar_retornaTrue_cuandoEmpleadoExiste() {
        when(empleadoRepository.deleteById(1L)).thenReturn(true);

        boolean resultado = empleadoService.eliminar(1L);

        assertTrue(resultado);
        verify(empleadoRepository, times(1)).deleteById(1L);
    }

    @Test
    void crear_guardaEmpleado_cuandoDatosValidos() {
        Empleado empleado = new Empleado(null, "Pedro", "Reyes", "11111111-1",
                "Developer", new BigDecimal("800000"), new BigDecimal("200000"), new BigDecimal("50000"));
        Empleado empleadoGuardado = new Empleado(1L, "Pedro", "Reyes", "11111111-1",
                "Developer", new BigDecimal("800000"), new BigDecimal("200000"), new BigDecimal("50000"));

        when(empleadoRepository.save(empleado)).thenReturn(empleadoGuardado);

        Empleado resultado = empleadoService.crear(empleado);

        assertNotNull(resultado.getId());
        assertEquals(1L, resultado.getId());
        verify(empleadoValidator, times(1)).validar(empleado);
        verify(empleadoRepository, times(1)).save(empleado);
    }

}
