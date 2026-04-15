package com.previred.desafio.dto;

import java.util.List;

public class ErrorResponse {

    private int status;
    private String message;
    private List<ApiError> errors;

    public ErrorResponse() {
    }

    public ErrorResponse(int status, String message, List<ApiError> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ApiError> getErrors() {
        return errors;
    }

    public void setErrors(List<ApiError> errors) {
        this.errors = errors;
    }
}
