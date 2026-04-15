package com.desafio.empleados.integration;

import com.desafio.empleados.EmpleadosApplication;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integración: servlet + JDBC + H2 (contexto Spring Boot completo).
 */
@SpringBootTest(classes = EmpleadosApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("API /api/empleados (integración)")
class EmpleadoServletIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/empleados";
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }

    @Test
    @DisplayName("GET inicial devuelve JSON array (puede estar vacío)")
    void getListaEsJsonArray() throws Exception {
        ResponseEntity<String> res = restTemplate.getForEntity(baseUrl(), String.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode root = objectMapper.readTree(res.getBody());
        assertThat(root.isArray()).isTrue();
    }

    @Test
    @DisplayName("Respuestas /api incluyen X-Request-Id (generado si el cliente no lo envía)")
    void getDevuelveCabeceraXRequestId() {
        ResponseEntity<String> res = restTemplate.getForEntity(baseUrl(), String.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getHeaders().getFirst("X-Request-Id")).isNotBlank();
    }

    @Test
    @DisplayName("Cabecera X-Request-Id del cliente se respeta en la respuesta")
    void getRespetaXRequestIdCliente() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Request-Id", "trace-cliente-unitario");
        ResponseEntity<String> res = restTemplate.exchange(
                baseUrl(),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getHeaders().getFirst("X-Request-Id")).isEqualTo("trace-cliente-unitario");
    }

    @Test
    @DisplayName("POST válido → 201 y GET incluye el empleado; DELETE → 204")
    void flujoPostGetDelete() throws Exception {
        String json = "{"
                + "\"nombre\":\"IT\","
                + "\"apellido\":\"Test\","
                + "\"rutDni\":\"11.111.111-1\","
                + "\"cargo\":\"QA\","
                + "\"salarioBase\":450000,"
                + "\"bono\":0,"
                + "\"descuentos\":0"
                + "}";

        ResponseEntity<String> created = restTemplate.postForEntity(
                baseUrl(),
                new HttpEntity<>(json, jsonHeaders()),
                String.class);

        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        JsonNode body = objectMapper.readTree(created.getBody());
        assertThat(body.get("id").asLong()).isPositive();
        long id = body.get("id").asLong();

        ResponseEntity<String> list = restTemplate.getForEntity(baseUrl(), String.class);
        assertThat(list.getBody()).contains("11111111-1");

        ResponseEntity<Void> del = restTemplate.exchange(
                baseUrl() + "/" + id,
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Void.class);
        assertThat(del.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> listAfter = restTemplate.getForEntity(baseUrl(), String.class);
        assertThat(listAfter.getBody()).doesNotContain("11111111-1");
    }

    @Test
    @DisplayName("POST con JSON inválido → 400 y arreglo errores (campo cuerpo)")
    void postJsonInvalidoDevuelve400() throws Exception {
        ResponseEntity<String> res = restTemplate.postForEntity(
                baseUrl(),
                new HttpEntity<>("{ esto no es json", jsonHeaders()),
                String.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        JsonNode root = objectMapper.readTree(res.getBody());
        assertThat(root.has("errores")).isTrue();
        assertThat(root.get("errores").isArray()).isTrue();
        assertThat(root.get("errores").size()).isPositive();
        assertThat(root.get("errores").get(0).get("campo").asText()).isEqualTo("cuerpo");
    }

    @Test
    @DisplayName("PUT no soportado → 405 y cabecera Allow")
    void putNoPermitido405() {
        ResponseEntity<String> res = restTemplate.exchange(
                baseUrl(),
                HttpMethod.PUT,
                new HttpEntity<>("{}", jsonHeaders()),
                String.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        assertThat(res.getHeaders().getFirst("Allow")).isNotBlank();
    }

    @Test
    @DisplayName("POST salario base negativo → 400 errores salarioBase")
    void postSalarioNegativo400() throws Exception {
        String json = "{\"nombre\":\"X\",\"apellido\":\"Y\",\"rutDni\":\"11.111.111-1\",\"cargo\":\"Z\","
                + "\"salarioBase\":-1,\"bono\":0,\"descuentos\":0}";
        ResponseEntity<String> res = restTemplate.postForEntity(
                baseUrl(),
                new HttpEntity<>(json, jsonHeaders()),
                String.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        JsonNode root = objectMapper.readTree(res.getBody());
        assertThat(root.get("errores").size()).isPositive();
        assertThat(root.get("errores").toString()).contains("salarioBase");
    }

    @Test
    @DisplayName("POST salarioBase numérico como string: comportamiento Jackson (acepta o 400 cuerpo)")
    void postSalarioBaseComoStringSegunJackson() {
        String json = "{\"nombre\":\"X\",\"apellido\":\"Y\",\"rutDni\":\"12.345.678-5\",\"cargo\":\"Z\","
                + "\"salarioBase\":\"450000\",\"bono\":0,\"descuentos\":0}";
        ResponseEntity<String> res = restTemplate.postForEntity(
                baseUrl(),
                new HttpEntity<>(json, jsonHeaders()),
                String.class);
        assertThat(res.getStatusCode()).isIn(HttpStatus.CREATED, HttpStatus.BAD_REQUEST);
        if (res.getStatusCode() == HttpStatus.BAD_REQUEST) {
            assertThat(res.getBody()).contains("errores");
        }
    }

    @Test
    @DisplayName("POST con salario bajo → 400 y cuerpo con errores")
    void postInvalidoDevuelve400() throws Exception {
        String json = "{"
                + "\"nombre\":\"X\","
                + "\"apellido\":\"Y\","
                + "\"rutDni\":\"11.111.111-1\","
                + "\"cargo\":\"Z\","
                + "\"salarioBase\":100,"
                + "\"bono\":0,"
                + "\"descuentos\":0"
                + "}";

        ResponseEntity<String> res = restTemplate.postForEntity(
                baseUrl(),
                new HttpEntity<>(json, jsonHeaders()),
                String.class);

        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        JsonNode root = objectMapper.readTree(res.getBody());
        assertThat(root.has("errores")).isTrue();
        assertThat(root.get("errores").isArray()).isTrue();
        assertThat(root.get("errores").size()).isPositive();
    }

    @Test
    @DisplayName("DELETE id inexistente → 404")
    void deleteInexistente() {
        ResponseEntity<String> res = restTemplate.exchange(
                baseUrl() + "/999999999",
                HttpMethod.DELETE,
                null,
                String.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
