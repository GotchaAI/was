package Gotcha.domain.room.controller;

import Gotcha.domain.room.api.RoomApi;
import gotcha_domain.auth.SecurityUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import socket_server.domain.room.service.RoomService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/room")
public class RoomController implements RoomApi {
    private final RoomService roomService;

    @GetMapping()
    public ResponseEntity<?> getRoomSummaries() {
        return ResponseEntity.ok(roomService.getAllRoomSummaries());
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<?> getRoomInfo(@PathVariable(value = "roomId") String roomId,
                                         @AuthenticationPrincipal SecurityUserDetails userDetails) {
        return ResponseEntity.ok(roomService.getRoomDetails(roomId, userDetails.getUuid()));
    }
}
