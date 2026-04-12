package com.previred.empleados.service;

import com.previred.empleados.entity.EmpleadoEntity;
import com.previred.empleados.exception.BusinessValidationException;
import com.previred.empleados.exception.EmpleadoNotFoundException;
import com.previred.empleados.model.EmpleadoModel;
import com.previred.empleados.repository.IEmpleadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpleadoServiceTest {

    @Mock
    private IEmpleadoRepository empleadoRepository;

    @InjectMocks
    private EmpleadoService empleadoService;

    private EmpleadoModel modelValido;

    @BeforeEach
    void setUp() {
        modelValido = new EmpleadoModel();
        modelValido.setNombre("Juan");
        modelValido.setApellido("Pérez");
        modelValido.setRut("12345678-5");
        modelValido.setCargo("Desarrollador");
        modelValido.setSalarioBase(500000.0);
        modelValido.setBonos(100000.0);
        modelValido.setDescuentos(50000.0);
    }

    // --- listarEmpleados ---

    @Test
    void listarEmpleados_deberiaRetornarListaDeModels() {
        EmpleadoEntity entity = new EmpleadoEntity();
        entity.setId(1L);
        entity.setNombre("Juan");
        entity.setApellido("Pérez");
        entity.setRut("12345678-5");
        entity.setCargo("Dev");
        entity.setSalarioBase(500000.0);
        entity.setBonos(0.0);
        entity.setDescuentos(0.0);
        when(empleadoRepository.findAll()).thenReturn(Arrays.asList(entity));

        List<EmpleadoModel> resultado = empleadoService.listarEmpleados();

        assertEquals(1, resultado.size());
        assertEquals("Juan", resultado.get(0).getNombre());
        verify(empleadoRepository).findAll();
    }

    @Test
    void listarEmpleados_sinDatos_deberiaRetornarListaVacia() {
        when(empleadoRepository.findAll()).thenReturn(Collections.emptyList());

        List<EmpleadoModel> resultado = empleadoService.listarEmpleados();

        assertTrue(resultado.isEmpty());
    }

    // --- crearEmpleado ---

    @Test
    void crearEmpleado_conDatosValidos_deberiaCrear() {
        when(empleadoRepository.existsByRut(anyString())).thenReturn(false);
        EmpleadoEntity savedEntity = new EmpleadoEntity();
        savedEntity.setId(1L);
        savedEntity.setNombre("Juan");
        savedEntity.setApellido("Pérez");
        savedEntity.setRut("12345678-5");
        savedEntity.setCargo("Desarrollador");
        savedEntity.setSalarioBase(500000.0);
        savedEntity.setBonos(100000.0);
        savedEntity.setDescuentos(50000.0);
        when(empleadoRepository.save(any())).thenReturn(savedEntity);

        EmpleadoModel resultado = empleadoService.crearEmpleado(modelValido);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(empleadoRepository).save(any());
    }

    // --- Validaciones de campos obligatorios ---

    @Test
    void crearEmpleado_sinNombre_deberiaLanzarExcepcion() {
        modelValido.setNombre(null);

        BusinessValidationException ex = assertThrows(BusinessValidationException.class,
                () -> empleadoService.crearEmpleado(modelValido));

        assertTrue(ex.getErrores().stream().anyMatch(e -> e.contains("nombre")));
    }

    @Test
    void crearEmpleado_sinApellido_deberiaLanzarExcepcion() {
        modelValido.setApellido("");

        BusinessValidationException ex = assertThrows(BusinessValidationException.class,
                () -> empleadoService.crearEmpleado(modelValido));

        assertTrue(ex.getErrores().stream().anyMatch(e -> e.contains("apellido")));
    }

    @Test
    void crearEmpleado_sinRut_deberiaLanzarExcepcion() {
        modelValido.setRut(null);

        BusinessValidationException ex = assertThrows(BusinessValidationException.class,
                () -> empleadoService.crearEmpleado(modelValido));

        assertTrue(ex.getErrores().stream().anyMatch(e -> e.contains("RUT")));
    }

    @Test
    void crearEmpleado_sinCargo_deberiaLanzarExcepcion() {
        modelValido.setCargo("  ");

        BusinessValidationException ex = assertThrows(BusinessValidationException.class,
                () -> empleadoService.crearEmpleado(modelValido));

        assertTrue(ex.getErrores().stream().anyMatch(e -> e.contains("cargo")));
    }

    // --- Validación formato RUT ---

    @Test
    void crearEmpleado_conRutFormatoInvalido_deberiaLanzarExcepcion() {
        modelValido.setRut("123-456");

        BusinessValidationException ex = assertThrows(BusinessValidationException.class,
                () -> empleadoService.crearEmpleado(modelValido));

        assertTrue(ex.getErrores().stream().anyMatch(e -> e.contains("formato")));
    }

    // --- Validación RUT duplicado ---

    @Test
    void crearEmpleado_conRutDuplicado_deberiaLanzarExcepcion() {
        when(empleadoRepository.existsByRut("12345678-5")).thenReturn(true);

        BusinessValidationException ex = assertThrows(BusinessValidationException.class,
                () -> empleadoService.crearEmpleado(modelValido));

        assertTrue(ex.getErrores().stream().anyMatch(e -> e.contains("Ya existe")));
    }

    // --- Validación salario mínimo ---

    @Test
    void crearEmpleado_conSalarioMenorAlMinimo_deberiaLanzarExcepcion() {
        modelValido.setSalarioBase(300000.0);

        BusinessValidationException ex = assertThrows(BusinessValidationException.class,
                () -> empleadoService.crearEmpleado(modelValido));

        assertTrue(ex.getErrores().stream().anyMatch(e -> e.contains("salario base")));
    }

    @Test
    void crearEmpleado_conSalarioNulo_deberiaLanzarExcepcion() {
        modelValido.setSalarioBase(null);

        BusinessValidationException ex = assertThrows(BusinessValidationException.class,
                () -> empleadoService.crearEmpleado(modelValido));

        assertTrue(ex.getErrores().stream().anyMatch(e -> e.contains("salario base")));
    }

    // --- Validación bonos > 50% salario ---

    @Test
    void crearEmpleado_conBonosMayoresAl50Porciento_deberiaLanzarExcepcion() {
        modelValido.setSalarioBase(500000.0);
        modelValido.setBonos(300000.0); // 60% del salario

        BusinessValidationException ex = assertThrows(BusinessValidationException.class,
                () -> empleadoService.crearEmpleado(modelValido));

        assertTrue(ex.getErrores().stream().anyMatch(e -> e.contains("bonos")));
    }

    @Test
    void crearEmpleado_conBonosExactamente50Porciento_deberiaCrear() {
        modelValido.setBonos(250000.0); // exactamente 50%
        when(empleadoRepository.existsByRut(anyString())).thenReturn(false);
        EmpleadoEntity saved = new EmpleadoEntity();
        saved.setId(1L);
        saved.setNombre("Juan");
        saved.setApellido("Pérez");
        saved.setRut("12345678-5");
        saved.setCargo("Desarrollador");
        saved.setSalarioBase(500000.0);
        saved.setBonos(250000.0);
        saved.setDescuentos(50000.0);
        when(empleadoRepository.save(any())).thenReturn(saved);

        EmpleadoModel resultado = empleadoService.crearEmpleado(modelValido);

        assertNotNull(resultado);
    }

    // --- Validación descuentos > salario ---

    @Test
    void crearEmpleado_conDescuentosMayoresAlSalario_deberiaLanzarExcepcion() {
        modelValido.setSalarioBase(500000.0);
        modelValido.setDescuentos(600000.0);

        BusinessValidationException ex = assertThrows(BusinessValidationException.class,
                () -> empleadoService.crearEmpleado(modelValido));

        assertTrue(ex.getErrores().stream().anyMatch(e -> e.contains("descuentos")));
    }

    // --- eliminarEmpleado ---

    @Test
    void eliminarEmpleado_existente_deberiaEliminar() {
        when(empleadoRepository.deleteById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> empleadoService.eliminarEmpleado(1L));

        verify(empleadoRepository).deleteById(1L);
    }

    @Test
    void eliminarEmpleado_noExistente_deberiaLanzarExcepcion() {
        when(empleadoRepository.deleteById(99L)).thenReturn(false);

        assertThrows(EmpleadoNotFoundException.class,
                () -> empleadoService.eliminarEmpleado(99L));
    }

    // --- Múltiples errores ---

    @Test
    void crearEmpleado_conMultiplesErrores_deberiaRetornarTodos() {
        EmpleadoModel modelInvalido = new EmpleadoModel();

        BusinessValidationException ex = assertThrows(BusinessValidationException.class,
                () -> empleadoService.crearEmpleado(modelInvalido));

        assertTrue(ex.getErrores().size() >= 4);
    }
}
