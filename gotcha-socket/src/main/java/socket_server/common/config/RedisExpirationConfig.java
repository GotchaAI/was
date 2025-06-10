package socket_server.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import socket_server.common.listener.RedisKeyExpirationListener;
import socket_server.domain.room.service.RoomUserService;

@Configuration
@RequiredArgsConstructor
public class RedisExpirationConfig {

    private final RedisConnectionFactory connectionFactory;

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }

    @Bean
    public RedisKeyExpirationListener redisKeyExpirationListener(RedisMessageListenerContainer container, RoomUserService roomUserService) {
        return new RedisKeyExpirationListener(container, roomUserService);
    }
}

