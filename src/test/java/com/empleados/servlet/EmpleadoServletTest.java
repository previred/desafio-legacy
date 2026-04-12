package com.empleados.servlet;

import com.empleados.EmpleadosApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = EmpleadosApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmpleadoServletTest {

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
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Test
    @Order(1)
    @DisplayName("GET /api/empleados debe retornar una lista")
    void getEmpleados() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl(), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().startsWith("["));
        assertTrue(response.getBody().endsWith("]"));
    }

    @Test
    @Order(2)
    @DisplayName("POST /api/empleados debe crear un empleado válido")
    void postEmpleadoValido() throws Exception {
        Map<String, Object> empleado = new HashMap<>();
        empleado.put("nombre", "María");
        empleado.put("apellido", "González");
        empleado.put("rut", "11.111.111-1");
        empleado.put("cargo", "Ingeniera");
        empleado.put("salario", 800000);
        empleado.put("bono", 100000);
        empleado.put("descuentos", 50000);

        HttpEntity<String> entity = new HttpEntity<>(
            objectMapper.writeValueAsString(empleado), jsonHeaders());

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl(), entity, String.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().contains("María"));
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/empleados debe retornar el empleado creado")
    void getEmpleadosConDatos() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl(), String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("María"));
    }

    @Test
    @Order(4)
    @DisplayName("POST con RUT duplicado debe retornar 400")
    void postRutDuplicado() throws Exception {
        Map<String, Object> empleado = new HashMap<>();
        empleado.put("nombre", "Pedro");
        empleado.put("apellido", "López");
        empleado.put("rut", "11.111.111-1");
        empleado.put("cargo", "Analista");
        empleado.put("salario", 500000);
        empleado.put("bono", 0);
        empleado.put("descuentos", 0);

        HttpEntity<String> entity = new HttpEntity<>(
            objectMapper.writeValueAsString(empleado), jsonHeaders());

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl(), entity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Ya existe"));
    }

    @Test
    @Order(5)
    @DisplayName("POST con salario bajo debe retornar 400")
    void postSalarioBajo() throws Exception {
        Map<String, Object> empleado = new HashMap<>();
        empleado.put("nombre", "Pedro");
        empleado.put("apellido", "López");
        empleado.put("rut", "22.222.222-2");
        empleado.put("cargo", "Analista");
        empleado.put("salario", 200000);
        empleado.put("bono", 0);
        empleado.put("descuentos", 0);

        HttpEntity<String> entity = new HttpEntity<>(
            objectMapper.writeValueAsString(empleado), jsonHeaders());

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl(), entity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("400,000"));
    }

    @Test
    @Order(6)
    @DisplayName("POST con bono excesivo debe retornar 400")
    void postBonoExcesivo() throws Exception {
        Map<String, Object> empleado = new HashMap<>();
        empleado.put("nombre", "Pedro");
        empleado.put("apellido", "López");
        empleado.put("rut", "22.222.222-2");
        empleado.put("cargo", "Analista");
        empleado.put("salario", 500000);
        empleado.put("bono", 300000);
        empleado.put("descuentos", 0);

        HttpEntity<String> entity = new HttpEntity<>(
            objectMapper.writeValueAsString(empleado), jsonHeaders());

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl(), entity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("50%"));
    }

    @Test
    @Order(7)
    @DisplayName("POST con descuentos mayores al salario debe retornar 400")
    void postDescuentosExcesivos() throws Exception {
        Map<String, Object> empleado = new HashMap<>();
        empleado.put("nombre", "Pedro");
        empleado.put("apellido", "López");
        empleado.put("rut", "22.222.222-2");
        empleado.put("cargo", "Analista");
        empleado.put("salario", 500000);
        empleado.put("bono", 0);
        empleado.put("descuentos", 600000);

        HttpEntity<String> entity = new HttpEntity<>(
            objectMapper.writeValueAsString(empleado), jsonHeaders());

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl(), entity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("descuentos"));
    }

    @Test
    @Order(8)
    @DisplayName("DELETE /api/empleados/{id} debe eliminar el empleado")
    void deleteEmpleado() {
        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl() + "/1", HttpMethod.DELETE, null, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("eliminado"));
    }

    @Test
    @Order(9)
    @DisplayName("DELETE con ID inexistente debe retornar 404")
    void deleteInexistente() {
        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl() + "/999", HttpMethod.DELETE, null, String.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(10)
    @DisplayName("POST con campos vacíos debe retornar 400")
    void postCamposVacios() throws Exception {
        Map<String, Object> empleado = new HashMap<>();
        empleado.put("nombre", "");
        empleado.put("apellido", "");
        empleado.put("rut", "");
        empleado.put("cargo", "");
        empleado.put("salario", 0);
        empleado.put("bono", 0);
        empleado.put("descuentos", 0);

        HttpEntity<String> entity = new HttpEntity<>(
            objectMapper.writeValueAsString(empleado), jsonHeaders());

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl(), entity, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("errores"));
    }
}
