package socket_server.domain.game.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socket_server.common.exception.ErrorType;
import socket_server.domain.game.model.GamePlayer;
import socket_server.domain.game.repository.GameRepository;
import socket_server.domain.room.model.RoomUserInfo;
import socket_server.domain.room.repository.RoomUserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GamePlayerService {
    /**
     * 도메인 데이터 GamePlayer 처리 담당.
     */
    private final RoomUserRepository roomUserRepository;

    public List<GamePlayer> getGamePlayersFromRoom(String roomId, ErrorType errorType) {
        return roomUserRepository.findUsersByRoomId(roomId, errorType).stream().map(RoomUserInfo::toGamePlayer).toList();
    }

}
