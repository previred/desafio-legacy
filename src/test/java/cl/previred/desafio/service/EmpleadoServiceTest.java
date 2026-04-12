package cl.previred.desafio.service;

import cl.previred.desafio.dto.EmpleadoRequest;
import cl.previred.desafio.dto.ValidationError;
import cl.previred.desafio.exception.ValidationExceptionList;
import cl.previred.desafio.model.Empleado;
import cl.previred.desafio.repository.EmpleadoRepositoryPort;
import cl.previred.desafio.service.EmpleadoValidator;
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
    private EmpleadoRepositoryPort empleadoRepository;

    @Mock
    private EmpleadoValidator empleadoValidator;

    private EmpleadoService empleadoService;

    @BeforeEach
    void setUp() {
        empleadoService = new EmpleadoService(empleadoRepository, empleadoValidator);
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

        Empleado empleadoGuardado = new Empleado();
        empleadoGuardado.setId(1L);
        empleadoGuardado.setNombre("Juan");
        empleadoGuardado.setApellido("Perez");
        empleadoGuardado.setRut("11111111-1");
        empleadoGuardado.setCargo("Desarrollador");
        empleadoGuardado.setSalario(new BigDecimal("500000"));

        doNothing().when(empleadoValidator).validate(request);
        when(empleadoRepository.save(any(Empleado.class))).thenReturn(empleadoGuardado);

        Empleado result = empleadoService.crearEmpleado(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Juan", result.getNombre());
        verify(empleadoRepository).save(any(Empleado.class));
    }

    @Test
    void crearEmpleadoConValidacionFallidaLanzaExcepcion() {
        EmpleadoRequest request = new EmpleadoRequest();
        request.setNombre("");
        request.setRut("INVALID");

        ValidationError error = new ValidationError("nombre", "El nombre es requerido");
        ValidationExceptionList ex = new ValidationExceptionList(Arrays.asList(error));
        doThrow(ex).when(empleadoValidator).validate(request);

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

        doNothing().when(empleadoValidator).validate(request);
        when(empleadoRepository.save(any(Empleado.class))).thenAnswer(i -> i.getArgument(0));

        empleadoService.crearEmpleado(request);

        verify(empleadoRepository).save(argThat(emp -> emp.getBono().equals(BigDecimal.ZERO)));
    }

    @Test
    void crearEmpleadoRetornaEmpleadoConIdAsignado() {
        EmpleadoRequest request = new EmpleadoRequest();
        request.setNombre("Juan");
        request.setApellido("Perez");
        request.setRut("11111111-1");
        request.setCargo("Desarrollador");
        request.setSalario(new BigDecimal("500000"));

        Empleado empleadoCreado = new Empleado();
        empleadoCreado.setId(42L);
        empleadoCreado.setNombre("Juan");
        empleadoCreado.setApellido("Perez");
        empleadoCreado.setRut("11111111-1");
        empleadoCreado.setCargo("Desarrollador");
        empleadoCreado.setSalario(new BigDecimal("500000"));
        empleadoCreado.setBono(BigDecimal.ZERO);
        empleadoCreado.setDescuentos(BigDecimal.ZERO);

        doNothing().when(empleadoValidator).validate(request);
        when(empleadoRepository.save(any(Empleado.class))).thenReturn(empleadoCreado);

        Empleado result = empleadoService.crearEmpleado(request);

        assertEquals(42L, result.getId());
        assertEquals("Juan", result.getNombre());
        assertEquals("Perez", result.getApellido());
        assertEquals("11111111-1", result.getRut());
    }

    @Test
    void eliminarEmpleadoDelegaAlRepositorio() {
        when(empleadoRepository.deleteById(1L)).thenReturn(true);

        boolean result = empleadoService.eliminarEmpleado(1L);

        assertTrue(result);
        verify(empleadoRepository).deleteById(1L);
    }

    @Test
    void crearEmpleadoConRutSinGuionPersisteEnFormatoCanonico() {
        EmpleadoRequest request = new EmpleadoRequest();
        request.setNombre("Juan");
        request.setApellido("Perez");
        request.setRut("19");
        request.setCargo("Desarrollador");
        request.setSalario(new BigDecimal("500000"));

        doNothing().when(empleadoValidator).validate(request);
        when(empleadoRepository.save(any(Empleado.class))).thenAnswer(i -> i.getArgument(0));

        empleadoService.crearEmpleado(request);

        verify(empleadoRepository).save(argThat(emp -> "1-9".equals(emp.getRut())));
    }

    @Test
    void crearEmpleadoConRutConPuntosPersisteEnFormatoCanonico() {
        EmpleadoRequest request = new EmpleadoRequest();
        request.setNombre("Juan");
        request.setApellido("Perez");
        request.setRut("12.345.678-5");
        request.setCargo("Desarrollador");
        request.setSalario(new BigDecimal("500000"));

        doNothing().when(empleadoValidator).validate(request);
        when(empleadoRepository.save(any(Empleado.class))).thenAnswer(i -> i.getArgument(0));

        empleadoService.crearEmpleado(request);

        verify(empleadoRepository).save(argThat(emp -> "12345678-5".equals(emp.getRut())));
    }
}
