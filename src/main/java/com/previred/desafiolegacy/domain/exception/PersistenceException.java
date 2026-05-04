package com.previred.desafiolegacy.domain.exception;

public class PersistenceException extends RuntimeException  {
    public PersistenceException(String message) {
        super(message);
    }
}
