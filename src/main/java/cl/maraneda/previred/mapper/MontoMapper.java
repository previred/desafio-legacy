package cl.maraneda.previred.mapper;

import cl.maraneda.previred.dto.MontoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MontoMapper {
    private static final ObjectMapper mapper = new ObjectMapper();

    private MontoMapper() {}

    public static MontoDTO fromJsonString(String json){
        try {
            return mapper.readValue(json, MontoDTO.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("JSON inválido", e);
        }
    }
}
