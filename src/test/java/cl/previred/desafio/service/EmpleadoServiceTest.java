package cl.previred.desafio.service;

import cl.previred.desafio.dto.EmpleadoRequest;
import cl.previred.desafio.dto.ValidationError;
import cl.previred.desafio.exception.ValidationExceptionList;
import cl.previred.desafio.model.Empleado;
import cl.previred.desafio.repository.EmpleadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpleadoServiceTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private ValidationService validationService;

    private EmpleadoService empleadoService;

    @BeforeEach
    void setUp() {
        empleadoService = new EmpleadoService(empleadoRepository, validationService);
    }

    @Test
    void obtenerTodosLosEmpleadosDelegaAlRepositorio() {
        Empleado emp = new Empleado();
        emp.setId(1L);
        when(empleadoRepository.findAll()).thenReturn(Arrays.asList(emp));

        List<Empleado> result = empleadoService.getAllEmpleados();

        assertEquals(1, result.size());
        verify(empleadoRepository).findAll();
    }

    @Test
    void crearEmpleadoConValidacionExitosaCreaEmpleado() {
        EmpleadoRequest request = new EmpleadoRequest();
        request.setNombre("Juan");
        request.setApellido("Perez");
        request.setRut("11111111-1");
        request.setCargo("Desarrollador");
        request.setSalario(new BigDecimal("500000"));

        doNothing().when(validationService).validate(request);
        when(empleadoRepository.save(any(Empleado.class))).thenAnswer(i -> i.getArgument(0));

        assertDoesNotThrow(() -> empleadoService.crearEmpleado(request));
        verify(empleadoRepository).save(any(Empleado.class));
    }

    @Test
    void crearEmpleadoConValidacionFallidaLanzaExcepcion() {
        EmpleadoRequest request = new EmpleadoRequest();
        request.setNombre("");
        request.setRut("INVALID");

        ValidationError error = new ValidationError("nombre", "El nombre es requerido");
        ValidationExceptionList ex = new ValidationExceptionList(Arrays.asList(error));
        doThrow(ex).when(validationService).validate(request);

        ValidationExceptionList thrown = assertThrows(ValidationExceptionList.class,
            () -> empleadoService.crearEmpleado(request));

        assertEquals(1, thrown.getErrores().size());
        assertEquals("nombre", thrown.getErrores().get(0).getCampo());
        verify(empleadoRepository, never()).save(any());
    }

    @Test
    void crearEmpleadoConBonoNuloAsignaCero() {
        EmpleadoRequest request = new EmpleadoRequest();
        request.setNombre("Juan");
        request.setApellido("Perez");
        request.setRut("11111111-1");
        request.setCargo("Desarrollador");
        request.setSalario(new BigDecimal("500000"));
        request.setBono(null);

        doNothing().when(validationService).validate(request);
        when(empleadoRepository.save(any(Empleado.class))).thenAnswer(i -> i.getArgument(0));

        empleadoService.crearEmpleado(request);

        verify(empleadoRepository).save(argThat(emp -> emp.getBono().equals(BigDecimal.ZERO)));
    }

    @Test
    void eliminarEmpleadoDelegaAlRepositorio() {
        when(empleadoRepository.deleteById(1L)).thenReturn(true);

        boolean result = empleadoService.eliminarEmpleado(1L);

        assertTrue(result);
        verify(empleadoRepository).deleteById(1L);
    }
}
