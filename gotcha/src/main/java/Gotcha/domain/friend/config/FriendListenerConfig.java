package Gotcha.domain.friend.config;

import Gotcha.domain.friend.listener.FriendOfflineNotifier;
import Gotcha.domain.friend.repository.FriendRepository;
import gotcha_user.service.UserService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import socket_server.domain.friend.service.FriendSocketService;

@Configuration
public class FriendListenerConfig {

    @Bean
    @ConditionalOnMissingBean(FriendOfflineNotifier.class)
    public FriendOfflineNotifier friendOfflineNotifier(
            UserService userService,
            FriendRepository friendRepository,
            FriendSocketService friendSocketService
    ) {
        return new FriendOfflineNotifier(userService, friendRepository, friendSocketService);
    }
}
