package cl.previred.desafio.exception;

import cl.previred.desafio.dto.ErrorResponse;

public class ResolvedErrorResponse {

    private final int status;
    private final ErrorResponse body;

    public ResolvedErrorResponse(int status, ErrorResponse body) {
        this.status = status;
        this.body = body;
    }

    public int getStatus() {
        return status;
    }

    public ErrorResponse getBody() {
        return body;
    }
}