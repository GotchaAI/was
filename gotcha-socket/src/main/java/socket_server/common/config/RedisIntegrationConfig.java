package socket_server.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import socket_server.domain.personal.handler.PersonalPubSubHandler;
import socket_server.domain.room.handler.RoomPubSubHandler;

import static socket_server.common.constants.WebSocketConstants.*;

@Slf4j
@Configuration
@EnableIntegration
public class RedisIntegrationConfig {
    private final String REDIS_MESSAGE_SOURCE = "redis_messageSource";
    ObjectMapper objectMapper = new ObjectMapper();
    private final RoomPubSubHandler roomHandler;
    private final PersonalPubSubHandler personalPubSubHandler;

    public RedisIntegrationConfig(RoomPubSubHandler roomHandler, PersonalPubSubHandler personalPubSubHandler) {
        this.roomHandler = roomHandler;
        this.personalPubSubHandler=personalPubSubHandler;
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
    public MessageChannel personalMessageChannel (@Qualifier("redisExecutor") TaskExecutor exec) {
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
    public MessageProducer redisInboundAdapter(RedisConnectionFactory cf) {
        RedisInboundChannelAdapter adapter = new RedisInboundChannelAdapter(cf);
        adapter.setTopicPatterns(
                // 개인 유저 메시지
                PERSONAL_PREFIX +"*",
                // 채팅
                CHAT_ALL_CHANNEL,                     // /sub/chat/all
                CHAT_PRIVATE_CHANNEL + "*",          // /sub/chat/private/*
                CHAT_ROOM_CHANNEL + "*",             // /sub/chat/room/*

                // 대기방
                ROOM_LIST_INFO,                      // /sub/room/list/info
                ROOM_CREATE_INFO,                    // /sub/room/create/info
                ROOM_LEAVE + "*",                    // /sub/room/leave/*
                ROOM_UPDATE + "*",                   // /sub/room/update/*
                ROOM_JOIN + "*",                     // /sub/room/join/*


                // 게임
                GAME_READY_CHANNEL + "*",            // /sub/game/ready/*
                GAME_END_CHANNEL + "*",              // /sub/game/end/*
                GAME_INFO_CHANNEL + "*",             // /sub/game/info/*
                GAME_START_CHANNEL + "*"           // /sub/game/start/*
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
                    if (topic.startsWith(PERSONAL_PREFIX)) return "personalMessageChannel";
                    if (topic.startsWith(GAME_PREFIX)) return "gameMessageChannel";
                    if (topic.startsWith(CHAT_PREFIX)) return "chatMessageChannel";
                    if (topic.startsWith(ROOM_PREFIX)) return "roomMessageChannel";
                    return "unknownMessageChannel";
                })
                .get();
    }

    @Bean
    public IntegrationFlow personalMessageFlow() {
        return IntegrationFlow.from("personalMessageChannel")
                .handle((msg, headers) -> {
                    RedisMessage redisMessage = objectMapper.convertValue(msg, RedisMessage.class);
                    log.info("👤 [개인 메시지] user={}, topic={}, payload={}",
                            redisMessage.userId(), redisMessage.topic(), redisMessage.payload());
                    personalPubSubHandler.onMessage(redisMessage.topic()+redisMessage.userId(), redisMessage);
                    return null;
                }).get();
    }

    @Bean
    public IntegrationFlow roomMessageFlow() {
        return IntegrationFlow.from("roomMessageChannel")
                .handle((msg, headers) -> {
                    RedisMessage redisMessage = objectMapper.convertValue(msg, RedisMessage.class);
                    log.info("📦 [방 메시지] topic={}, user={}, payload={}",
                            redisMessage.topic(), redisMessage.userId(), redisMessage.payload());
                    roomHandler.onMessage(redisMessage.topic(), redisMessage);
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

                            template.convertAndSend(ERROR_CHANNEL_PREFIX+userId+ERROR_CHANEL, dto);
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
