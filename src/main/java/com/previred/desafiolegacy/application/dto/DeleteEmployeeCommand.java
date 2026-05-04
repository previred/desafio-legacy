package com.previred.desafiolegacy.application.dto;

public class DeleteEmployeeCommand {

    private Long id;

    public DeleteEmployeeCommand(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
