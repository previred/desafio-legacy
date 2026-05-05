package cl.previred.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class JsonUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JsonUtils() {
    }

    public static <T> T fromJson(InputStream inputStream, Class<T> clazz) throws IOException {
        return OBJECT_MAPPER.readValue(inputStream, clazz);
    }

    public static void writeJson(OutputStream outputStream, Object data) throws IOException {
        OBJECT_MAPPER.writeValue(outputStream, data);
    }
}
