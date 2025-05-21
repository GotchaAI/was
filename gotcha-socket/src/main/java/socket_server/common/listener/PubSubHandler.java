package socket_server.common.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import gotcha_common.exception.CustomException;
import gotcha_common.exception.exceptionCode.GlobalExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import socket_server.common.config.RedisMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

//Redis Pub/Sub 메시지를 채널별로 분기하여 처리하고, WebSocket 구독자에게 전달하는 추상 핸들러 베이스 클래스
@Slf4j
public abstract class PubSubHandler {

    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 변환기
    protected final SimpMessagingTemplate messagingTemplate;
    protected final Map<String, BiConsumer<String,  Object >> handlers;

    public PubSubHandler(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.handlers = new HashMap<>();
        initHandlers();
    }

    // DSL IntegrationFlow에서 호출될 entry point
    public void onMessage(String topic, Object payload) {
        validatePayloadFormat(payload);

        handlers.entrySet().stream()
                .filter(e -> topic.startsWith(e.getKey()))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(this::handleUnknownChannel)
                .accept(topic, payload);
    }

    protected abstract void initHandlers();

    private void handleUnknownChannel(String topic, Object object ) {
        throw new CustomException(GlobalExceptionCode.INVALID_CHANNEL);
    }

    // RedisIntegrationConfig에서 전달된 메시지가 RedisMessage 형식인지 검증
    private void validatePayloadFormat(Object payload){
        if (!(payload instanceof RedisMessage)) {
            throw new CustomException(GlobalExceptionCode.INVALID_MESSAGE_FORMAT);
        }
    }

    protected <T> T convertMessageToDto(Object raw, Class<T> clazz) {
        try {
            if (raw instanceof Map map) {
                map.remove("@class"); //dto내에 또 dto가 들어있는 경우 발생하는 에러 방지용 코드.
            }
            return objectMapper.convertValue(raw, clazz);
        } catch (Exception e) {
            throw new CustomException(GlobalExceptionCode.INVALID_MESSAGE_FORMAT);
        }
    }

    protected <T> List<T> convertMessageToList(Object raw, Class<T> clazz) {
        try {
            if (raw instanceof String rawStr) { // String 으로 들어온 경우 readValue
                return objectMapper.readValue(
                        rawStr,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, clazz)
                );
            }

            return objectMapper.convertValue( // else convertValue
                    raw,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, clazz)
            );
        } catch (Exception e) {
            throw new CustomException(GlobalExceptionCode.INVALID_MESSAGE_FORMAT);
        }
    }

}
