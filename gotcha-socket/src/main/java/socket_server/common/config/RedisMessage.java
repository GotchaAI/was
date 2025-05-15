package socket_server.common.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RedisMessage(
        String userId,
        String topic,
        Object payload)
{}
