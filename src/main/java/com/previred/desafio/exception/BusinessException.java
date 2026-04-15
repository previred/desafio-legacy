package com.previred.desafio.exception;

import com.previred.desafio.dto.ApiError;
import java.util.List;

public class BusinessException extends RuntimeException {

    private final int status;
    private final List<ApiError> errors;

    public BusinessException(int status, String message, List<ApiError> errors) {
        super(message);
        this.status = status;
        this.errors = errors;
    }

    public int getStatus() {
        return status;
    }

    public List<ApiError> getErrors() {
        return errors;
    }
}
