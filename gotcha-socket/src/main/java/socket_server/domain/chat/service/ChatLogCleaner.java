package socket_server.domain.chat.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import gotcha_domain.chat.ChatType;
import socket_server.domain.room.repository.RoomRepository;

@Component
@Slf4j
public class ChatLogCleaner {

    private final ChatLogService chatLogService;
    private final RoomRepository roomRepository;

    public ChatLogCleaner(ChatLogService chatLogService, RoomRepository roomRepository) {
        this.chatLogService = chatLogService;
        this.roomRepository = roomRepository;
    }

    @Scheduled(fixedRate = 60000)
    public void cleanExpiredChatLogs() {
        // 1. 모든 채팅 로그 삭제
        chatLogService.removeExpiredMessages(ChatType.ALL, null);

        // 2. SSCAN을 이용해 대기방 채팅 로그 삭제
        try (Cursor<String> roomKeys = roomRepository.scanRoomKeys()) {
            while (roomKeys.hasNext()) {
                String roomKey = roomKeys.next();
                String roomId = roomKey.replaceFirst("room:", "");
                chatLogService.removeExpiredMessages(ChatType.ROOM, roomId);
            }
        } catch (Exception e) {
            log.error("대기방 채팅 로그 삭제 중 오류 발생", e);
        }

        // 3. SSCAN을 이용해 1대1 채팅 로그 삭제
        try (Cursor<String> privateKeys = chatLogService.scanPrivateChatKeys()) {
            while (privateKeys.hasNext()) {
                String key = privateKeys.next();
                chatLogService.removeExpiredMessagesByKey(key);
            }
        } catch (Exception e) {
            log.error("1대1 채팅 로그 삭제 중 오류 발생", e);
        }
    }
}
