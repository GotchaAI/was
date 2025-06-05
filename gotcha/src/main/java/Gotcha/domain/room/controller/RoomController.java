package Gotcha.domain.room.controller;

import Gotcha.domain.room.api.RoomApi;
import gotcha_domain.auth.SecurityUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import socket_server.domain.room.dto.RoomListReq;
import socket_server.domain.room.service.RoomService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/room")
public class RoomController implements RoomApi {
    private final RoomService roomService;

    @GetMapping()
    public ResponseEntity<?> getRoomSummaries(@ParameterObject @Valid RoomListReq roomListReq) {
        return ResponseEntity.ok(roomService.getAllRoomSummaries(roomListReq));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<?> getRoomInfo(@PathVariable(value = "roomId") String roomId,
                                         @AuthenticationPrincipal SecurityUserDetails userDetails) {
        return ResponseEntity.ok(roomService.getRoomDetails(roomId, userDetails.getUuid()));
    }
}
