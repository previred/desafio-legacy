package com.previred.desafiolegacy.domain.exception;

public class DataDomainException extends RuntimeException {

    private String field;

    public DataDomainException( String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
