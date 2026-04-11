package com.previred.empleados.servlet;

import com.google.gson.Gson;
import com.previred.empleados.exception.BusinessValidationException;
import com.previred.empleados.exception.EmpleadoNotFoundException;
import com.previred.empleados.model.EmpleadoModel;
import com.previred.empleados.service.IEmpleadoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpleadoServletTest {

    @Mock
    private IEmpleadoService empleadoService;

    private EmpleadoServlet servlet;
    private Gson gson;

    @BeforeEach
    void setUp() throws Exception {
        gson = new Gson();
        servlet = new EmpleadoServlet();

        // Inyectar dependencias via reflection (el servlet usa init() con WebApplicationContext)
        java.lang.reflect.Field serviceField = EmpleadoServlet.class.getDeclaredField("empleadoService");
        serviceField.setAccessible(true);
        serviceField.set(servlet, empleadoService);

        java.lang.reflect.Field gsonField = EmpleadoServlet.class.getDeclaredField("gson");
        gsonField.setAccessible(true);
        gsonField.set(servlet, gson);
    }

    // --- GET ---

    @Test
    void doGet_deberiaRetornar200ConLista() throws IOException {
        EmpleadoModel model = new EmpleadoModel();
        model.setId(1L);
        model.setNombre("Juan");
        when(empleadoService.listarEmpleados()).thenReturn(Arrays.asList(model));

        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/empleados");
        MockHttpServletResponse resp = new MockHttpServletResponse();

        servlet.doGet(req, resp);

        assertEquals(200, resp.getStatus());
        assertTrue(resp.getContentType().startsWith("application/json"));
        assertTrue(resp.getContentAsString().contains("Juan"));
    }

    @Test
    void doGet_sinEmpleados_deberiaRetornar200ConListaVacia() throws IOException {
        when(empleadoService.listarEmpleados()).thenReturn(Collections.emptyList());

        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/empleados");
        MockHttpServletResponse resp = new MockHttpServletResponse();

        servlet.doGet(req, resp);

        assertEquals(200, resp.getStatus());
        assertEquals("[]", resp.getContentAsString());
    }

    // --- POST ---

    @Test
    void doPost_conDatosValidos_deberiaRetornar201() throws IOException {
        EmpleadoModel created = new EmpleadoModel();
        created.setId(1L);
        created.setNombre("Juan");
        when(empleadoService.crearEmpleado(any())).thenReturn(created);

        MockHttpServletRequest req = new MockHttpServletRequest("POST", "/api/empleados");
        req.setContentType("application/json");
        req.setContent(gson.toJson(created).getBytes());
        MockHttpServletResponse resp = new MockHttpServletResponse();

        servlet.doPost(req, resp);

        assertEquals(201, resp.getStatus());
        assertTrue(resp.getContentAsString().contains("Juan"));
    }

    @Test
    void doPost_conValidacionFallida_deberiaRetornar400() throws IOException {
        when(empleadoService.crearEmpleado(any()))
                .thenThrow(new BusinessValidationException(Arrays.asList("Error de test")));

        MockHttpServletRequest req = new MockHttpServletRequest("POST", "/api/empleados");
        req.setContentType("application/json");
        req.setContent("{\"nombre\":\"Test\"}".getBytes());
        MockHttpServletResponse resp = new MockHttpServletResponse();

        servlet.doPost(req, resp);

        assertEquals(400, resp.getStatus());
        assertTrue(resp.getContentAsString().contains("Error de test"));
    }

    // --- DELETE ---

    @Test
    void doDelete_conIdValido_deberiaRetornar204() throws IOException {
        doNothing().when(empleadoService).eliminarEmpleado(1L);

        MockHttpServletRequest req = new MockHttpServletRequest("DELETE", "/api/empleados/1");
        req.setPathInfo("/1");
        MockHttpServletResponse resp = new MockHttpServletResponse();

        servlet.doDelete(req, resp);

        assertEquals(204, resp.getStatus());
    }

    @Test
    void doDelete_sinId_deberiaRetornar400() throws IOException {
        MockHttpServletRequest req = new MockHttpServletRequest("DELETE", "/api/empleados");
        MockHttpServletResponse resp = new MockHttpServletResponse();

        servlet.doDelete(req, resp);

        assertEquals(400, resp.getStatus());
    }

    @Test
    void doDelete_noExistente_deberiaRetornar404() throws IOException {
        doThrow(new EmpleadoNotFoundException(99L)).when(empleadoService).eliminarEmpleado(99L);

        MockHttpServletRequest req = new MockHttpServletRequest("DELETE", "/api/empleados/99");
        req.setPathInfo("/99");
        MockHttpServletResponse resp = new MockHttpServletResponse();

        servlet.doDelete(req, resp);

        assertEquals(404, resp.getStatus());
    }

    @Test
    void doDelete_conIdInvalido_deberiaRetornar400() throws IOException {
        MockHttpServletRequest req = new MockHttpServletRequest("DELETE", "/api/empleados/abc");
        req.setPathInfo("/abc");
        MockHttpServletResponse resp = new MockHttpServletResponse();

        servlet.doDelete(req, resp);

        assertEquals(400, resp.getStatus());
        assertTrue(resp.getContentAsString().contains("número válido"));
    }
}
