package cl.previred.rsanchez.demoprevired.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ErrorResponse {

    private String mensaje;
    private List<String> errores;
}
