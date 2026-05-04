package com.previred.desafiolegacy.application.dto;

import java.util.HashMap;

public class CommandResult {

    private CommandStatus status;

    private HashMap<String, String> errors;

    private String message;

    public CommandResult(CommandStatus status, HashMap<String, String> errors) {
        this.status = status;
        this.errors = errors;
    }

    public CommandResult(CommandStatus commandStatus, String message) {
        this.status = commandStatus;
        this.message = message;
    }

    public static CommandResult success() {
        return new CommandResult(CommandStatus.SUCCESS, new HashMap<>());
    }

    public static CommandResult fail(CommandStatus status, String field, String message) {

        HashMap<String, String> errors = new HashMap<>();
        errors.put(field, message);

        return new CommandResult(status, errors);
    }

    public static CommandResult fail(CommandStatus commandStatus,  String message) {
        return new CommandResult(commandStatus, message);
    }

    public CommandStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public void setStatus(CommandStatus status) {
        this.status = status;
    }

    public HashMap<String, String> getErrors() {
        return errors;
    }

    public void setErrors(HashMap<String, String> errors) {
        this.errors = errors;
    }
}
