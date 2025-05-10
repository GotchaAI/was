package socket_server.common.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

//입력된 채널과 메시지를 처리한 후 (sub), 처리된 결과를 해당 채널의 다른 구독자에게 전파 (pub)
@Service
public abstract class PubSubHandler implements MessageListener {
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 변환을 위한 ObjectMapper

    protected final SimpMessagingTemplate messagingTemplate;
    protected final Map<String, BiConsumer<String, String>> handlers;

    public PubSubHandler(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.handlers = new HashMap<>();
        initHandlers();
    }

    // 각 하위 클래스에서 핸들러 초기화
    protected abstract void initHandlers();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel(), StandardCharsets.UTF_8);
        String msg = message.toString();

        // 채널에 맞는 처리 메소드 호출
        handlers.entrySet().stream()
                .filter(entry -> extractChannelType(channel).equals(entry.getKey()))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(this::handleUnknownChannel)
                .accept(channel, msg);
    }

    // 채널 타입 추출 (채널 이름에서 구체적인 타입 추출)
    private String extractChannelType(String channel) {
        return channel.substring(0, channel.lastIndexOf("/") + 1);
    }

    // 기본 핸들러: 알 수 없는 채널에 대한 처리
    private void handleUnknownChannel(String channel, String message) {
        System.out.println("[알 수 없는 채널] " + channel + " 메시지: " + message);
    }

    //todo : 소켓 내 에러 처리 로직 추가.
    protected <T> T convertMessageToDto(String message, Class<T> dtoClass) {
        try {
            return objectMapper.readValue(message, dtoClass);
        } catch (Exception e) {
            System.err.println("[JSON 변환 오류] " + e.getMessage());
            return null;
        }
    }
}