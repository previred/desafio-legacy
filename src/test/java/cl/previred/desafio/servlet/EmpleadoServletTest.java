package cl.previred.desafio.servlet;

import cl.previred.desafio.dto.EmpleadoRequest;
import cl.previred.desafio.dto.ValidationError;
import cl.previred.desafio.exception.ApiExceptionResolver;
import cl.previred.desafio.exception.ValidationExceptionList;
import cl.previred.desafio.model.Empleado;
import cl.previred.desafio.service.EmpleadoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpleadoServletTest {

    @Mock
    private EmpleadoService empleadoService;

    private ApiExceptionResolver apiExceptionResolver;
    private ObjectMapper objectMapper;
    private EmpleadoServlet empleadoServlet;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        apiExceptionResolver = new ApiExceptionResolver();
        empleadoServlet = new EmpleadoServlet(empleadoService, objectMapper, apiExceptionResolver);
    }

    @Test
    void doPostConRequestValidoRetorna201YEmpleadoCreado() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse resp = new MockHttpServletResponse();
        req.setMethod("POST");
        req.setContentType("application/json");
        req.setContent(("{"
                + "\"nombre\":\"Juan\","
                + "\"apellido\":\"Perez\","
                + "\"rut\":\"11111111-1\","
                + "\"cargo\":\"Dev\","
                + "\"salario\":500000"
                + "}").getBytes("UTF-8"));

        Empleado creado = new Empleado();
        creado.setId(10L);
        creado.setNombre("Juan");
        creado.setApellido("Perez");
        creado.setRut("11111111-1");
        creado.setCargo("Dev");
        creado.setSalario(new BigDecimal("500000"));

        when(empleadoService.crearEmpleado(any(EmpleadoRequest.class))).thenReturn(creado);

        empleadoServlet.doPost(req, resp);

        assertEquals(201, resp.getStatus());
        assertTrue(resp.getContentAsString().contains("\"id\":10"));
    }

    @Test
    void doDeleteConIdExistenteRetorna204() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse resp = new MockHttpServletResponse();
        req.setMethod("DELETE");
        req.setPathInfo("/1");

        when(empleadoService.eliminarEmpleado(1L)).thenReturn(true);

        empleadoServlet.doDelete(req, resp);

        assertEquals(204, resp.getStatus());
    }

    @Test
    void doGetConEmpleadosRetorna200YListaDeEmpleados() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse resp = new MockHttpServletResponse();
        req.setMethod("GET");

        Empleado emp1 = new Empleado();
        emp1.setId(1L);
        emp1.setNombre("Juan");
        emp1.setApellido("Perez");
        emp1.setRut("11111111-1");
        emp1.setCargo("Dev");
        emp1.setSalario(new BigDecimal("500000"));

        when(empleadoService.getAllEmpleados()).thenReturn(Arrays.asList(emp1));

        empleadoServlet.doGet(req, resp);

        assertEquals(200, resp.getStatus());
        assertTrue(resp.getContentAsString().contains("\"id\":1"));
        assertTrue(resp.getContentAsString().contains("Juan"));
    }
}
