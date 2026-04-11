package cl.previred.desafio.service;

import cl.previred.desafio.dto.EmpleadoRequest;
import cl.previred.desafio.dto.ValidationError;
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
public class EmpleadoServiceTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private ValidationService validationService;

    private EmpleadoService empleadoService;

    @BeforeEach
    public void setUp() {
        empleadoService = new EmpleadoService(empleadoRepository, validationService);
    }

    @Test
    public void getAllEmpleados_delegatesToRepository() {
        Empleado emp = new Empleado();
        emp.setId(1L);
        when(empleadoRepository.findAll()).thenReturn(Arrays.asList(emp));

        List<Empleado> result = empleadoService.getAllEmpleados();

        assertEquals(1, result.size());
        verify(empleadoRepository).findAll();
    }

    @Test
    public void crearEmpleado_conValidacionExitosa_guardaEmpleado() {
        EmpleadoRequest request = new EmpleadoRequest();
        request.setNombre("Juan");
        request.setApellido("Perez");
        request.setRut("11111111-1");
        request.setCargo("Desarrollador");
        request.setSalario(new BigDecimal("500000"));

        when(validationService.validate(request)).thenReturn(Arrays.<ValidationError>asList());
        when(empleadoRepository.save(any(Empleado.class))).thenAnswer(i -> i.getArgument(0));

        List<ValidationError> errores = empleadoService.crearEmpleado(request);

        assertTrue(errores.isEmpty());
        verify(empleadoRepository).save(any(Empleado.class));
    }

    @Test
    public void crearEmpleado_conValidacionFallida_retornaErrores() {
        EmpleadoRequest request = new EmpleadoRequest();
        request.setNombre("");
        request.setRut("INVALID");

        ValidationError error = new ValidationError("nombre", "El nombre es requerido");
        when(validationService.validate(request)).thenReturn(Arrays.asList(error));

        List<ValidationError> errores = empleadoService.crearEmpleado(request);

        assertEquals(1, errores.size());
        assertEquals("nombre", errores.get(0).getCampo());
        verify(empleadoRepository, never()).save(any());
    }

    @Test
    public void crearEmpleado_conBonoNulo_asignaCero() {
        EmpleadoRequest request = new EmpleadoRequest();
        request.setNombre("Juan");
        request.setApellido("Perez");
        request.setRut("11111111-1");
        request.setCargo("Desarrollador");
        request.setSalario(new BigDecimal("500000"));
        request.setBono(null);

        when(validationService.validate(request)).thenReturn(Arrays.<ValidationError>asList());
        when(empleadoRepository.save(any(Empleado.class))).thenAnswer(i -> i.getArgument(0));

        empleadoService.crearEmpleado(request);

        verify(empleadoRepository).save(argThat(emp -> emp.getBono().equals(BigDecimal.ZERO)));
    }

    @Test
    public void eliminarEmpleado_delegatesToRepository() {
        when(empleadoRepository.deleteById(1L)).thenReturn(true);

        boolean result = empleadoService.eliminarEmpleado(1L);

        assertTrue(result);
        verify(empleadoRepository).deleteById(1L);
    }
}