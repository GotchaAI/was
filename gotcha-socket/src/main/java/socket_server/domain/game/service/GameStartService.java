package socket_server.domain.game.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socket_server.common.exception.ErrorType;
import socket_server.common.util.JsonSerializer;
import socket_server.domain.game.dto.AIGameStartReq;
import socket_server.domain.game.dto.AISaysRes;
import socket_server.domain.game.enumType.GameStatus;
import socket_server.domain.game.meta.GameMeta;
import socket_server.domain.game.model.Game;
import socket_server.domain.game.model.GamePlayer;
import socket_server.domain.game.model.Round;
import socket_server.domain.game.model.Word;
import socket_server.domain.game.repository.GamePlayerRepository;
import socket_server.domain.game.repository.GameRepository;
import socket_server.domain.game.repository.RoundRepository;
import socket_server.domain.game.util.WordUtils;
import socket_server.domain.room.model.RoomMetadata;
import socket_server.domain.room.model.RoomUserInfo;
import socket_server.domain.room.repository.RoomUserRepository;
import socket_server.domain.room.service.RoomUserService;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class GameStartService {
    private final RoomUserService roomUserService;
    private final GamePlayerRepository gamePlayerRepository;
    private final AIClientService aiClientService;
    private final GameRepository gameRepository;
    private final RoundRepository roundRepository;
    private final GameBroadCaster gameBroadCaster;
    private final RoundStartService roundStartService;
    private final RoomUserRepository roomUserRepository;
    private final ErrorType GAME_ERROR = ErrorType.GAME;
    private final JsonSerializer jsonSerializer;

    public void startGame(String roomId, String userUuid, ErrorType errorType)  {
        // 1. 게임 시작 가능한지(레디 상태, 플레이어 수) check 후 방 메타정보 조회
        RoomMetadata roomMetadata = roomUserService.validateRoomOwnerAndGetRoomMetadata(roomId, userUuid);
        roomUserService.checkGameStart(roomId, roomMetadata.getGameType());

        //todo: 이미 진행중인 게임이 있다면?

        // 2. 게임 메타데이터 생성
        Game game = Game.builder().
                roomId(roomId).
                gameStatus(GameStatus.GAME_STARTED).
                gameType(roomMetadata.getGameType()).
                difficulty(roomMetadata.getDifficulty()).
                currentRound(0).
                totalRounds(roomMetadata.getRoundCount()).build();

        // 3. 게임 플레이어 정보 조회 후 연결
        List<GamePlayer> gamePlayers = roomUserRepository.findUsersByRoomId(roomId,errorType)
                .stream().map(RoomUserInfo::toGamePlayer).toList();
        game.setGamePlayers(gamePlayers);

        // 4. 라운드 정보 초기화 후 연결
        List<Round> rounds = initRounds(game.getTotalRounds(), gamePlayers);
        game.setRounds(rounds);

        // 5. Redis에 저장 : GameMeta, GamePlayers, Rounds
        saveGame(game);

        // 6. AI 서버 메시지 받아오기
        String aiSays = aiClientService.getGameStartMessage(roomId, new AIGameStartReq(gamePlayers.stream().map(GamePlayer::getNickname).toList()));

        // 7. 시작 이벤트 브로드캐스트
        gameBroadCaster.broadcastStartEvent(userUuid, roomId, new AISaysRes(game, aiSays));


        // 8. 5초 후 게임 시작(EntryPoint)
        roundStartService.startNextRound(roomId);

    }

    private void saveGame(Game game) {
        gameRepository.saveGameMeta(GameMeta.fromGame(game));
        savePlayers(game.getRoomId(), game.getGamePlayers());
        roundRepository.saveRoundMetasString(game.getRoomId(),
                jsonSerializer.serialize(game.getRounds().stream().map(Round::toRoundMeta).toList(), GAME_ERROR));

        for (Round round : game.getRounds()) {
            String wordsJson =
                    jsonSerializer.serialize(round.getWords().stream().map(Word::toWordMeta).toList(), GAME_ERROR);
            roundRepository.saveWordMetasString(game.getRoomId(), round.getRoundIndex(), wordsJson);
            // todo: guess?
        }
    }


    private void savePlayers(String roomId, List<GamePlayer> gamePlayers) {
        gamePlayerRepository.savePlayerUuids(roomId, gamePlayers.stream().map(GamePlayer::getPlayerUuid).toList());
        for(GamePlayer gamePlayer : gamePlayers) {
            String playerJson = jsonSerializer.serialize(gamePlayer, GAME_ERROR);
            gamePlayerRepository.saveGamePlayerString(roomId, gamePlayer.getPlayerUuid(), playerJson);
        }
    }

    /**
     * START
     */
    private List<Round> initRounds(int totalRounds, List<GamePlayer> gamePlayers) {
        List<Round> rounds = new ArrayList<>();
        List<Integer> indexes = WordUtils.getRandomIndexes(totalRounds * 2); // get random indexes, 플레이어는 항상 2명이라고 가정
        for(int i = 0; i < totalRounds; i++) {
            List<Word> words = new ArrayList<>();
            for(int j = 0; j < 2; j++){
                Word word = Word.builder()
                        .wordIndex(j)
                        .word(WordUtils.getEngWord(indexes.get(i * 2 + j)))
                        .drawerUuid(gamePlayers.get(j).getPlayerUuid())
                        .aiGuesses(new ArrayList<>())
                        .playerGuesses(new ArrayList<>())
                        .aiPredictions(new ArrayList<>()).build();
                words.add(word);
            }

            Round round = Round.builder().
                    roundIndex(i + 1).
                    currentWordIndex(0).
                    words(words).
                    build();
            rounds.add(round);
        }
        return rounds;
    }


}
