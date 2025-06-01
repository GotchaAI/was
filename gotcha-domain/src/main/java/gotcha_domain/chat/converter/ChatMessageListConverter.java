package gotcha_domain.chat.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gotcha_domain.chat.ChatMessage;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.List;

@Converter
public class ChatMessageListConverter implements AttributeConverter<List<ChatMessage>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);


    @Override
    public String convertToDatabaseColumn(List<ChatMessage> chatMessages) {
        try {
            return objectMapper.writeValueAsString(chatMessages);
        } catch (Exception e) {
            throw new IllegalArgumentException("ChatMessage 변환 실패", e);
        }
    }

    @Override
    public List<ChatMessage> convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<ChatMessage>>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("ChatMessage 역변환 실패", e);
        }
    }
}

