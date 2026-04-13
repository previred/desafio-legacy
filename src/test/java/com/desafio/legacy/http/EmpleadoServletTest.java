package com.desafio.legacy.http;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import com.desafio.legacy.dto.EmpleadoRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmpleadoServletTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM empleados");
    }

    @Test
    void shouldReturnEmployeeListWithGetEndpoint() throws Exception {
        insertEmpleado("11111111-1");

        ResponseEntity<String> response = restTemplate.getForEntity(url("/api/empleados"), String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JsonNode root = objectMapper.readTree(response.getBody());
        assertTrue(root.isArray());
        assertEquals(1, root.size());
        assertEquals("11111111-1", root.get(0).get("rutDni").asText());
    }

    @Test
    void shouldFilterEmployeeListByQueryParams() throws Exception {
        insertEmpleado("11111111-1");
        jdbcTemplate.update(
            "INSERT INTO empleados (nombre, apellido, rut_dni, cargo, salario_base, bono, descuentos) VALUES (?, ?, ?, ?, ?, ?, ?)",
            "Luis",
            "Soto",
            "22222222-2",
            "Developer",
            new BigDecimal("700000"),
            new BigDecimal("50000"),
            new BigDecimal("10000")
        );

        ResponseEntity<String> response = restTemplate.getForEntity(url("/api/empleados?q=luis&cargo=dev"), String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JsonNode root = objectMapper.readTree(response.getBody());
        assertTrue(root.isArray());
        assertEquals(1, root.size());
        assertEquals("22222222-2", root.get(0).get("rutDni").asText());
    }

    @Test
    void shouldCreateEmployeeWithPostEndpoint() throws Exception {
        EmpleadoRequest request = buildValidRequest("12345678-9");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.postForEntity(
            url("/api/empleados"),
            new HttpEntity<EmpleadoRequest>(request, headers),
            String.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        JsonNode created = objectMapper.readTree(response.getBody());
        assertNotNull(created.get("id"));
        assertEquals("12345678-9", created.get("rutDni").asText());
    }

    @Test
    void shouldReturnBadRequestWhenPostBreaksBusinessRules() throws Exception {
        EmpleadoRequest request = buildValidRequest("23456789-0");
        request.setSalarioBase(new BigDecimal("300000"));
        request.setBono(new BigDecimal("200000"));
        request.setDescuentos(new BigDecimal("400000"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.postForEntity(
            url("/api/empleados"),
            new HttpEntity<EmpleadoRequest>(request, headers),
            String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        JsonNode error = objectMapper.readTree(response.getBody());
        assertEquals("VALIDATION_ERROR", error.get("code").asText());
        assertTrue(error.get("details").isArray());
        assertFalse(error.get("details").isEmpty());
    }

    @Test
    void shouldReturnBadRequestWhenRutDniAlreadyExists() throws Exception {
        insertEmpleado("12345678-9");

        EmpleadoRequest request = buildValidRequest("12345678-9");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.postForEntity(
            url("/api/empleados"),
            new HttpEntity<EmpleadoRequest>(request, headers),
            String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        JsonNode error = objectMapper.readTree(response.getBody());
        assertEquals("VALIDATION_ERROR", error.get("code").asText());
        assertTrue(error.get("details").toString().contains("El RUT/DNI ya existe"));
    }

    @Test
    void shouldCreateEmployeeWhenBonoAndDescuentosAreNull() throws Exception {
        EmpleadoRequest request = buildValidRequest("44444444-4");
        request.setBono(null);
        request.setDescuentos(null);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.postForEntity(
            url("/api/empleados"),
            new HttpEntity<EmpleadoRequest>(request, headers),
            String.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        JsonNode created = objectMapper.readTree(response.getBody());
        assertEquals(0.0d, created.get("bono").asDouble(), 0.0001d);
        assertEquals(0.0d, created.get("descuentos").asDouble(), 0.0001d);
    }

    @Test
    void shouldReturnBadRequestWhenJsonIsMalformed() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.postForEntity(
            url("/api/empleados"),
            new HttpEntity<String>("{\"nombre\":\"Ana\"", headers),
            String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        JsonNode error = objectMapper.readTree(response.getBody());
        assertEquals("INVALID_JSON", error.get("code").asText());
    }

    @Test
    void shouldReturnBadRequestWhenRutDniHasInvalidFormat() throws Exception {
        EmpleadoRequest request = buildValidRequest(" 1234 ");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = restTemplate.postForEntity(
            url("/api/empleados"),
            new HttpEntity<EmpleadoRequest>(request, headers),
            String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        JsonNode error = objectMapper.readTree(response.getBody());
        assertEquals("VALIDATION_ERROR", error.get("code").asText());
        assertTrue(error.get("details").toString().contains("El formato de RUT/DNI es invalido"));
    }

    @Test
    void shouldDeleteEmployeeWithDeleteEndpoint() {
        Long id = insertEmpleado("34567890-1");

        ResponseEntity<Void> response = restTemplate.exchange(
            url("/api/empleados/" + id),
            HttpMethod.DELETE,
            null,
            Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(1) FROM empleados WHERE id = ?", Integer.class, id);
        assertEquals(Integer.valueOf(0), count);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingUnknownEmployee() throws Exception {
        ResponseEntity<String> response = restTemplate.exchange(
            url("/api/empleados/9999"),
            HttpMethod.DELETE,
            null,
            String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        JsonNode error = objectMapper.readTree(response.getBody());
        assertEquals("EMPLEADO_NOT_FOUND", error.get("code").asText());
    }

    @Test
    void shouldReturnBadRequestWhenDeleteIdIsInvalid() throws Exception {
        ResponseEntity<String> response = restTemplate.exchange(
            url("/api/empleados/abc"),
            HttpMethod.DELETE,
            null,
            String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        JsonNode error = objectMapper.readTree(response.getBody());
        assertEquals("INVALID_PARAMETER", error.get("code").asText());
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    private EmpleadoRequest buildValidRequest(String rutDni) {
        EmpleadoRequest request = new EmpleadoRequest();
        request.setNombre("Ana");
        request.setApellido("Perez");
        request.setRutDni(rutDni);
        request.setCargo("Analista");
        request.setSalarioBase(new BigDecimal("500000"));
        request.setBono(new BigDecimal("50000"));
        request.setDescuentos(new BigDecimal("20000"));
        return request;
    }

    private Long insertEmpleado(String rutDni) {
        jdbcTemplate.update(
            "INSERT INTO empleados (nombre, apellido, rut_dni, cargo, salario_base, bono, descuentos) VALUES (?, ?, ?, ?, ?, ?, ?)",
            "Ana",
            "Perez",
            rutDni,
            "Analista",
            new BigDecimal("500000"),
            new BigDecimal("50000"),
            new BigDecimal("20000")
        );
        return jdbcTemplate.queryForObject("SELECT id FROM empleados WHERE rut_dni = ?", Long.class, rutDni);
    }
}
