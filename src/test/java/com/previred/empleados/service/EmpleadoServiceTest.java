package com.previred.empleados.service;

import com.previred.empleados.dao.EmpleadoDAO;
import com.previred.empleados.dto.EmpleadoDTO;
import com.previred.empleados.dto.EmpleadoRequestDTO;
import com.previred.empleados.exception.DuplicateRutException;
import com.previred.empleados.exception.ValidationException;
import com.previred.empleados.model.Empleado;
import com.previred.empleados.service.impl.EmpleadoServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EmpleadoServiceTest {

    private EmpleadoService empleadoService;
    private EmpleadoDAO empleadoDAO;

    @Before
    public void setUp() {
        empleadoDAO = mock(EmpleadoDAO.class);
        empleadoService = new EmpleadoServiceImpl(empleadoDAO);
    }

    @Test
    public void testGetAllEmpleados() {
        Empleado empleado1 = createEmpleado(1L, "Juan", "Pérez", "12.345.678-5",
                new BigDecimal("1000000"), new BigDecimal("200000"), new BigDecimal("50000"));
        Empleado empleado2 = createEmpleado(2L, "María", "González", "23.456.789-6",
                new BigDecimal("1500000"), new BigDecimal("300000"), new BigDecimal("100000"));

        when(empleadoDAO.findAll()).thenReturn(Arrays.asList(empleado1, empleado2));

        List<EmpleadoDTO> empleados = empleadoService.getAllEmpleados();

        assertEquals(2, empleados.size());
        assertEquals("Juan", empleados.get(0).getNombre());
        assertEquals("María", empleados.get(1).getNombre());
        assertEquals(new BigDecimal("1150000"), empleados.get(0).getSalarioTotal());
        verify(empleadoDAO, times(1)).findAll();
    }

    @Test
    public void testCreateEmpleadoExitoso() {
        EmpleadoRequestDTO request = createRequest("Juan", "Pérez", "12.345.678-5",
                new BigDecimal("1000000"), new BigDecimal("200000"), new BigDecimal("50000"));

        when(empleadoDAO.existsByRut(anyString())).thenReturn(false);
        when(empleadoDAO.save(any(Empleado.class))).thenAnswer(invocation -> {
            Empleado empleado = invocation.getArgument(0);
            empleado.setId(1L);
            return empleado;
        });

        EmpleadoDTO result = empleadoService.createEmpleado(request);

        assertNotNull(result);
        assertEquals("Juan", result.getNombre());
        assertEquals("12.345.678-5", result.getRut());
        assertEquals(new BigDecimal("1150000"), result.getSalarioTotal());
        verify(empleadoDAO, times(1)).save(any(Empleado.class));
    }

    @Test(expected = DuplicateRutException.class)
    public void testCreateEmpleadoRutDuplicado() {
        EmpleadoRequestDTO request = createRequest("Juan", "Pérez", "12.345.678-5",
                new BigDecimal("1000000"), new BigDecimal("200000"), new BigDecimal("50000"));

        when(empleadoDAO.existsByRut("12.345.678-5")).thenReturn(true);

        empleadoService.createEmpleado(request);
    }

    @Test(expected = ValidationException.class)
    public void testCreateEmpleadoSalarioInvalido() {
        EmpleadoRequestDTO request = createRequest("Juan", "Pérez", "12.345.678-5",
                new BigDecimal("300000"), new BigDecimal("0"), new BigDecimal("0"));

        empleadoService.createEmpleado(request);
    }

    @Test(expected = ValidationException.class)
    public void testCreateEmpleadoBonosExcesivos() {
        EmpleadoRequestDTO request = createRequest("Juan", "Pérez", "12.345.678-5",
                new BigDecimal("1000000"), new BigDecimal("600000"), new BigDecimal("0"));

        empleadoService.createEmpleado(request);
    }

    @Test(expected = ValidationException.class)
    public void testCreateEmpleadoRutInvalido() {
        EmpleadoRequestDTO request = createRequest("Juan", "Pérez", "12.345.678-0",
                new BigDecimal("1000000"), new BigDecimal("200000"), new BigDecimal("0"));

        empleadoService.createEmpleado(request);
    }

    @Test
    public void testNormalizacionRut() {
        EmpleadoRequestDTO request = createRequest("Juan", "Pérez", "12345678-5",
                new BigDecimal("1000000"), new BigDecimal("200000"), new BigDecimal("50000"));

        when(empleadoDAO.existsByRut(anyString())).thenReturn(false);
        when(empleadoDAO.save(any(Empleado.class))).thenAnswer(invocation -> {
            Empleado empleado = invocation.getArgument(0);
            empleado.setId(1L);
            return empleado;
        });

        EmpleadoDTO result = empleadoService.createEmpleado(request);

        ArgumentCaptor<Empleado> captor = ArgumentCaptor.forClass(Empleado.class);
        verify(empleadoDAO).save(captor.capture());

        assertEquals("12.345.678-5", captor.getValue().getRut());
        assertEquals("12.345.678-5", result.getRut());
    }

    @Test
    public void testDeleteEmpleado() {
        empleadoService.deleteEmpleado(1L);
        verify(empleadoDAO, times(1)).deleteById(1L);
    }

    private Empleado createEmpleado(Long id, String nombre, String apellido, String rut,
                                     BigDecimal salarioBase, BigDecimal bonos, BigDecimal descuentos) {
        Empleado empleado = new Empleado(nombre, apellido, rut, "Cargo", salarioBase, bonos, descuentos);
        empleado.setId(id);
        return empleado;
    }

    private EmpleadoRequestDTO createRequest(String nombre, String apellido, String rut,
                                              BigDecimal salarioBase, BigDecimal bonos, BigDecimal descuentos) {
        EmpleadoRequestDTO request = new EmpleadoRequestDTO();
        request.setNombre(nombre);
        request.setApellido(apellido);
        request.setRut(rut);
        request.setCargo("Desarrollador");
        request.setSalarioBase(salarioBase);
        request.setBonos(bonos);
        request.setDescuentos(descuentos);
        return request;
    }
}
