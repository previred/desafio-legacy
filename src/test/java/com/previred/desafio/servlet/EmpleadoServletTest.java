package com.previred.desafio.servlet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.previred.desafio.dto.ApiError;
import com.previred.desafio.dto.EmpleadoRequest;
import com.previred.desafio.exception.BusinessException;
import com.previred.desafio.model.Empleado;
import com.previred.desafio.service.EmpleadoService;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Pruebas unitarias del contrato HTTP expuesto por el servlet.
 */
@ExtendWith(MockitoExtension.class)
class EmpleadoServletTest {

    @Mock
    private EmpleadoService empleadoService;

    private ObjectMapper objectMapper;
    private EmpleadoServlet empleadoServlet;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        empleadoServlet = new EmpleadoServlet(empleadoService, objectMapper);
    }

    /** Verifica que GET responda 200 y serialice empleados. */
    @Test
    void shouldReturnOkWithEmployeesOnGet() throws Exception {
        when(empleadoService.findAll()).thenReturn(Arrays.asList(
                new Empleado(1L, "Ana", "Perez", "12345678-9", "Analista",
                        new BigDecimal("500000"), new BigDecimal("50000"), new BigDecimal("20000"))
        ));

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        empleadoServlet.doGet(request, response);

        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);
        JsonNode body = objectMapper.readTree(response.getContentAsString());
        assertThat(body.isArray()).isTrue();
        assertThat(body.get(0).get("nombre").asText()).isEqualTo("Ana");
    }

    /** Verifica que POST válido responda 201. */
    @Test
    void shouldReturnCreatedOnValidPost() throws Exception {
        Empleado created = new Empleado(10L, "Ana", "Perez", "12345678-9", "Analista",
                new BigDecimal("500000"), new BigDecimal("50000"), new BigDecimal("20000"));
        when(empleadoService.create(org.mockito.ArgumentMatchers.any(EmpleadoRequest.class))).thenReturn(created);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("application/json");
        request.setContent(("{\"nombre\":\"Ana\",\"apellido\":\"Perez\",\"rutDni\":\"12345678-9\","
                + "\"cargo\":\"Analista\",\"salarioBase\":500000,\"bono\":50000,\"descuentos\":20000}")
                .getBytes());
        MockHttpServletResponse response = new MockHttpServletResponse();

        empleadoServlet.doPost(request, response);

        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_CREATED);
        JsonNode body = objectMapper.readTree(response.getContentAsString());
        assertThat(body.get("id").asLong()).isEqualTo(10L);
    }

    /** Verifica que un error de negocio responda 400 con error tipado. */
    @Test
    void shouldReturnBadRequestWhenBusinessExceptionOccursOnPost() throws Exception {
        when(empleadoService.create(org.mockito.ArgumentMatchers.any(EmpleadoRequest.class)))
                .thenThrow(new BusinessException(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Validation failed",
                        Collections.singletonList(ApiError.of("rutDni", "DUPLICATE_RUT_DNI", "El RUT/DNI ya existe"))
                ));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("application/json");
        request.setContent(("{\"nombre\":\"Ana\",\"apellido\":\"Perez\",\"rutDni\":\"12345678-9\","
                + "\"cargo\":\"Analista\",\"salarioBase\":500000,\"bono\":50000,\"descuentos\":20000}")
                .getBytes());
        MockHttpServletResponse response = new MockHttpServletResponse();

        empleadoServlet.doPost(request, response);

        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
        JsonNode body = objectMapper.readTree(response.getContentAsString());
        assertThat(body.get("errors").get(0).get("code").asText()).isEqualTo("DUPLICATE_RUT_DNI");
    }

    /** Verifica que un JSON inválido responda 400. */
    @Test
    void shouldReturnBadRequestWhenJsonIsInvalidOnPost() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("application/json");
        request.setContent("{invalid-json}".getBytes());
        MockHttpServletResponse response = new MockHttpServletResponse();

        empleadoServlet.doPost(request, response);

        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
        JsonNode body = objectMapper.readTree(response.getContentAsString());
        assertThat(body.get("errors").get(0).get("code").asText()).isEqualTo("INVALID_JSON");
    }

    /** Verifica que DELETE rechace ids no numéricos. */
    @Test
    void shouldReturnBadRequestWhenDeleteReceivesInvalidId() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("id", "abc");
        MockHttpServletResponse response = new MockHttpServletResponse();

        empleadoServlet.doDelete(request, response);

        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_BAD_REQUEST);
        JsonNode body = objectMapper.readTree(response.getContentAsString());
        assertThat(body.get("errors").get(0).get("code").asText()).isEqualTo("INVALID_ID");
    }
}
