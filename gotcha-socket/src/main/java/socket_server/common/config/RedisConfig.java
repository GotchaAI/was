//package socket_server.common.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.listener.PatternTopic;
//import org.springframework.data.redis.listener.RedisMessageListenerContainer;
//import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
//import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//import socket_server.domain.chat.handler.ChattingPubSubHandler;
//import socket_server.domain.game.handler.GamePubSubHandler;
//
//import static socket_server.common.constants.WebSocketConstants.CHAT_PREFIX;
//import static socket_server.common.constants.WebSocketConstants.GAME_PREFIX;
//
//@EnableCaching
//@Configuration
//public class RedisConfig {
//
//    @Value("${spring.data.redis.host}")
//    private String host;
//
//    @Value("${spring.data.redis.port}")
//    private String port;
//
//    //Redis와의 연결을 생성.
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
//        redisStandaloneConfiguration.setHostName(host);
//        redisStandaloneConfiguration.setPort(Integer.parseInt(port));
//        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration);
//        return lettuceConnectionFactory;
//    }
//
//    //Redis Pub/Sub에서 메시지를 리스닝하는 컨테이너 -> (구독자가 어떤 메시지를 받을지 관리)
//    @Bean
//    public RedisMessageListenerContainer redisMessageListenerContainer(
//            RedisConnectionFactory connectionFactory,
//            MessageListenerAdapter chatListenerAdapter,
//            MessageListenerAdapter gameListenerAdapter) {
//
//        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory);
//
//        //컨테이너는 각 채널의 리스닝어뎁터를 받음 -> 해당 컨테이너가 모든 채널 관리가 가능함!
//        container.addMessageListener(chatListenerAdapter, new PatternTopic(CHAT_PREFIX+"*")); //전체 채팅 채널
//        container.addMessageListener(gameListenerAdapter, new PatternTopic(GAME_PREFIX + "*"));//인게임 관련 채널
//
//        return container;
//    }
//
//    // Redis에서 메시지를 수신하면 RedisSubscriber 클래스의 onMessage 메서드를 호출하도록 설정
//    @Bean
//    public MessageListenerAdapter chatListenerAdapter(ChattingPubSubHandler chattingSubscriber) {
//        return new MessageListenerAdapter(chattingSubscriber, "onMessage");
//    }
//
//    @Bean
//    public MessageListenerAdapter gameListenerAdapter(GamePubSubHandler gameSubscriber) {
//        return new MessageListenerAdapter(gameSubscriber, "onMessage");
//    }
//
//    @Bean
//    @Primary
//    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
//        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(redisConnectionFactory);
//
//        // 모든 직렬화 방식은 String 중심으로 처리
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new StringRedisSerializer());
//        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
//        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
//
//        return redisTemplate;
//    }
//
//    @Bean
//    public RedisTemplate <String, Object> redisTemplateObject(RedisConnectionFactory connectionFactory) {
//        RedisTemplate <String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(connectionFactory);
//
//        // Key, HashKey는 문자열로
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setHashKeySerializer(new StringRedisSerializer());
//
//        // JSON으로 직렬화
//        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer();
//
//        template.setValueSerializer(serializer);
//        template.setHashValueSerializer(serializer);
//
//        return template;
//    }
//
//
//}
