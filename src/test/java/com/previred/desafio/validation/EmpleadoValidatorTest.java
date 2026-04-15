package com.previred.desafio.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.previred.desafio.dto.ApiError;
import com.previred.desafio.dto.EmpleadoRequest;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para las reglas de validación de empleados.
 */
class EmpleadoValidatorTest {

    private EmpleadoValidator empleadoValidator;

    @BeforeEach
    void setUp() {
        empleadoValidator = new EmpleadoValidator();
    }

    /** Verifica que un request nulo sea rechazado. */
    @Test
    void shouldReturnRequiredFieldWhenRequestIsNull() {
        List<ApiError> errors = empleadoValidator.validate(null);

        assertThat(errors)
                .singleElement()
                .extracting(ApiError::getField, ApiError::getCode)
                .containsExactly("requestBody", "REQUIRED_FIELD");
    }

    /** Verifica que el RUT/DNI sea obligatorio. */
    @Test
    void shouldReturnRequiredFieldWhenRutDniIsBlank() {
        EmpleadoRequest request = validRequest();
        request.setRutDni(" ");

        List<ApiError> errors = empleadoValidator.validate(request);

        assertThat(errors)
                .extracting(ApiError::getField, ApiError::getCode)
                .contains(tuple("rutDni", "REQUIRED_FIELD"));
    }

    /** Verifica el formato del RUT/DNI. */
    @Test
    void shouldReturnInvalidFormatWhenRutDniIsInvalid() {
        EmpleadoRequest request = validRequest();
        request.setRutDni("abc");

        List<ApiError> errors = empleadoValidator.validate(request);

        assertThat(errors)
                .extracting(ApiError::getField, ApiError::getCode)
                .contains(tuple("rutDni", "INVALID_FORMAT"));
    }

    /** Verifica el salario base mínimo permitido. */
    @Test
    void shouldReturnMinSalaryWhenSalarioBaseIsLowerThanAllowed() {
        EmpleadoRequest request = validRequest();
        request.setSalarioBase(new BigDecimal("399999"));

        List<ApiError> errors = empleadoValidator.validate(request);

        assertThat(errors)
                .extracting(ApiError::getField, ApiError::getCode)
                .contains(tuple("salarioBase", "MIN_SALARY"));
    }

    /** Verifica que el bono no supere el 50% del salario base. */
    @Test
    void shouldReturnBonusExceedsLimitWhenBonoIsGreaterThanHalfSalary() {
        EmpleadoRequest request = validRequest();
        request.setBono(new BigDecimal("250001"));

        List<ApiError> errors = empleadoValidator.validate(request);

        assertThat(errors)
                .extracting(ApiError::getField, ApiError::getCode)
                .contains(tuple("bono", "BONUS_EXCEEDS_LIMIT"));
    }

    /** Verifica que los descuentos no excedan el salario base. */
    @Test
    void shouldReturnDiscountsExceedSalaryWhenDiscountsAreGreaterThanSalary() {
        EmpleadoRequest request = validRequest();
        request.setDescuentos(new BigDecimal("500001"));

        List<ApiError> errors = empleadoValidator.validate(request);

        assertThat(errors)
                .extracting(ApiError::getField, ApiError::getCode)
                .contains(tuple("descuentos", "DISCOUNTS_EXCEED_SALARY"));
    }

    /** Verifica que un request válido no produzca errores. */
    @Test
    void shouldReturnNoErrorsWhenRequestIsValid() {
        List<ApiError> errors = empleadoValidator.validate(validRequest());

        assertThat(errors).isEmpty();
    }

    private EmpleadoRequest validRequest() {
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
