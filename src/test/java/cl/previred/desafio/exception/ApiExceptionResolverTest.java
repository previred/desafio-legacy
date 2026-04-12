package cl.previred.desafio.exception;

import cl.previred.desafio.dto.ValidationError;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.type.SimpleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class ApiExceptionResolverTest {

    private ApiExceptionResolver resolver;
    private static final String REQUEST_URI = "/api/empleados";

    @BeforeEach
    void setUp() {
        resolver = new ApiExceptionResolver();
    }

    @Test
    void validationExceptionRetorna400ConCampoYMensaje() {
        ValidationException ex = new ValidationException("nombre", "El nombre es requerido");

        ResolvedErrorResponse response = resolver.resolve(ex, REQUEST_URI);

        assertEquals(400, response.getStatus());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getErrores());
        assertEquals(1, response.getBody().getErrores().size());
        assertEquals("nombre", response.getBody().getErrores().get(0).getCampo());
        assertEquals("El nombre es requerido", response.getBody().getErrores().get(0).getMensaje());
    }

    @Test
    void validationExceptionListRetorna400YListaDeErrores() {
        ValidationExceptionList ex = new ValidationExceptionList(Arrays.asList(
                new ValidationError("nombre", "El nombre es requerido"),
                new ValidationError("rut", "RUT invalido")
        ));

        ResolvedErrorResponse response = resolver.resolve(ex, REQUEST_URI);

        assertEquals(400, response.getStatus());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getErrores());
        assertEquals(2, response.getBody().getErrores().size());
        assertEquals("nombre", response.getBody().getErrores().get(0).getCampo());
        assertEquals("rut", response.getBody().getErrores().get(1).getCampo());
    }

    @Test
    void resourceNotFoundExceptionRetorna404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("id", "Empleado no encontrado con id: 123");

        ResolvedErrorResponse response = resolver.resolve(ex, REQUEST_URI);

        assertEquals(404, response.getStatus());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getErrores());
        assertEquals(1, response.getBody().getErrores().size());
        assertEquals("id", response.getBody().getErrores().get(0).getCampo());
        assertEquals("Empleado no encontrado con id: 123", response.getBody().getErrores().get(0).getMensaje());
    }

    @Test
    void invalidFormatExceptionRetorna400ConCampo() {
        InvalidFormatException ex = new InvalidFormatException(null, "valor", Integer.class);

        ResolvedErrorResponse response = resolver.resolve(ex, REQUEST_URI);

        assertEquals(400, response.getStatus());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getErrores());
        assertEquals(1, response.getBody().getErrores().size());
        assertEquals("json", response.getBody().getErrores().get(0).getCampo());
    }

    @Test
    void mismatchedInputExceptionRetorna400ConErrorJson() {
        MismatchedInputException ex = MismatchedInputException.from(null, SimpleType.construct(String.class), "Tipo incorrecto");

        ResolvedErrorResponse response = resolver.resolve(ex, REQUEST_URI);

        assertEquals(400, response.getStatus());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getErrores());
        assertEquals(1, response.getBody().getErrores().size());
        assertEquals("json", response.getBody().getErrores().get(0).getCampo());
        assertEquals("JSON invalido o no pudo ser parseado", response.getBody().getErrores().get(0).getMensaje());
    }

    @Test
    void jsonParseExceptionRetorna400ConErrorJson() {
        JsonParseException ex = new JsonParseException((JsonParser) null, "JSON mal formado", (Throwable) null);

        ResolvedErrorResponse response = resolver.resolve(ex, REQUEST_URI);

        assertEquals(400, response.getStatus());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getErrores());
        assertEquals(1, response.getBody().getErrores().size());
        assertEquals("json", response.getBody().getErrores().get(0).getCampo());
        assertEquals("JSON invalido o no pudo ser parseado", response.getBody().getErrores().get(0).getMensaje());
    }

    @Test
    void repositoryExceptionRetorna500ConMensajeGenerico() {
        RepositoryException ex = new RepositoryException("Error de base de datos");

        ResolvedErrorResponse response = resolver.resolve(ex, REQUEST_URI);

        assertEquals(500, response.getStatus());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getErrores());
        assertEquals(1, response.getBody().getErrores().size());
        assertEquals("internal", response.getBody().getErrores().get(0).getCampo());
        assertEquals("Error interno del servidor", response.getBody().getErrores().get(0).getMensaje());
    }

    @Test
    void technicalExceptionRetorna500ConMensajeGenerico() {
        TechnicalException ex = new TechnicalException("Error tecnico especifico");

        ResolvedErrorResponse response = resolver.resolve(ex, REQUEST_URI);

        assertEquals(500, response.getStatus());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getErrores());
        assertEquals(1, response.getBody().getErrores().size());
        assertEquals("internal", response.getBody().getErrores().get(0).getCampo());
        assertEquals("Error interno del servidor", response.getBody().getErrores().get(0).getMensaje());
    }

    @Test
    void runtimeExceptionNoControladaRetorna500ConMensajeGenerico() {
        RuntimeException ex = new RuntimeException("Error runtime no esperado");

        ResolvedErrorResponse response = resolver.resolve(ex, REQUEST_URI);

        assertEquals(500, response.getStatus());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getErrores());
        assertEquals(1, response.getBody().getErrores().size());
        assertEquals("internal", response.getBody().getErrores().get(0).getCampo());
        assertEquals("Error interno del servidor", response.getBody().getErrores().get(0).getMensaje());
    }
}