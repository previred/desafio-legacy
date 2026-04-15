package com.previred.desafiolegacy.service;

import com.previred.desafiolegacy.model.Empleado;
import com.previred.desafiolegacy.repository.EmpleadoRepository;
import com.previred.desafiolegacy.validator.EmpleadoValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
                "Developer", 800000, 100000, 50000);
        Empleado ana = new Empleado(2L, "Ana", "Fernandez", "22222222-2",
                "Designer", 700000, 50000, 30000);
        when(empleadoRepository.findAll()).thenReturn(Arrays.asList(pedro, ana));

        List<Empleado> resultado = empleadoService.obtenerTodos();

        assertEquals(2, resultado.size());
        assertEquals("Ana", resultado.get(0).getNombre());
        assertEquals("Pedro", resultado.get(1).getNombre());
    }

    @Test
    void eliminar_retornaFalse_cuandoEmpleadoNoExiste() {
        when(empleadoRepository.findById(99L)).thenReturn(Optional.empty());

        boolean resultado = empleadoService.eliminar(99L);

        assertFalse(resultado);
        verify(empleadoRepository, never()).deleteById(99L);
    }

    @Test
    void eliminar_retornaTrue_cuandoEmpleadoExiste() {
        Empleado empleado = new Empleado(1L, "Ana", "Fernandez", "22222222-2",
                "Designer", 700000, 50000, 30000);
        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleado));
        when(empleadoRepository.deleteById(1L)).thenReturn(true);

        boolean resultado = empleadoService.eliminar(1L);

        assertTrue(resultado);
        verify(empleadoRepository, times(1)).deleteById(1L);
    }

    @Test
    void crear_guardaEmpleado_cuandoDatosValidos() {
        Empleado empleado = new Empleado(null, "Ana", "Fernandez", "22222222-2",
                "Developer", 800000, 200000, 50000);
        Empleado empleadoGuardado = new Empleado(1L, "Ana", "Fernandez", "22222222-2",
                "Developer", 800000, 200000, 50000);

        when(empleadoRepository.save(empleado)).thenReturn(empleadoGuardado);

        Empleado resultado = empleadoService.crear(empleado);

        assertNotNull(resultado.getId());
        assertEquals(1L, resultado.getId());
        verify(empleadoRepository, times(1)).save(empleado);
    }

}
