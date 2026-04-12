package com.empleados.service;

import com.empleados.api.generated.model.EmpleadoResponse;
import com.empleados.exception.business.RutDuplicadoException;
import com.empleados.mapper.EmpleadoMapper;
import com.empleados.model.Empleado;
import com.empleados.repository.EmpleadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmpleadoServiceTest {

    private EmpleadoRepository repository;
    private EmpleadoService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(EmpleadoRepository.class);
        EmpleadoMapper mapper = new EmpleadoMapper();
        service = new EmpleadoService(repository, mapper);
    }

    private Empleado empleadoValido() {
        return Empleado.builder()
            .nombre("Juan").apellido("Perez")
            .rut("12.345.678-5").cargo("Desarrollador")
            .salario(500000L).bono(100000L).descuentos(50000L)
            .build();
    }

    @Nested
    @DisplayName("Listar empleados")
    class ListarTests {

        @Test
        @DisplayName("Debe retornar lista vacia cuando no hay empleados")
        void listarVacio() {
            when(repository.findAll()).thenReturn(List.of());
            assertTrue(service.listarEmpleados().isEmpty());
        }

        @Test
        @DisplayName("Debe retornar EmpleadoResponse con salarioLiquido calculado")
        void listarConDatos() {
            when(repository.findAll()).thenReturn(Arrays.asList(empleadoValido()));

            List<EmpleadoResponse> result = service.listarEmpleados();
            assertEquals(1, result.size());
            assertEquals(550000L, result.get(0).getSalarioLiquido());
        }
    }

    @Nested
    @DisplayName("Crear empleado")
    class CrearTests {

        @Test
        @DisplayName("Debe crear empleado valido y retornar EmpleadoResponse")
        void crearValido() {
            Empleado guardado = empleadoValido();
            guardado.setId(1L);

            when(repository.existsByRut("12.345.678-5")).thenReturn(false);
            when(repository.save(any(Empleado.class))).thenReturn(guardado);

            EmpleadoResponse resp = service.crearEmpleado(empleadoValido());
            assertEquals(1L, resp.getId());
            assertEquals("Juan", resp.getNombre());
            assertEquals(550000L, resp.getSalarioLiquido());
        }

        @Test
        @DisplayName("Debe rechazar RUT duplicado")
        void rutDuplicado() {
            when(repository.existsByRut("12.345.678-5")).thenReturn(true);

            RutDuplicadoException ex = assertThrows(
                RutDuplicadoException.class,
                () -> service.crearEmpleado(empleadoValido())
            );
            assertTrue(ex.getMessage().contains("Ya existe"));
        }
    }

    @Nested
    @DisplayName("Eliminar empleado")
    class EliminarTests {

        @Test
        @DisplayName("Debe retornar true al eliminar existente")
        void eliminarExistente() {
            when(repository.existsById(1L)).thenReturn(true);
            assertTrue(service.eliminarEmpleado(1L));
            verify(repository).deleteById(1L);
        }

        @Test
        @DisplayName("Debe retornar false al eliminar inexistente")
        void eliminarInexistente() {
            when(repository.existsById(999L)).thenReturn(false);
            assertFalse(service.eliminarEmpleado(999L));
            verify(repository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("Buscar por ID")
    class BuscarTests {

        @Test
        @DisplayName("Debe encontrar empleado existente")
        void buscarExistente() {
            Empleado e = empleadoValido();
            e.setId(1L);
            when(repository.findById(1L)).thenReturn(Optional.of(e));
            assertTrue(service.buscarPorId(1L).isPresent());
        }

        @Test
        @DisplayName("Debe retornar vacio si no existe")
        void buscarInexistente() {
            when(repository.findById(999L)).thenReturn(Optional.empty());
            assertFalse(service.buscarPorId(999L).isPresent());
        }
    }
}
