package com.previred.empleados.dto;

import java.util.ArrayList;
import java.util.List;

public class ErrorResponseDTO {
    private List<String> errors;

    public ErrorResponseDTO() {
        this.errors = new ArrayList<>();
    }

    public ErrorResponseDTO(List<String> errors) {
        this.errors = errors;
    }

    public ErrorResponseDTO(String error) {
        this.errors = new ArrayList<>();
        this.errors.add(error);
    }

    public void addError(String error) {
        this.errors.add(error);
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
