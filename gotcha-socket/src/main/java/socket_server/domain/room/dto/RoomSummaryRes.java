package socket_server.domain.room.dto;

import socket_server.domain.room.model.RoomMetadata;

public record RoomSummaryRes(
        String title,
        String roomId,
        String owner,
        boolean hasPassword,
        int maxUser,
        int currentUser
) {
    public static RoomSummaryRes of(RoomMetadata metadata, int currentUser) {
        return new RoomSummaryRes(
                metadata.getTitle(),
                metadata.getId(),
                metadata.getOwner(),
                metadata.isHasPassword(),
                metadata.getMax(),
                currentUser
        );
    }
}
