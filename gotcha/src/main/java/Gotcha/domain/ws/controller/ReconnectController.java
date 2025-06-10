package Gotcha.domain.ws.controller;

import Gotcha.domain.ws.api.ReconnectApi;
import gotcha_domain.auth.SecurityUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import socket_server.common.util.DisconnectManager;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/ws")
@RequiredArgsConstructor
public class ReconnectController implements ReconnectApi {
    private final DisconnectManager disconnectManager;

    @GetMapping("/reconnect")
    public ResponseEntity<?> reconnect(@AuthenticationPrincipal SecurityUserDetails userDetails) {
        String roomId = disconnectManager.cancelDisconnectIfExists(userDetails.getUuid());
        return ResponseEntity.ok(Map.of("roomId", roomId));
    }
}
