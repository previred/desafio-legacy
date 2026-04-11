package cl.previred.desafio.service;

import cl.previred.desafio.dto.EmpleadoRequest;
import cl.previred.desafio.dto.ValidationError;
import cl.previred.desafio.repository.EmpleadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ValidationServiceTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    private ValidationService validationService;

    @BeforeEach
    public void setUp() {
        validationService = new ValidationService(empleadoRepository);
    }

    @Test
    public void validate_salarioMinimoValido_retornaSinErrores() {
        EmpleadoRequest request = new EmpleadoRequest();
        request.setNombre("Juan");
        request.setApellido("Perez");
        request.setRut("11111111-1");
        request.setCargo("Desarrollador");
        request.setSalario(new BigDecimal("400000"));
        request.setBono(new BigDecimal("100000"));
        request.setDescuentos(new BigDecimal("50000"));

        List<ValidationError> errores = validationService.validate(request);

        assertTrue(errores.isEmpty(), "No deberia haber errores con salario minimo valido");
    }

    @Test
    public void validate_salarioMenor400Mil_retornaError() {
        EmpleadoRequest request = new EmpleadoRequest();
        request.setNombre("Juan");
        request.setApellido("Perez");
        request.setRut("11111111-1");
        request.setCargo("Desarrollador");
        request.setSalario(new BigDecimal("399999"));

        List<ValidationError> errores = validationService.validate(request);

        assertEquals(1, errores.size());
        assertEquals("salario", errores.get(0).getCampo());
    }

    @Test
    public void validate_bonoSupera50Porciento_retornaError() {
        EmpleadoRequest request = new EmpleadoRequest();
        request.setNombre("Juan");
        request.setApellido("Perez");
        request.setRut("11111111-1");
        request.setCargo("Desarrollador");
        request.setSalario(new BigDecimal("500000"));
        request.setBono(new BigDecimal("300000"));

        List<ValidationError> errores = validationService.validate(request);

        assertTrue(errores.stream().anyMatch(e -> e.getCampo().equals("bono")));
    }

    @Test
    public void validate_descuentosSuperaSalario_retornaError() {
        EmpleadoRequest request = new EmpleadoRequest();
        request.setNombre("Juan");
        request.setApellido("Perez");
        request.setRut("11111111-1");
        request.setCargo("Desarrollador");
        request.setSalario(new BigDecimal("400000"));
        request.setDescuentos(new BigDecimal("400001"));

        List<ValidationError> errores = validationService.validate(request);

        assertTrue(errores.stream().anyMatch(e -> e.getCampo().equals("descuentos")));
    }

    @Test
    public void validate_rutDuplicado_retornaError() {
        EmpleadoRequest request = new EmpleadoRequest();
        request.setNombre("Juan");
        request.setApellido("Perez");
        request.setRut("11111111-1");
        request.setCargo("Desarrollador");
        request.setSalario(new BigDecimal("400000"));
        request.setBono(BigDecimal.ZERO);
        request.setDescuentos(BigDecimal.ZERO);

        when(empleadoRepository.existsByRut("11111111-1")).thenReturn(true);

        List<ValidationError> errores = validationService.validate(request);

        assertTrue(errores.stream().anyMatch(e -> e.getCampo().equals("rut") && e.getMensaje().contains("ya existe")));
    }

    @Test
    public void validate_camposRequeridosFaltantes_retornaErrores() {
        EmpleadoRequest request = new EmpleadoRequest();

        List<ValidationError> errores = validationService.validate(request);

        assertTrue(errores.size() >= 3);
    }
}