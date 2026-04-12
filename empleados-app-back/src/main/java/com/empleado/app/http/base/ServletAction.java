package com.empleado.app.http.base;

@FunctionalInterface
public interface ServletAction {

    void execute() throws Exception;
}