package socket_server.domain.room.dto;

import socket_server.domain.room.model.RoomMetadata;

public record RoomSummaryRes(
        String roomId,
        String title,
        String owner,
        boolean hasPassword,
        int maxUser,
        int currentUser
) {
    public static RoomSummaryRes of(RoomMetadata metadata, int currentUser) {
        return new RoomSummaryRes(
                metadata.getId(),
                metadata.getTitle(),
                metadata.getOwner(),
                metadata.isHasPassword(),
                metadata.getMax(),
                currentUser
        );
    }
}
