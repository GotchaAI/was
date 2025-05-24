package socket_server.domain.game.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import socket_server.common.util.IDGenerator;

@Service
@AllArgsConstructor
public class GameService {
    /**
     * 1. 게임 시작
     */
    private final IDGenerator idGenerator;


    public void startGame() {

    }



}
