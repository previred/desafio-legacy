package cl.previred.desafio.exception;

import cl.previred.desafio.dto.ErrorResponse;
import cl.previred.desafio.dto.ErrorResponseFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ApiExceptionResolver {

    private static final Logger LOG = LoggerFactory.getLogger(ApiExceptionResolver.class);

    public ResolvedErrorResponse resolve(Exception ex, String requestUri) {
        if (ex instanceof ValidationExceptionList) {
            ValidationExceptionList validationEx = (ValidationExceptionList) ex;
            LOG.warn("ValidationExceptionList en {} - {} errores", requestUri, validationEx.getErrores().size());
            return new ResolvedErrorResponse(400, ErrorResponseFactory.validationError(validationEx.getErrores()));
        }

        if (ex instanceof ValidationException) {
            ValidationException validationEx = (ValidationException) ex;
            LOG.warn("ValidationException en {} - Campo: {}", requestUri, validationEx.getCampo());
            return new ResolvedErrorResponse(400,
                    ErrorResponseFactory.validationError(validationEx.getCampo(), validationEx.getMessage()));
        }

        if (ex instanceof ResourceNotFoundException) {
            ResourceNotFoundException notFoundEx = (ResourceNotFoundException) ex;
            LOG.warn("ResourceNotFoundException en {} - Campo: {}", requestUri, notFoundEx.getCampo());
            return new ResolvedErrorResponse(404,
                    ErrorResponseFactory.validationError(notFoundEx.getCampo(), notFoundEx.getMessage()));
        }

        if (ex instanceof InvalidFormatException) {
            InvalidFormatException invalidFormatEx = (InvalidFormatException) ex;
            String campo = invalidFormatEx.getPath().isEmpty() ? "json" : invalidFormatEx.getPath().get(0).getFieldName();
            LOG.warn("InvalidFormat en {} - Campo: {}", requestUri, campo);
            return new ResolvedErrorResponse(400,
                    ErrorResponseFactory.validationError(campo, "Formato invalido para el campo: " + campo));
        }

        if (ex instanceof MismatchedInputException) {
            LOG.warn("MismatchedInputException en {}", requestUri);
            return new ResolvedErrorResponse(400, ErrorResponseFactory.jsonInvalidError());
        }

        if (ex instanceof JsonParseException) {
            LOG.warn("JsonParseException en {}", requestUri);
            return new ResolvedErrorResponse(400, ErrorResponseFactory.jsonInvalidError());
        }

        if (ex instanceof RepositoryException || ex instanceof TechnicalException) {
            LOG.error("Error tecnico en {}", requestUri, ex);
            return new ResolvedErrorResponse(500, ErrorResponseFactory.internalError("Error interno del servidor"));
        }

        LOG.error("Error no controlado en {}", requestUri, ex);
        return new ResolvedErrorResponse(500, ErrorResponseFactory.internalError("Error interno del servidor"));
    }
}