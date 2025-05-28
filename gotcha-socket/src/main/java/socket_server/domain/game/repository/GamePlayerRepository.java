package socket_server.domain.game.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.game.model.GamePlayer;
import socket_server.domain.game.model.Round;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Repository
public class GamePlayerRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final JsonSerializer jsonSerializer;
    public GamePlayerRepository(@Qualifier("socketStringRedisTemplate") RedisTemplate<String, String> redisTemplate,
                                JsonSerializer jsonSerializer) {
        this.redisTemplate = redisTemplate;
        this.jsonSerializer = jsonSerializer;
    }

    //todo: game:{roomId}:players 는 SET,
    //todo: player:{roomId}:{uuid} 는 STRING (uuid, USERNAME), 데이터 구조 수정 필요

    /**
     * game:{roomId}:players
     */
    public static String getGamePlayersKey(String roomId) {
        return GameRepository.getGameKey(roomId) + ":players";
    }

    /**
     * GamePlayers 저장
     */
    public void savePlayers(String roomId, List<GamePlayer> gamePlayers) {
        String key = getGamePlayersKey(roomId);
        redisTemplate.opsForSet().add(key, gamePlayers.stream().map(GamePlayer::getPlayerUuid).toList().toArray(new String[0]));
        for(GamePlayer gamePlayer : gamePlayers) {
            savePlayer(roomId, gamePlayer);
        }
    };

    /**
     * GamePlayers 조회
     */
    public List<GamePlayer> findPlayersByRoomId(String roomId) {
        String key = getGamePlayersKey(roomId);
        Set<String> uuids = redisTemplate.opsForSet().members(key);
        if (uuids == null || uuids.isEmpty()) {
            return List.of();
        }

        List<GamePlayer> players = new ArrayList<>();
        for (String uuid : uuids) {
            GamePlayer player = findPlayerByUuid(roomId, uuid);
            if (player != null) {
                players.add(player);
            }
        }
        return players;
    }

    /**
     * player:{roomId}:{uuid}
     */
    public static String getPlayerKey(String roomId, String uuid) {
        return GameRepository.getGameKey(roomId) + ":" + uuid;
    }

    public GamePlayer findPlayerByUuid(String roomId, String uuid) {
        String key = getPlayerKey(roomId, uuid);
        String playerJson = redisTemplate.opsForValue().get(key);
        if (playerJson == null) {
            return null;
        }
        return jsonSerializer.deserialize(playerJson, GamePlayer.class);
    }

    public void savePlayer(String roomId, GamePlayer player) {
        String key = getPlayerKey(roomId, player.getPlayerUuid());
        String playerJson = jsonSerializer.serialize(player);
        redisTemplate.opsForValue().set(key, playerJson);
        log.info("Player {} saved", playerJson);
    }


    /**
     * game:{roomId}:round:{roundIndex}:scores
     */
    public static String getScoreKey(String roomId, int roundIndex) {
        return RoundRepository.getGameRoundsKey(roomId) + ":" + roundIndex + ":scores";
    }

    public void saveScoreByUuid(String roomId, String uuid, int roundIndex,  int score) {
        String key = getScoreKey(roomId, roundIndex);
        redisTemplate.opsForHash().put(key, uuid, String.valueOf(score));
    }

    public int findScoreByUuid(String roomId, String uuid, int roundIndex) {
        String key = getScoreKey(roomId, roundIndex);
        String score = (String) redisTemplate.opsForHash().get(key, uuid);
        if (score == null) {
            return 0;
        }
        return Integer.parseInt(score);
    }

    public Map findScores(String roomId, int roundIndex) {
        String key = getScoreKey(roomId, roundIndex);
        return redisTemplate.opsForHash().entries(key);
    }

}