package socket_server.common.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import gotcha_common.exception.CustomException;
import gotcha_common.exception.exceptionCode.GlobalExceptionCode;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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
    public <T> T deserialize(Object raw, Class<T> clazz) {
        try {
            if (raw instanceof Map map) {
                map.remove("@class"); //dto내에 또 dto가 들어있는 경우 발생하는 에러 방지용 코드.
            }
            if(raw instanceof String rawstr) {
                return objectMapper.readValue(rawstr, clazz);
            }
            return objectMapper.convertValue(raw, clazz);
        } catch (JsonProcessingException e) {
            throw new CustomException(GlobalExceptionCode.INVALID_MESSAGE_FORMAT);
        }
    }

    // String(JSON) to List
    public <T> List<T> deserializeList(Object raw, Class<T> elementClass) {
        try {
            JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, elementClass);
            if(raw instanceof String rawstr) {
                return objectMapper.readValue(rawstr, type);
            }
            return objectMapper.convertValue(raw, type);
        } catch (JsonProcessingException e) {
            throw new CustomException(GlobalExceptionCode.INVALID_MESSAGE_FORMAT);
        }
    }
}