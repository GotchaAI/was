package socket_server.domain.game.repository;

import gotcha_common.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import socket_server.common.exception.ErrorType;
import socket_server.common.exception.SocketCustomException;
import socket_server.common.exception.game.GameExceptionCode;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.game.meta.GameMeta;
import socket_server.domain.game.meta.WordMeta;
import socket_server.domain.game.model.*;
import socket_server.domain.game.meta.RoundMeta;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
public class GameRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final JsonSerializer jsonSerializer;
    /**
     * <pre>
     * game:{roomId} (HASH) // List Player, List Round 빼고 저장
     * ├── gameType, difficulty, currentRound, totalRounds, aiScore, status
     *
     * game:{roomId}:players (STRING, JSON)
     * └── [{"playerUuid":"p1","nickname":"user1","score":10}, {"playerUuid":"p2",...}]
     *
     * game:{roomId}:rounds (STRING, JSON) List Word 빼고 저장
     * └── [{"roundIndex":1,"drawingEndTime":123,"roundWinner":"AI"}, ...]
     *
     * game:{roomId}:round:{roundIndex}:words (STRING, JSON) List Guess 빼고 저장
     * └── [{"wordIndex":0,"word":"cat","drawerUuid":"p1"}, {"wordIndex":1,...}]
     *
     * game:{roomId}:round:{roundIndex}:word:{wordIndex}:guesses (LIST)
     * └──
     * </pre>
     */
    public GameRepository(@Qualifier("socketStringRedisTemplate") RedisTemplate<String, String> redisTemplate,
                          JsonSerializer jsonSerializer) {
        this.redisTemplate = redisTemplate;
        this.jsonSerializer = jsonSerializer;
    }

    /**
     * game:{roomId}
     */
    public static String getGameKey(String roomId) {
        return "game:" + roomId;
    }


    /**
     * Game 메타데이터만 저장(List GamePlayers, List Rounds 제외)
     */
    public void saveGameMeta(GameMeta gameMeta){
        Map<String, Object> gameData = Map.of(
                "gameType", gameMeta.getGameType().name(),
                "difficulty", gameMeta.getDifficulty().name(),
                "currentRound", String.valueOf(gameMeta.getCurrentRound()),
                "gameStatus", String.valueOf(gameMeta.getGameStatus()),
                "totalRounds", String.valueOf(gameMeta.getTotalRounds()),
                "aiScore", String.valueOf(gameMeta.getAiScore())
        );

        redisTemplate.opsForHash().putAll(getGameKey(gameMeta.getRoomId()), gameData);

        log.info("Game {} saved", gameData);
    }

    /**
     * Game 메타데이터만 조회(List GamePlayers, List Rounds 제외)
     */
    public Map<Object, Object> findGameMeta(String roomId) {
        String key = getGameKey(roomId);
        return redisTemplate.opsForHash().entries(key);
    }





}
