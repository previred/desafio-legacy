package com.desafio.legacy.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BusinessValidationException extends RuntimeException {

    private final String code;
    private final List<String> details;

    public BusinessValidationException(String code, String message, List<String> details) {
        super(message);
        this.code = code;
        if (details == null) {
            this.details = Collections.emptyList();
        } else {
            this.details = Collections.unmodifiableList(new ArrayList<String>(details));
        }
    }

    public String getCode() {
        return code;
    }

    public List<String> getDetails() {
        return details;
    }
}
