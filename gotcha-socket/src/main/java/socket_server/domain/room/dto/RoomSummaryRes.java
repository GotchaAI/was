package socket_server.domain.room.dto;

import socket_server.domain.game.enumType.Difficulty;
import socket_server.domain.game.enumType.GameType;
import socket_server.domain.room.model.RoomMetadata;

public record RoomSummaryRes(
        String roomId,
        String title,
        String owner,
        GameType gameType,
        Difficulty difficulty,
        boolean hasPassword,
        int maxUser,
        int currentUser
) {
    public static RoomSummaryRes of(RoomMetadata metadata, int currentUser) {
        return new RoomSummaryRes(
                metadata.getId(),
                metadata.getTitle(),
                metadata.getOwner(),
                metadata.getGameType(),
                metadata.getDifficulty(),
                metadata.isHasPassword(),
                metadata.getMax(),
                currentUser
        );
    }
}
