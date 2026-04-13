package com.desafio.legacy.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ErrorResponse {

    private String code;
    private String message;
    private List<String> details;

    public ErrorResponse() {
        this.details = new ArrayList<String>();
    }

    public ErrorResponse(String code, String message, List<String> details) {
        this.code = code;
        this.message = message;
        setDetails(details);
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

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        if (details == null) {
            this.details = new ArrayList<String>();
            return;
        }
        this.details = Collections.unmodifiableList(new ArrayList<String>(details));
    }
}
