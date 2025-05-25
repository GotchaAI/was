package socket_server.domain.game.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.game.enumType.Difficulty;
import socket_server.domain.game.enumType.GameType;
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
     * @param redisTemplate
     */
    public GameRepository(@Qualifier("socketStringRedisTemplate") RedisTemplate<String, String> redisTemplate,
                          JsonSerializer jsonSerializer) {
        this.redisTemplate = redisTemplate;
        this.jsonSerializer = jsonSerializer;
    }

    /**
     * game:{roomId}
     * @param roomId
     * @return
     */
    public String getGameKey(String roomId) {
        return "game:" + roomId;
    }


    /**
     * Game 메타데이터만 저장(List GamePlayers, List Rounds 제외)
     * @param gameMeta
     */
    public void saveGameMeta(GameMeta gameMeta){
        Map<String, Object> gameData = Map.of(
                "gameType", gameMeta.getGameType().name(),
                "difficulty", gameMeta.getDifficulty().name(),
                "currentRound", String.valueOf(gameMeta.getCurrentRound()),
                "totalRounds", String.valueOf(gameMeta.getTotalRounds()),
                "aiScore", String.valueOf(gameMeta.getAiScore())
        );

        redisTemplate.opsForHash().putAll(getGameKey(gameMeta.getRoomId()), gameData);

        log.info("Game {} saved", gameData);
    }

    /**
     * Game 메타데이터만 조회(List GamePlayers, List Rounds 제외)
     * @param roomId
     * @return
     */
    public Optional<GameMeta> getGameMeta(String roomId) {
        String key = getGameKey(roomId);
        Map<Object, Object> gameDataMap = redisTemplate.opsForHash().entries(key);
        if (gameDataMap.isEmpty()) {
            return Optional.empty();
        }
        GameMeta gameMeta = GameMeta.fromRedisMap(roomId, gameDataMap);
        return Optional.of(gameMeta);
    }

    /**
     * game:{roomId}:players
     * @param roomId
     * @return
     */
    private String getGamePlayersKey(String roomId) {
        return getGameKey(roomId) + ":players";
    }

    /**
     * GamePlayers 저장
     * @param roomId
     * @param players
     */
    public void savePlayers(String roomId, List<GamePlayer> players) {
        String key = getGamePlayersKey(roomId);
        String playersJson = jsonSerializer.serialize(players);
        redisTemplate.opsForValue().set(key, playersJson);
        log.info("Players {} saved", playersJson);
    }

    /**
     * GamePlayers 조회
     * @param roomId
     * @return
     */
    public List<GamePlayer> getPlayers(String roomId) {
        String key = getGamePlayersKey(roomId);
        String playersJson = redisTemplate.opsForValue().get(key);
        if (playersJson == null) {
            return List.of();
        }
        return jsonSerializer.deserializeList(playersJson, GamePlayer.class);
    }

    /**
     * game:{roomId}:rounds
     * @param roomId
     * @return
     */
    private String getGameRoundsKey(String roomId) {
        return getGameKey(roomId) + ":rounds";
    }

    /**
     * Round 메타정보 List 저장 (roundIndex, drawingEndTime, roundWinner)
     * @param roomId
     * @param rounds
     */
    public void saveRoundMetas(String roomId, List<Round> rounds) {
        String key = getGameRoundsKey(roomId);
        List<RoundMeta> roundMetas = rounds.stream().map(Round::toRoundMeta).toList();
        String roundsJson = jsonSerializer.serialize(roundMetas);
        redisTemplate.opsForValue().set(key, roundsJson);
        log.info("RoundMetas {} saved", roundsJson);
    }

    /**
     * Round 메타정보 List 조회 (roundIndex, drawingEndTime, roundWinner)
     * @param roomId
     * @return
     */
    public List<RoundMeta> getRoundMetas(String roomId) {
        String key = getGameRoundsKey(roomId);
        String roundsJson = redisTemplate.opsForValue().get(key);
        if (roundsJson == null) {
            return List.of();
        }
        return jsonSerializer.deserializeList(roundsJson, RoundMeta.class);
    }

    /**
     * game:{roomId}:round:{roundIndex}:words
     * @param roomId
     * @param roundIndex
     * @return
     */
    private String getRoundWordsKey(String roomId, int roundIndex) {
        return getGameRoundsKey(roomId) + roundIndex + ":words";
    }

    /**
     * Word 메타정보 List 저장(wordIndex, word, drawerUuid)
     * @param roomId
     * @param roundIndex
     * @param words
     */
    public void saveWords(String roomId, int roundIndex, List<Word> words) {
        String key = getRoundWordsKey(roomId, roundIndex);
        List<WordMeta> wordMetas = words.stream().map(Word::toWordMeta).toList();
        String wordsJson = jsonSerializer.serialize(wordMetas);
        redisTemplate.opsForValue().set(key, wordsJson);
        log.info("Words {} saved", wordsJson);
    }

    /**
     * Word 메타정보 List 조회(wordIndex, word, drawerUuid)
     * @param roomId
     * @param roundIndex
     * @return
     */
    public List<WordMeta> getWords(String roomId, int roundIndex) {
        String key = getRoundWordsKey(roomId, roundIndex);
        String wordsJson = redisTemplate.opsForValue().get(key);
        if (wordsJson == null) {
            return List.of();
        }
        return jsonSerializer.deserializeList(wordsJson, WordMeta.class);
    }


    private String getGuessKey(String roomId, int roundIndex, int wordIndex){
        return getRoundWordsKey(roomId, roundIndex) + wordIndex + ":guesses";
    }

    /**
     * GUESS 정보 추가
     * @param roomId
     * @param roundIndex
     * @param wordIndex
     * @param guess
     */
    public void addGuess(String roomId, int roundIndex, int wordIndex, Guess guess){
        String key = getGuessKey(roomId, roundIndex, wordIndex);
        String guessJson = jsonSerializer.serialize(guess);
        redisTemplate.opsForList().rightPush(key, guessJson);
        log.info("Guess {} saved", guessJson);
    }

    /**
     * List Guess 조회
     * @param roomId
     * @param roundIndex
     * @param wordIndex
     * @return
     */
    public List<Guess> getGuesses(String roomId, int roundIndex, int wordIndex){
        String key = getGuessKey(roomId, roundIndex, wordIndex);
        List<String> guessesJsonList = redisTemplate.opsForList().range(key, 0, -1);
        if (guessesJsonList == null || guessesJsonList.isEmpty()) {
            return List.of();
        }
        return jsonSerializer.deserializeList(guessesJsonList, Guess.class);
    }

    /**
     * 게임 전체 정보 조회
     */
    public Optional<Game> getGame(String roomId) {
        Optional<Game> gameOpt = getGameMeta(roomId);
        if (gameOpt.isEmpty()) {
            return Optional.empty();
        }
        Game game = gameOpt.get();
        // Round 가져와서 roundIndex로 WordMeta 조회
        List<Round> rounds = getRoundMetas(roomId).stream().map(RoundMeta::toRound).toList();
        for(Round round: rounds) {
            // Word 가져와서 wordIndex로 Guess 조회
            List<Word> words = getWords(roomId, round.getRoundIndex()).stream().map(WordMeta::toWord).toList();
            for(Word word: words) {
                List<Guess> guesses = getGuesses(roomId, round.getRoundIndex(), word.getWordIndex());
                word.setGuesses(guesses);
            }
            round.setWords(words);
        }
        game.setRounds(rounds);
        return Optional.of(game);
    }

}
