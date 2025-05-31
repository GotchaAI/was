package socket_server.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import gotcha_common.exception.CustomException;
import gotcha_common.exception.ExceptionRes;
import gotcha_common.exception.exceptionCode.GlobalExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.GenericHandler;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.redis.inbound.RedisInboundChannelAdapter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import socket_server.common.exception.ErrorType;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.game.handler.GamePubSubHandler;
import socket_server.domain.lobby.handler.LobbyPubSubHandler;
import socket_server.domain.room.handler.RoomPubSubHandler;

import static socket_server.common.constants.WebSocketConstants.*;

@Slf4j
@Configuration
@EnableIntegration
public class RedisIntegrationConfig {
    private final String REDIS_MESSAGE_SOURCE = "redis_messageSource";
    ObjectMapper objectMapper = new ObjectMapper();
    private final RoomPubSubHandler roomHandler;
    private final GamePubSubHandler gamePubSubHandler;
    private final LobbyPubSubHandler lobbyPubSubHandler;

    public RedisIntegrationConfig(RoomPubSubHandler roomHandler, GamePubSubHandler gamePubSubHandler, LobbyPubSubHandler lobbyPubSubHandler) {
        this.roomHandler = roomHandler;
        this.gamePubSubHandler = gamePubSubHandler;
        this.lobbyPubSubHandler = lobbyPubSubHandler;
    }

    @Bean("redisExecutor")
    public TaskExecutor redisExecutor() {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(20);
        exec.setMaxPoolSize(200);
        exec.setQueueCapacity(1000);
        exec.setThreadNamePrefix("redis-exec-");
        exec.initialize();
        return exec;
    }

    @Bean("broadcastExecutor")
    public TaskExecutor broadcastExecutor() {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(10);
        exec.setMaxPoolSize(50);
        exec.setQueueCapacity(500);
        exec.setThreadNamePrefix("broadcast-exec-");
        exec.initialize();
        return exec;
    }

    // Channels
    @Bean
    public MessageChannel redisInputChannel(@Qualifier("redisExecutor") TaskExecutor exec) {
        return new ExecutorChannel(exec);
    }

    @Bean
    public MessageChannel roomMessageChannel(@Qualifier("redisExecutor") TaskExecutor exec) {
        return new ExecutorChannel(exec);
    }

    @Bean
    public MessageChannel unknownMessageChannel(@Qualifier("redisExecutor") TaskExecutor exec) {
        return new ExecutorChannel(exec);
    }

    @Bean
    public MessageChannel redisErrorChannel(@Qualifier("redisExecutor") TaskExecutor exec) {
        return new ExecutorChannel(exec);
    }

    @Bean
    public MessageChannel chatMessageChannel(@Qualifier("redisExecutor") TaskExecutor exec) {
        return new ExecutorChannel(exec);
    }

    @Bean
    public MessageChannel gameMessageChannel(@Qualifier("redisExecutor") TaskExecutor exec) {
        return new ExecutorChannel(exec);
    }

    @Bean
    public MessageChannel lobbyMessageChannel(@Qualifier("redisExecutor") TaskExecutor exec) {
        return new ExecutorChannel(exec);
    }

    @Bean
    public MessageProducer redisInboundAdapter(RedisConnectionFactory cf) {
        RedisInboundChannelAdapter adapter = new RedisInboundChannelAdapter(cf);
        adapter.setTopicPatterns(
                // 채팅
                CHAT_ALL_CHANNEL,                     // /sub/chat/all
                CHAT_PRIVATE_CHANNEL + "*",          // /sub/chat/private/*
                CHAT_ROOM_CHANNEL + "*",             // /sub/chat/room/*

                // 대기방
                ROOM_PREFIX + "*",

                // 게임
                GAME_PREFIX + "*",                   // /sub/game/*
                GAME_READY_CHANNEL + "*",            // /sub/game/ready/*
                GAME_END_CHANNEL + "*",              // /sub/game/end/*
                GAME_INFO_CHANNEL + "*",             // /sub/game/info/*
                GAME_START_CHANNEL + "*",          // /sub/game/start/*

                //로비
                LOBBY_JOIN_CHANNEL + "*",         //sub/lobby/join/ + roomId
                LOBBY_ROOM_CREATE_CHANNEL + "*",  //sub/lobby/create/ + uuid
                LOBBY_ROOM_LIST_EVENT + "*"       //sub/lobby/list/event
        );
        adapter.setSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        adapter.setOutputChannel(redisInputChannel(redisExecutor()));
        adapter.setErrorChannel(redisErrorChannel(redisExecutor()));
        return adapter;
    }

