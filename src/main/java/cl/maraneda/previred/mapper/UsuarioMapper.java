package cl.maraneda.previred.mapper;

import cl.maraneda.previred.dto.UsuarioDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UsuarioMapper {

    private static final ObjectMapper mapper = new ObjectMapper();

    private UsuarioMapper() {}

    public static UsuarioDTO fromJsonString(String json) {
        try {
            return mapper.readValue(json, UsuarioDTO.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("JSON inválido", e);
        }
    }
}

