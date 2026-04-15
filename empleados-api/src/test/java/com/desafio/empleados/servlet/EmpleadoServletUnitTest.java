package com.desafio.empleados.servlet;

import com.desafio.empleados.exception.PersistenciaEmpleadosException;
import com.desafio.empleados.model.Empleado;
import com.desafio.empleados.model.ErrorRegistro;
import com.desafio.empleados.model.RespuestaErrorValidacion;
import com.desafio.empleados.service.EmpleadoService;
import com.desafio.empleados.service.EmpleadoService.ResultadoAlta;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmpleadoServlet (mock HTTP)")
class EmpleadoServletUnitTest {

    @Mock
    private EmpleadoService empleadoService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private EmpleadoServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new EmpleadoServlet(empleadoService, objectMapper);
    }

    @Test
    void putMetodoNoPermitido405YAllow() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setMethod("PUT");
        req.setPathInfo(null);
        req.setContentType("application/json");
        req.setContent("{}".getBytes(StandardCharsets.UTF_8));
        MockHttpServletResponse resp = new MockHttpServletResponse();
        servlet.service(req, resp);
        assertThat(resp.getStatus()).isEqualTo(405);
        assertThat(resp.getHeader("Allow")).contains("GET").contains("POST").contains("DELETE");
        assertThat(resp.getContentAsString(StandardCharsets.UTF_8)).contains("mensaje");
    }

    @Test
    void getConSubrutaDevuelve404() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setMethod("GET");
        req.setPathInfo("/extra");
        MockHttpServletResponse resp = new MockHttpServletResponse();
        servlet.service(req, resp);
        assertThat(resp.getStatus()).isEqualTo(404);
    }

    @Test
    void getListarErrorDevuelve500() throws Exception {
        when(empleadoService.listar()).thenThrow(new PersistenciaEmpleadosException("db", null));
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setMethod("GET");
        req.setPathInfo(null);
        MockHttpServletResponse resp = new MockHttpServletResponse();
        servlet.service(req, resp);
        assertThat(resp.getStatus()).isEqualTo(500);
        assertThat(resp.getContentAsString(StandardCharsets.UTF_8)).contains("Error interno");
    }

    @Test
    void postJsonMalformado400() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setMethod("POST");
        req.setPathInfo(null);
        req.setContentType("application/json");
        req.setContent("{ no es json".getBytes(StandardCharsets.UTF_8));
        MockHttpServletResponse resp = new MockHttpServletResponse();
        servlet.service(req, resp);
        assertThat(resp.getStatus()).isEqualTo(400);
        assertThat(resp.getContentAsString(StandardCharsets.UTF_8)).contains("errores").contains("cuerpo");
    }

    @Test
    void postCuerpoVacio400() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setMethod("POST");
        req.setPathInfo(null);
        req.setContentType("application/json");
        req.setContent(new byte[0]);
        MockHttpServletResponse resp = new MockHttpServletResponse();
        servlet.service(req, resp);
        assertThat(resp.getStatus()).isEqualTo(400);
        assertThat(resp.getContentAsString(StandardCharsets.UTF_8)).contains("errores");
    }

    @Test
    void postContentTypeInvalido415() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setMethod("POST");
        req.setPathInfo(null);
        req.setContentType("text/plain");
        req.setContent("{}".getBytes(StandardCharsets.UTF_8));
        MockHttpServletResponse resp = new MockHttpServletResponse();
        servlet.service(req, resp);
        assertThat(resp.getStatus()).isEqualTo(415);
    }

    @Test
    void postConPathExtra404() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setMethod("POST");
        req.setPathInfo("/no");
        req.setContentType("application/json");
        req.setContent("{}".getBytes(StandardCharsets.UTF_8));
        MockHttpServletResponse resp = new MockHttpServletResponse();
        servlet.service(req, resp);
        assertThat(resp.getStatus()).isEqualTo(404);
    }

    @Test
    void postValidacion400() throws Exception {
        RespuestaErrorValidacion err = new RespuestaErrorValidacion();
        err.setErrores(Collections.singletonList(new ErrorRegistro("x", "y")));
        when(empleadoService.validarYGuardar(any(Empleado.class))).thenReturn(ResultadoAlta.conErrores(err));

        String json = "{\"nombre\":\"a\",\"apellido\":\"b\",\"rutDni\":\"11.111.111-1\",\"cargo\":\"c\","
                + "\"salarioBase\":500000,\"bono\":0,\"descuentos\":0}";
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setMethod("POST");
        req.setPathInfo(null);
        req.setContentType("application/json;charset=UTF-8");
        req.setContent(json.getBytes(StandardCharsets.UTF_8));
        MockHttpServletResponse resp = new MockHttpServletResponse();
        servlet.service(req, resp);
        assertThat(resp.getStatus()).isEqualTo(400);
        assertThat(resp.getContentAsString(StandardCharsets.UTF_8)).contains("errores");
    }

    @Test
    void deleteSinIdValido400() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setMethod("DELETE");
        req.setPathInfo("/");
        MockHttpServletResponse resp = new MockHttpServletResponse();
        servlet.service(req, resp);
        assertThat(resp.getStatus()).isEqualTo(400);
    }

    @Test
    void deleteIdNoNumerico400() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setMethod("DELETE");
        req.setPathInfo("/abc");
        MockHttpServletResponse resp = new MockHttpServletResponse();
        servlet.service(req, resp);
        assertThat(resp.getStatus()).isEqualTo(400);
    }

    @Test
    void deleteServicioError500() throws Exception {
        when(empleadoService.eliminar(anyLong())).thenThrow(new PersistenciaEmpleadosException("db", null));
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setMethod("DELETE");
        req.setPathInfo("/7");
        MockHttpServletResponse resp = new MockHttpServletResponse();
        servlet.service(req, resp);
        assertThat(resp.getStatus()).isEqualTo(500);
    }

    @Test
    void getListaOk() throws Exception {
        when(empleadoService.listar()).thenReturn(Collections.emptyList());
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setMethod("GET");
        req.setPathInfo(null);
        MockHttpServletResponse resp = new MockHttpServletResponse();
        servlet.service(req, resp);
        assertThat(resp.getStatus()).isEqualTo(200);
        assertThat(resp.getContentAsString(StandardCharsets.UTF_8)).isEqualTo("[]");
    }

    @Test
    void postCreado201ConLocation() throws Exception {
        when(empleadoService.validarYGuardar(any(Empleado.class))).thenAnswer(inv -> {
            Empleado e = inv.getArgument(0);
            Empleado persistido = new Empleado();
            persistido.setId(42L);
            persistido.setNombre(e.getNombre());
            persistido.setApellido(e.getApellido());
            persistido.setRutDni(e.getRutDni());
            persistido.setCargo(e.getCargo());
            persistido.setSalarioBase(e.getSalarioBase());
            persistido.setBono(e.getBono());
            persistido.setDescuentos(e.getDescuentos());
            return ResultadoAlta.exitoso(persistido);
        });

        String json = "{\"nombre\":\"n\",\"apellido\":\"a\",\"rutDni\":\"11.111.111-1\",\"cargo\":\"c\","
                + "\"salarioBase\":500000,\"bono\":0,\"descuentos\":0}";
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setMethod("POST");
        req.setPathInfo(null);
        req.setContentType("application/json");
        req.setContent(json.getBytes(StandardCharsets.UTF_8));
        req.setScheme("http");
        req.setServerName("localhost");
        req.setServerPort(8080);
        req.setRequestURI("/api/empleados");
        MockHttpServletResponse resp = new MockHttpServletResponse();
        servlet.service(req, resp);
        assertThat(resp.getStatus()).isEqualTo(201);
        assertThat(resp.getHeader("Location")).contains("/42");
    }
}
