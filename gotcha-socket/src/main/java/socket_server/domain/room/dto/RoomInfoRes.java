package socket_server.domain.room.dto;

import socket_server.domain.game.enumType.Difficulty;
import socket_server.domain.game.enumType.GameType;
import socket_server.domain.room.model.RoomMetadata;

public record RoomInfoRes(
        String roomId,
        String title,
        String ownerUuid,
        boolean hasPassword,
        GameType gameType,
        Difficulty difficulty,
        int roundCount,
        int maxUser,
        int minUser
) {
    public static RoomInfoRes from(RoomMetadata metadata) {
        return new RoomInfoRes(
                metadata.getId(),
                metadata.getTitle(),
                metadata.getOwnerUuid(),
                metadata.isHasPassword(),
                metadata.getGameType(),
                metadata.getDifficulty(),
                metadata.getRoundCount(),
                metadata.getMax(),
                metadata.getMin()
        );
    }
}
