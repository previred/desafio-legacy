package com.desafio.legacy.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.desafio.legacy.dto.EmpleadoRequest;
import com.desafio.legacy.dto.EmpleadoResponse;
import com.desafio.legacy.exception.BusinessValidationException;
import com.desafio.legacy.exception.ResourceNotFoundException;
import com.desafio.legacy.model.Empleado;
import com.desafio.legacy.repository.EmpleadoRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpleadoServiceImplTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private EmpleadoValidator empleadoValidator;

    @InjectMocks
    private EmpleadoServiceImpl empleadoService;

    @BeforeEach
    void setUp() {
        lenient().when(empleadoValidator.validate(any(EmpleadoRequest.class))).thenReturn(Collections.<String>emptyList());
    }

    @Test
    void shouldCreateEmployeeWhenRequestIsValid() {
        EmpleadoRequest request = buildValidRequest();

        Empleado saved = new Empleado(
            10L,
            "Ana",
            "Perez",
            "12345678-9",
            "Analista",
            new BigDecimal("500000"),
            new BigDecimal("50000"),
            new BigDecimal("20000")
        );

        when(empleadoRepository.existsByRutDni("12345678-9")).thenReturn(false);
        when(empleadoRepository.save(any(Empleado.class))).thenReturn(saved);

        EmpleadoResponse response = empleadoService.crearEmpleado(request);

        assertEquals(Long.valueOf(10L), response.getId());
        assertEquals("12345678-9", response.getRutDni());
        assertEquals(0, new BigDecimal("500000").compareTo(response.getSalarioBase()));
    }

    @Test
    void shouldRejectWhenRutDniAlreadyExists() {
        EmpleadoRequest request = buildValidRequest();

        when(empleadoRepository.existsByRutDni("12345678-9")).thenReturn(true);

        BusinessValidationException exception = assertThrows(
            BusinessValidationException.class,
            () -> empleadoService.crearEmpleado(request)
        );

        assertEquals("VALIDATION_ERROR", exception.getCode());
        assertTrue(exception.getDetails().contains("El RUT/DNI ya existe"));
    }

    @Test
    void shouldRejectWhenBusinessRulesAreInvalid() {
        EmpleadoRequest request = new EmpleadoRequest();
        request.setNombre("Ana");
        request.setApellido("Perez");
        request.setRutDni("12345678-9");
        request.setCargo("Analista");
        request.setSalarioBase(new BigDecimal("300000"));
        request.setBono(new BigDecimal("200000"));
        request.setDescuentos(new BigDecimal("400000"));

        when(empleadoValidator.validate(any(EmpleadoRequest.class))).thenReturn(
            Arrays.asList(
                "El salario base debe ser mayor o igual a 400000",
                "El bono no puede superar el 50% del salario base",
                "El total de descuentos no puede ser mayor al salario base"
            )
        );

        BusinessValidationException exception = assertThrows(
            BusinessValidationException.class,
            () -> empleadoService.crearEmpleado(request)
        );

        assertEquals("VALIDATION_ERROR", exception.getCode());
        assertTrue(
            exception.getDetails().containsAll(
                Arrays.asList(
                    "El salario base debe ser mayor o igual a 400000",
                    "El bono no puede superar el 50% del salario base",
                    "El total de descuentos no puede ser mayor al salario base"
                )
            )
        );
    }

    @Test
    void shouldReturnEmployeesWhenListing() {
        Empleado empleado = new Empleado(
            1L,
            "Ana",
            "Perez",
            "12345678-9",
            "Analista",
            new BigDecimal("500000"),
            new BigDecimal("50000"),
            new BigDecimal("20000")
        );

        when(empleadoRepository.findByFilters(null, null)).thenReturn(Collections.singletonList(empleado));

        assertEquals(1, empleadoService.obtenerEmpleados().size());
    }

    @Test
    void shouldDeleteEmployeeWhenIdExists() {
        when(empleadoRepository.deleteById(5L)).thenReturn(true);

        empleadoService.eliminarEmpleado(5L);

        verify(empleadoRepository).deleteById(5L);
    }

    @Test
    void shouldThrowNotFoundWhenDeletingUnknownId() {
        when(empleadoRepository.deleteById(99L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> empleadoService.eliminarEmpleado(99L)
        );

        assertEquals("EMPLEADO_NOT_FOUND", exception.getCode());
    }

    @Test
    void shouldTrimRutDniBeforeCheckingUniqueness() {
        EmpleadoRequest request = buildValidRequest();
        request.setRutDni(" 12345678-9 ");

        when(empleadoRepository.existsByRutDni("12345678-9")).thenReturn(true);

        assertThrows(BusinessValidationException.class, () -> empleadoService.crearEmpleado(request));

        verify(empleadoRepository).existsByRutDni(eq("12345678-9"));
    }

    private EmpleadoRequest buildValidRequest() {
        EmpleadoRequest request = new EmpleadoRequest();
        request.setNombre("Ana");
        request.setApellido("Perez");
        request.setRutDni("12345678-9");
        request.setCargo("Analista");
        request.setSalarioBase(new BigDecimal("500000"));
        request.setBono(new BigDecimal("50000"));
        request.setDescuentos(new BigDecimal("20000"));
        return request;
    }
}
