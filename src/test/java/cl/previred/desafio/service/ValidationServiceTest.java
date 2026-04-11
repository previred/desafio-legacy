package cl.previred.desafio.service;

import cl.previred.desafio.dto.EmpleadoRequest;
import cl.previred.desafio.dto.ValidationError;
import cl.previred.desafio.exception.ValidationExceptionList;
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
    public void validarSalarioMinimoValidoNoLanzaExcepcion() {
        EmpleadoRequest request = new EmpleadoRequest();
        request.setNombre("Juan");
        request.setApellido("Perez");
        request.setRut("11111111-1");
        request.setCargo("Desarrollador");
        request.setSalario(new BigDecimal("400000"));
        request.setBono(new BigDecimal("100000"));
        request.setDescuentos(new BigDecimal("50000"));

        assertDoesNotThrow(() -> validationService.validate(request));
    }

    @Test
    public void validarSalarioMenor400MilLanzaExcepcionConError() {
        EmpleadoRequest request = new EmpleadoRequest();
        request.setNombre("Juan");
        request.setApellido("Perez");
        request.setRut("11111111-1");
        request.setCargo("Desarrollador");
        request.setSalario(new BigDecimal("399999"));

        ValidationExceptionList ex = assertThrows(ValidationExceptionList.class,
            () -> validationService.validate(request));

        List<ValidationError> errores = ex.getErrores();
        assertEquals(1, errores.size());
        assertEquals("salario", errores.get(0).getCampo());
    }

    @Test
    public void validarBonoSupera50PorcientoLanzaExcepcionConError() {
        EmpleadoRequest request = new EmpleadoRequest();
        request.setNombre("Juan");
        request.setApellido("Perez");
        request.setRut("11111111-1");
        request.setCargo("Desarrollador");
        request.setSalario(new BigDecimal("500000"));
        request.setBono(new BigDecimal("300000"));

        ValidationExceptionList ex = assertThrows(ValidationExceptionList.class,
            () -> validationService.validate(request));

        assertTrue(ex.getErrores().stream().anyMatch(e -> e.getCampo().equals("bono")));
    }

    @Test
    public void validarDescuentosSuperaSalarioLanzaExcepcionConError() {
        EmpleadoRequest request = new EmpleadoRequest();
        request.setNombre("Juan");
        request.setApellido("Perez");
        request.setRut("11111111-1");
        request.setCargo("Desarrollador");
        request.setSalario(new BigDecimal("400000"));
        request.setDescuentos(new BigDecimal("400001"));

        ValidationExceptionList ex = assertThrows(ValidationExceptionList.class,
            () -> validationService.validate(request));

        assertTrue(ex.getErrores().stream().anyMatch(e -> e.getCampo().equals("descuentos")));
    }

    @Test
    public void validarRutDuplicadoLanzaExcepcionConError() {
        EmpleadoRequest request = new EmpleadoRequest();
        request.setNombre("Juan");
        request.setApellido("Perez");
        request.setRut("11111111-1");
        request.setCargo("Desarrollador");
        request.setSalario(new BigDecimal("400000"));
        request.setBono(BigDecimal.ZERO);
        request.setDescuentos(BigDecimal.ZERO);

        when(empleadoRepository.existsByRut("11111111-1")).thenReturn(true);

        ValidationExceptionList ex = assertThrows(ValidationExceptionList.class,
            () -> validationService.validate(request));

        assertTrue(ex.getErrores().stream().anyMatch(e -> e.getCampo().equals("rut") && e.getMensaje().contains("ya existe")));
    }

    @Test
    public void validarCamposRequeridosFaltantesLanzaExcepcionConErrores() {
        EmpleadoRequest request = new EmpleadoRequest();

        ValidationExceptionList ex = assertThrows(ValidationExceptionList.class,
            () -> validationService.validate(request));

        assertTrue(ex.getErrores().size() >= 3);
    }
}
