package socket_server.common.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import gotcha_common.exception.CustomException;
import gotcha_common.exception.exceptionCode.GlobalExceptionCode;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JsonSerializer {
    private final ObjectMapper objectMapper;

    public JsonSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // Object to String(JSON)
    public <T> String serialize(T object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new CustomException(GlobalExceptionCode.INVALID_MESSAGE_FORMAT);
        }
    }

    // String(JSON) to Object
    public <T> T deserialize(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new CustomException(GlobalExceptionCode.INVALID_MESSAGE_FORMAT);
        }
    }

    // String(JSON) to List
    public <T> List<T> deserializeList(String json, Class<T> elementClass) {
        try {
            JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, elementClass);
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new CustomException(GlobalExceptionCode.INVALID_MESSAGE_FORMAT);
        }
    }
}