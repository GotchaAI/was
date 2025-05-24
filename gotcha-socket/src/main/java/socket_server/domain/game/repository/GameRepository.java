package socket_server.domain.game.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.game.enumType.Difficulty;
import socket_server.domain.game.enumType.GameType;
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
     * game:{gameId} (HASH) // List Player, List Round 빼고 저장
     * ├── gameType, difficulty, currentRound, totalRounds, aiScore, status
     *
     * game:{gameId}:players (STRING, JSON)
     * └── [{"playerUuid":"p1","nickname":"user1","score":10}, {"playerUuid":"p2",...}]
     *
     * game:{gameId}:rounds (STRING, JSON) List Word 빼고 저장
     * └── [{"roundIndex":1,"drawingEndTime":123,"roundWinner":"AI"}, ...]
     *
     * game:{gameId}:round:{roundIndex}:words (STRING, JSON) List Guess 빼고 저장
     * └── [{"wordIndex":0,"word":"cat","drawerUuid":"p1"}, {"wordIndex":1,...}]
     *
     * game:{gameId}:round:{roundIndex}:word:{wordIndex}:guesses (LIST)
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
     * game:{gameId}
     * @param gameId
     * @return
     */
    public String getGameKey(String gameId) {
        return "game:" + gameId;
    }


    /**
     * Game 메타데이터만 저장(List GamePlayers, List Rounds 제외)
     * @param game
     */
    public void saveGameMeta(Game game){
        Map<String, Object> gameData = Map.of(
                "gameType", game.getGameType().name(),
                "difficulty", game.getDifficulty().name(),
                "currentRound", game.getCurrentRound(),
                "totalRounds", game.getTotalRounds(),
                "aiScore", game.getAiScore()
        );

        redisTemplate.opsForHash().putAll(getGameKey(game.getGameId()), gameData);

        log.info("Game {} saved", gameData);
    }

    /**
     * Game 메타데이터 조회
     * @param gameId
     * @return
     */
    public Optional<Game> getGameMeta(String gameId) {
        String key = getGameKey(gameId);
        Map<Object, Object> gameData = redisTemplate.opsForHash().entries(key);
        if (gameData.isEmpty()) {
            return Optional.empty();
        }
        Game game = Game.builder()
                .gameId(gameId)
                .gameType(GameType.valueOf((String) gameData.get("gameType")))
                .difficulty(Difficulty.valueOf((String) gameData.get("difficulty")))
                .currentRound((Integer) gameData.get("currentRound"))
                .totalRounds((Integer) gameData.get("totalRounds"))
                .aiScore((Integer) gameData.get("aiScore"))
                .build();
        return Optional.of(game);
    }

    /**
     * game:{gameId}:players
     * @param gameId
     * @return
     */
    private String getGamePlayersKey(String gameId) {
        return getGameKey(gameId) + ":players";
    }

    /**
     * GamePlayers 저장
     * @param gameId
     * @param players
     */
    public void savePlayers(String gameId, List<GamePlayer> players) {
        String key = getGamePlayersKey(gameId);
        String playersJson = jsonSerializer.serialize(players);
        redisTemplate.opsForValue().set(key, playersJson);
        log.info("Players {} saved", playersJson);
    }

    /**
     * GamePlayers 조회
     * @param gameId
     * @return
     */
    public List<GamePlayer> getPlayers(String gameId) {
        String key = getGamePlayersKey(gameId);
        String playersJson = redisTemplate.opsForValue().get(key);
        if (playersJson == null) {
            return List.of();
        }
        return jsonSerializer.deserializeList(playersJson, GamePlayer.class);
    }

    /**
     * game:{gameId}:rounds
     * @param gameId
     * @return
     */
    private String getGameRoundsKey(String gameId) {
        return getGameKey(gameId) + ":rounds";
    }

    /**
     * Round 메타정보 List 저장 (roundIndex, drawingEndTime, roundWinner)
     * @param gameId
     * @param rounds
     */
    public void saveRoundMetas(String gameId, List<Round> rounds) {
        String key = getGameRoundsKey(gameId);
        List<RoundMeta> roundMetas = rounds.stream().map(Round::toRoundMeta).toList();
        String roundsJson = jsonSerializer.serialize(roundMetas);
        redisTemplate.opsForValue().set(key, roundsJson);
        log.info("RoundMetas {} saved", roundsJson);
    }

    /**
     * Round 메타정보 List 조회 (roundIndex, drawingEndTime, roundWinner)
     * @param gameId
     * @return
     */
    public List<RoundMeta> getRoundMetas(String gameId) {
        String key = getGameRoundsKey(gameId);
        String roundsJson = redisTemplate.opsForValue().get(key);
        if (roundsJson == null) {
            return List.of();
        }
        return jsonSerializer.deserializeList(roundsJson, RoundMeta.class);
    }

    /**
     * game:{gameId}:round:{roundIndex}:words
     * @param gameId
     * @param roundIndex
     * @return
     */
    private String getRoundWordsKey(String gameId, int roundIndex) {
        return getGameRoundsKey(gameId) + roundIndex + ":words";
    }

    /**
     * Word 메타정보 List 저장(wordIndex, word, drawerUuid)
     * @param gameId
     * @param roundIndex
     * @param words
     */
    public void saveWords(String gameId, int roundIndex, List<Word> words) {
        String key = getRoundWordsKey(gameId, roundIndex);
        List<WordMeta> wordMetas = words.stream().map(Word::toWordMeta).toList();
        String wordsJson = jsonSerializer.serialize(wordMetas);
        redisTemplate.opsForValue().set(key, wordsJson);
        log.info("Words {} saved", wordsJson);
    }

    /**
     * Word 메타정보 List 조회(wordIndex, word, drawerUuid)
     * @param gameId
     * @param roundIndex
     * @return
     */
    public List<WordMeta> getWords(String gameId, int roundIndex) {
        String key = getRoundWordsKey(gameId, roundIndex);
        String wordsJson = redisTemplate.opsForValue().get(key);
        if (wordsJson == null) {
            return List.of();
        }
        return jsonSerializer.deserializeList(wordsJson, WordMeta.class);
    }


    private String getGuessKey(String gameId, int roundIndex, int wordIndex){
        return getRoundWordsKey(gameId, roundIndex) + wordIndex + ":guesses";
    }

    /**
     * GUESS 정보 추가
     * @param gameId
     * @param roundIndex
     * @param wordIndex
     * @param guess
     */
    public void addGuess(String gameId, int roundIndex, int wordIndex, Guess guess){
        String key = getGuessKey(gameId, roundIndex, wordIndex);
        String guessJson = jsonSerializer.serialize(guess);
        redisTemplate.opsForList().rightPush(key, guessJson);
        log.info("Guess {} saved", guessJson);
    }

    /**
     * List Guess 조회
     * @param gameId
     * @param roundIndex
     * @param wordIndex
     * @return
     */
    public List<Guess> getGuesses(String gameId, int roundIndex, int wordIndex){
        String key = getGuessKey(gameId, roundIndex, wordIndex);
        List<String> guessesJsonList = redisTemplate.opsForList().range(key, 0, -1);
        if (guessesJsonList == null || guessesJsonList.isEmpty()) {
            return List.of();
        }
        return jsonSerializer.deserializeList(guessesJsonList, Guess.class);
    }

    /**
     * 게임 전체 정보 조회
     */
    public Optional<Game> getGame(String gameId) {
        Optional<Game> gameOpt = getGameMeta(gameId);
        if (gameOpt.isEmpty()) {
            return Optional.empty();
        }
        Game game = gameOpt.get();
        // Round 가져와서 roundIndex로 WordMeta 조회
        List<Round> rounds = getRoundMetas(gameId).stream().map(RoundMeta::toRound).toList();
        for(Round round: rounds) {
            // Word 가져와서 wordIndex로 Guess 조회
            List<Word> words = getWords(gameId, round.getRoundIndex()).stream().map(WordMeta::toWord).toList();
            for(Word word: words) {
                List<Guess> guesses = getGuesses(gameId, round.getRoundIndex(), word.getWordIndex());
                word.setGuesses(guesses);
            }
            round.setWords(words);
        }
        game.setRounds(rounds);
        return Optional.of(game);
    }

}
