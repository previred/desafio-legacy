package com.previred.desafio.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.previred.desafio.dto.ApiError;
import com.previred.desafio.dto.EmpleadoRequest;
import com.previred.desafio.exception.BusinessException;
import com.previred.desafio.model.Empleado;
import com.previred.desafio.repository.EmpleadoRepository;
import com.previred.desafio.validation.EmpleadoValidator;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias para la orquestación de reglas en el servicio.
 */
@ExtendWith(MockitoExtension.class)
class EmpleadoServiceTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private EmpleadoValidator empleadoValidator;

    @InjectMocks
    private EmpleadoService empleadoService;

    private EmpleadoRequest request;

    @BeforeEach
    void setUp() {
        request = validRequest();
    }

    /** Verifica que el servicio propague errores del validador. */
    @Test
    void shouldThrowBusinessExceptionWhenValidatorReturnsErrors() {
        List<ApiError> validationErrors = Collections.singletonList(
                ApiError.of("nombre", "REQUIRED_FIELD", "El nombre es obligatorio")
        );
        when(empleadoValidator.validate(request)).thenReturn(validationErrors);

        assertThatThrownBy(() -> empleadoService.create(request))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> {
                    BusinessException businessException = (BusinessException) exception;
                    assertThat(businessException.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
                    assertThat(businessException.getErrors()).containsExactlyElementsOf(validationErrors);
                });

        verify(empleadoRepository, never()).save(any(Empleado.class));
    }

    /** Verifica el rechazo por RUT/DNI duplicado. */
    @Test
    void shouldThrowBusinessExceptionWhenRutDniAlreadyExists() {
        when(empleadoValidator.validate(request)).thenReturn(Collections.emptyList());
        when(empleadoRepository.existsByRutDni("12345678-9")).thenReturn(true);

        assertThatThrownBy(() -> empleadoService.create(request))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> {
                    BusinessException businessException = (BusinessException) exception;
                    assertThat(businessException.getErrors())
                            .extracting(ApiError::getCode)
                            .containsExactly("DUPLICATE_RUT_DNI");
                });
    }

    /** Verifica el guardado exitoso cuando el request es válido. */
    @Test
    void shouldSaveAndReturnEmpleadoWhenRequestIsValid() {
        Empleado persisted = new Empleado(1L, "Ana", "Perez", "12345678-9", "Analista",
                new BigDecimal("500000"), new BigDecimal("50000"), new BigDecimal("20000"));

        when(empleadoValidator.validate(request)).thenReturn(Collections.emptyList());
        when(empleadoRepository.existsByRutDni("12345678-9")).thenReturn(false);
        when(empleadoRepository.save(any(Empleado.class))).thenReturn(persisted);

        Empleado result = empleadoService.create(request);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getRutDni()).isEqualTo("12345678-9");
        verify(empleadoRepository).save(any(Empleado.class));
    }

    /** Verifica que el id inválido sea rechazado al eliminar. */
    @Test
    void shouldThrowBusinessExceptionWhenDeleteIdIsInvalid() {
        assertThatThrownBy(() -> empleadoService.deleteById(0L))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> {
                    BusinessException businessException = (BusinessException) exception;
                    assertThat(businessException.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
                    assertThat(businessException.getErrors())
                            .extracting(ApiError::getCode)
                            .containsExactly("INVALID_ID");
                });
    }

    /** Verifica el caso en que no existe el empleado a eliminar. */
    @Test
    void shouldThrowBusinessExceptionWhenDeleteDoesNotAffectRows() {
        when(empleadoRepository.deleteById(99L)).thenReturn(false);

        assertThatThrownBy(() -> empleadoService.deleteById(99L))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> {
                    BusinessException businessException = (BusinessException) exception;
                    assertThat(businessException.getStatus()).isEqualTo(HttpServletResponse.SC_NOT_FOUND);
                    assertThat(businessException.getErrors())
                            .extracting(ApiError::getCode)
                            .containsExactly("EMPLOYEE_NOT_FOUND");
                });
    }

    private EmpleadoRequest validRequest() {
        EmpleadoRequest empleadoRequest = new EmpleadoRequest();
        empleadoRequest.setNombre(" Ana ");
        empleadoRequest.setApellido(" Perez ");
        empleadoRequest.setRutDni("12345678-9");
        empleadoRequest.setCargo(" Analista ");
        empleadoRequest.setSalarioBase(new BigDecimal("500000"));
        empleadoRequest.setBono(new BigDecimal("50000"));
        empleadoRequest.setDescuentos(new BigDecimal("20000"));
        return empleadoRequest;
    }
}
