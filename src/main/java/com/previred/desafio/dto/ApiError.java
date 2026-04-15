package com.previred.desafio.dto;

public class ApiError {

    private String field;
    private String code;
    private String message;
    private Object rejectedValue;

    public ApiError() {
    }

    public ApiError(String field, String code, String message, Object rejectedValue) {
        this.field = field;
        this.code = code;
        this.message = message;
        this.rejectedValue = rejectedValue;
    }

    public static ApiError of(String code, String message) {
        return new ApiError(null, code, message, null);
    }

    public static ApiError of(String field, String code, String message) {
        return new ApiError(field, code, message, null);
    }

    public static ApiError of(String field, String code, String message, Object rejectedValue) {
        return new ApiError(field, code, message, rejectedValue);
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getRejectedValue() {
        return rejectedValue;
    }

    public void setRejectedValue(Object rejectedValue) {
        this.rejectedValue = rejectedValue;
    }
}
