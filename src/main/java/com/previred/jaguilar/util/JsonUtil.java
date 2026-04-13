package com.previred.jaguilar.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String convertirAJson(Object objeto) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(objeto);
    }

    public static <T> T convertirDesdeJson(String json, Class<T> clase) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(json, clase);
    }
}