package com.desafio.empleados.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RutChilenoValidator")
class RutChilenoValidatorTest {

    @Nested
    @DisplayName("normalizar")
    class Normalizar {

        @Test
        void nullDevuelveVacio() {
            assertThat(RutChilenoValidator.normalizar(null)).isEmpty();
        }

        @ParameterizedTest
        @CsvSource({
                "12.345.678-5, 12345678-5",
                "12345678-5, 12345678-5",
                "12345678-k, 12345678-K",
                "  11.111.111-1 , 11111111-1"
        })
        void quitaPuntosYDejaGuionYMayusculaK(String entrada, String esperado) {
            assertThat(RutChilenoValidator.normalizar(entrada)).isEqualTo(esperado);
        }
    }

    @Nested
    @DisplayName("esValido — RUTs correctos")
    class Validos {

        @ParameterizedTest(name = "[{index}] {0}")
        @ValueSource(strings = {
                "11.111.111-1",
                "11111111-1",
                "12.345.678-5",
                "12345678-5"
        })
        void rutConFormatoVariado(String rut) {
            assertThat(RutChilenoValidator.esValido(rut))
                    .as("debe validar DV para: %s", rut)
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("esValido — rechazos")
    class Invalidos {

        @ParameterizedTest
        @ValueSource(strings = {
                "12.345.678-0",
                "11111111-9",
                "123",
                "ABCDEFG"
        })
        void dvIncorrectoOCorto(String rut) {
            assertThat(RutChilenoValidator.esValido(rut)).isFalse();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        void vacioONulo(String rut) {
            assertThat(RutChilenoValidator.esValido(rut)).isFalse();
        }
    }

    @Nested
    @DisplayName("esFormatoPlausible")
    class Formato {

        @Test
        void normalizadoValidoEsPlausible() {
            assertThat(RutChilenoValidator.esFormatoPlausible("12345678-5")).isTrue();
        }

        @Test
        void demasiadoCortoNoEsPlausible() {
            assertThat(RutChilenoValidator.esFormatoPlausible("123-5")).isFalse();
        }
    }
}
