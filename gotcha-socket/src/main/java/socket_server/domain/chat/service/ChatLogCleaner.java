package socket_server.domain.chat.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import gotcha_domain.chat.ChatType;
import socket_server.domain.room.repository.RoomRepository;

import java.util.Set;

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
        chatLogService.removeExpiredMessages(ChatType.ALL, null);

        Set<String> roomIds = roomRepository.getAllRoomIds();
        for (String roomId : roomIds) {
            chatLogService.removeExpiredMessages(ChatType.ROOM, roomId);
        }

        Set<String> privateKeys = chatLogService.getPrivateChatKeys();
        for (String key : privateKeys) {
            chatLogService.removeExpiredMessagesByKey(key);
        }
    }
}
