package cl.previred.desafio.exception;

import cl.previred.desafio.dto.ErrorResponse;
import cl.previred.desafio.dto.ErrorResponseFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

/**
 * Componente que resuelve excepciones especificas de la API a sus
 * correspondientes respuestas HTTP.
 *
 * <p>Este resolvedor implementa el patron Strategy para manejar diferentes
 * tipos de excepciones y convertirlas en {@link ResolvedErrorResponse}
 * con el codigo de estado HTTP y cuerpo apropiados.</p>
 *
 * <p>Excepciones manejadas:</p>
 * <ul>
 *   <li>{@link ValidationExceptionList} - 400 Bad Request</li>
 *   <li>{@link ValidationException} - 400 Bad Request</li>
 *   <li>{@link ResourceNotFoundException} - 404 Not Found</li>
 *   <li>{@link InvalidFormatException} - 400 Bad Request</li>
 *   <li>{@link MismatchedInputException} - 400 Bad Request</li>
 *   <li>{@link JsonParseException} - 400 Bad Request</li>
 *   <li>{@link RepositoryException} - 500 Internal Server Error</li>
 *   <li>{@link TechnicalException} - 500 Internal Server Error</li>
 * </ul>
 *
 * @see ResolvedErrorResponse
 * @see ErrorResponse
 * @since 1.0
 */
@Component
public class ApiExceptionResolver {

    /** Logger para trazabilidad de errores resueltos. */
    private static final Logger LOG = LoggerFactory.getLogger(ApiExceptionResolver.class);

    /**
     * Resuelve una excepcion a su respuesta HTTP correspondiente.
     *
     * @param ex        la excepcion a resolver
     * @param requestUri URI de la peticion dondeoccurrio el error
     * @return ResolvedErrorResponse con codigo de estado y cuerpo de respuesta
     */
    public ResolvedErrorResponse resolve(Exception ex, String requestUri) {
        if (ex instanceof ValidationExceptionList) {
            ValidationExceptionList validationEx = (ValidationExceptionList) ex;
            LOG.warn("ValidationExceptionList en {} - {} errores", requestUri, validationEx.getErrores().size());
            return new ResolvedErrorResponse(HttpServletResponse.SC_BAD_REQUEST, ErrorResponseFactory.validationError(validationEx.getErrores()));
        }

        if (ex instanceof ValidationException) {
            ValidationException validationEx = (ValidationException) ex;
            LOG.warn("ValidationException en {} - Campo: {}", requestUri, validationEx.getCampo());
            return new ResolvedErrorResponse(HttpServletResponse.SC_BAD_REQUEST,
                    ErrorResponseFactory.badRequest(validationEx.getCampo(), validationEx.getMessage()));
        }

        if (ex instanceof ResourceNotFoundException) {
            ResourceNotFoundException notFoundEx = (ResourceNotFoundException) ex;
            LOG.warn("ResourceNotFoundException en {} - Campo: {}", requestUri, notFoundEx.getCampo());
            return new ResolvedErrorResponse(HttpServletResponse.SC_NOT_FOUND,
                    ErrorResponseFactory.notFound(notFoundEx.getCampo(), notFoundEx.getMessage()));
        }

        if (ex instanceof InvalidFormatException) {
            InvalidFormatException invalidFormatEx = (InvalidFormatException) ex;
            String campo = invalidFormatEx.getPath().isEmpty() ? "json" : invalidFormatEx.getPath().get(0).getFieldName();
            LOG.warn("InvalidFormat en {} - Campo: {}", requestUri, campo);
            return new ResolvedErrorResponse(HttpServletResponse.SC_BAD_REQUEST,
                    ErrorResponseFactory.badRequest(campo, "Formato invalido para el campo: " + campo));
        }

        if (ex instanceof MismatchedInputException) {
            LOG.warn("MismatchedInputException en {}", requestUri);
            return new ResolvedErrorResponse(HttpServletResponse.SC_BAD_REQUEST, ErrorResponseFactory.jsonInvalidError());
        }

        if (ex instanceof JsonParseException) {
            LOG.warn("JsonParseException en {}", requestUri);
            return new ResolvedErrorResponse(HttpServletResponse.SC_BAD_REQUEST, ErrorResponseFactory.jsonInvalidError());
        }

        if (ex instanceof RepositoryException || ex instanceof TechnicalException) {
            LOG.error("Error tecnico en {}", requestUri, ex);
            return new ResolvedErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorResponseFactory.internalError("Error interno del servidor"));
        }

        LOG.error("Error no controlado en {}", requestUri, ex);
        return new ResolvedErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ErrorResponseFactory.internalError("Error interno del servidor"));
    }
}