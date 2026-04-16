package cl.previred.rsanchez.demoprevired.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class BadRequestException extends RuntimeException {

    private final List<String> errores;


    public BadRequestException(List<String> errores) {
        this.errores = errores;
    }

}
