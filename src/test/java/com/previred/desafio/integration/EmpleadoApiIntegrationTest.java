package com.previred.desafio.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.previred.desafio.dto.EmpleadoRequest;
import com.previred.desafio.model.Empleado;
import java.math.BigDecimal;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Pruebas de integración mínimas sobre la API real de empleados.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmpleadoApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    /** Verifica que el endpoint GET exponga los datos semilla. */
    @Test
    void shouldReturnSeedEmployeesOnGet() {
        ResponseEntity<Empleado[]> response = restTemplate.getForEntity(baseUrl("/api/empleados"), Empleado[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThanOrEqualTo(2);
    }

    /** Verifica el alta vía API y su persistencia en una consulta posterior. */
    @Test
    void shouldCreateEmployeeAndReturnItInSubsequentGet() {
        EmpleadoRequest request = new EmpleadoRequest();
        request.setNombre("Lucia");
        request.setApellido("Mora");
        request.setRutDni("99887766-1");
        request.setCargo("QA");
        request.setSalarioBase(new BigDecimal("650000"));
        request.setBono(new BigDecimal("50000"));
        request.setDescuentos(new BigDecimal("15000"));

        ResponseEntity<Empleado> createResponse = restTemplate.postForEntity(baseUrl("/api/empleados"), request, Empleado.class);
        ResponseEntity<Empleado[]> listResponse = restTemplate.getForEntity(baseUrl("/api/empleados"), Empleado[].class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().getId()).isNotNull();
        assertThat(listResponse.getBody()).isNotNull();
        assertThat(Arrays.stream(listResponse.getBody()))
                .extracting(Empleado::getRutDni)
                .contains("99887766-1");
    }

    private String baseUrl(String path) {
        return "http://localhost:" + port + path;
    }
}