    @Bean
    public IntegrationFlow redisRoutingFlow() {
        return IntegrationFlow.from("redisInputChannel")
                .route(Message.class, msg -> {
                    String topic = (String) msg.getHeaders().get(REDIS_MESSAGE_SOURCE);
                    if (topic.startsWith(GAME_PREFIX)) return "gameMessageChannel";
                    if (topic.startsWith(CHAT_PREFIX)) return "chatMessageChannel";
                    if (topic.startsWith(ROOM_PREFIX)) return "roomMessageChannel";
                    if (topic.startsWith(LOBBY_PREFIX)) return "lobbyMessageChannel";
                    return "unknownMessageChannel";
                })
                .get();
    }

    @Bean
    public IntegrationFlow chatMessageFlow(SimpMessagingTemplate template) {
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return IntegrationFlow.from("chatMessageChannel")
                .handle((payload, headers) -> {
                    RedisMessage redisMessage = mapper.convertValue(payload, RedisMessage.class);

                    log.info("💬 [채팅 메시지] topic={}, user={}, payload={}",
                            redisMessage.topic(), redisMessage.userId(), redisMessage.payload());

                    template.convertAndSend(redisMessage.topic(), redisMessage.payload());

                    return null;
                }).get();
    }

    @Bean
    public IntegrationFlow roomMessageFlow(JsonSerializer jsonSerializer) {
        return IntegrationFlow.from("roomMessageChannel")
                .handle((msg, headers) -> {
                    RedisMessage redisMessage = jsonSerializer.deserialize(msg, RedisMessage.class, ErrorType.ROOM);
                    log.info("📦 [방 메시지] topic={}, user={}, payload={}",
                            redisMessage.topic(), redisMessage.userId(), redisMessage.payload());
                    roomHandler.onMessage(redisMessage.topic(), redisMessage);
                    return null;
                }).get();
    }

    @Bean
    public IntegrationFlow gameMessageFlow(JsonSerializer jsonSerializer) {
        return IntegrationFlow.from("gameMessageChannel")
                .handle((msg, headers) -> {
                    RedisMessage redisMessage = jsonSerializer.deserialize(msg, RedisMessage.class, ErrorType.GAME);
                    log.info("🎮 [게임 메시지] topic={}, user={}, payload={}",
                            redisMessage.topic(), redisMessage.userId(), redisMessage.payload());
                    gamePubSubHandler.onMessage(redisMessage.topic(), redisMessage);
                    return null;
                }).get();
    }

    @Bean
    public IntegrationFlow lobbyMessageFlow(JsonSerializer jsonSerializer) {
        return IntegrationFlow.from("lobbyMessageChannel")
                .handle((msg, headers) -> {
                    RedisMessage redisMessage = jsonSerializer.deserialize(msg, RedisMessage.class, ErrorType.LOBBY);
                    log.info("📚 [로비 메시지] topic={}, user={}, payload={}",
                            redisMessage.topic(), redisMessage.userId(), redisMessage.payload());
                    lobbyPubSubHandler.onMessage(redisMessage.topic(), redisMessage);
                    return null;
                }).get();
    }

    @Bean
    public IntegrationFlow unknownMessageFlow() {
        return IntegrationFlow.from("unknownMessageChannel")
                .handle((GenericHandler<Object>) (payload, headers) -> {
                    log.warn("❓ [알 수 없는 메시지] payload={}, headers={}", payload, headers);
                    return null;
                }).get();
    }


    @Bean
    public IntegrationFlow redisErrorFlow(SimpMessagingTemplate template) {
        return IntegrationFlow.from("redisErrorChannel")
                .handle((GenericHandler<Object>) (payload, headers) -> {
                    log.debug("❗ redisErrorFlow triggered. Payload type: {}, Headers: {}", payload.getClass(), headers);

                    Throwable t;
                    if (payload instanceof ErrorMessage em) {
                        t = em.getPayload();
                    } else if (payload instanceof Throwable th) {
                        t = th;
                    } else {
                        return null;
                    }

                    try {
                        if (t.getMessage() != null && t.getMessage().contains("userId")) {
                            RedisMessage redisMessage = objectMapper.readValue(t.getMessage(), RedisMessage.class);
                            String userId = redisMessage.userId();

                            ExceptionRes dto = (t instanceof CustomException ce)
                                    ? ExceptionRes.from(ce.getExceptionCode())
                                    : ExceptionRes.from(GlobalExceptionCode.INTERNAL_SERVER_ERROR);

                            template.convertAndSend(ERROR_CHANNEL_PREFIX+userId+ERROR_DEFAULT_CHANEL, dto);
                            log.debug("🚨 에러 메시지 전송 완료 → /user/{}/queue/errors", userId);
                        } else {
                            log.warn("❌ [redisErrorFlow] userId 추출 실패. 메시지 내용: {}", t.getMessage());
                        }
                    } catch (Exception e) {
                        log.error("❌ [redisErrorFlow] RedisMessage 역직렬화 실패", e);
                    }

                    return null;
                })
                .get();
    }
}
