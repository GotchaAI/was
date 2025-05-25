package Gotcha.domain.friend.controller;

import Gotcha.domain.friend.dto.FriendReq;
import Gotcha.domain.friend.service.FriendService;
import gotcha_common.dto.SuccessRes;
import gotcha_domain.auth.SecurityUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/friends")
public class FriendController {
    private final FriendService friendService;

    @GetMapping()
    public ResponseEntity<?> getFriends(@AuthenticationPrincipal SecurityUserDetails userDetails) {
        return ResponseEntity.ok(friendService.getFriends(userDetails.getId()));
    }

    @GetMapping("/request")
    public ResponseEntity<?> getFriendRequests(@AuthenticationPrincipal SecurityUserDetails userDetails) {
        return ResponseEntity.ok(friendService.getFriendRequestResToMe(userDetails.getId()));
    }

    @PostMapping("/request")
    public ResponseEntity<?> requestFriend(@AuthenticationPrincipal SecurityUserDetails userDetails,
                                           @Valid @RequestBody FriendReq friendReq) {
        friendService.requestFriend(userDetails.getId(), friendReq);
        return ResponseEntity.ok(SuccessRes.from("친구 신청을 성공적으로 보냈습니다."));
    }

    @PostMapping("/accept/{id}")
    public ResponseEntity<?> acceptFriend(@PathVariable(value = "id") Long friendRequestId,
                                          @AuthenticationPrincipal SecurityUserDetails userDetails) {
        friendService.acceptFriend(userDetails.getId(), friendRequestId);
        return ResponseEntity.ok(SuccessRes.from("친구 신청을 성공적으로 수락하였습니다."));
    }

    @PostMapping("/reject/{id}")
    public ResponseEntity<?> rejectFriend(@PathVariable(value = "id") Long friendRequestId,
                                          @AuthenticationPrincipal SecurityUserDetails userDetails) {
        friendService.rejectFriend(userDetails.getId(), friendRequestId);
        return ResponseEntity.ok(SuccessRes.from("친구 신청을 거절하였습니다."));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<?> deleteFriend(@PathVariable(value = "uuid") String uuid,
                                          @AuthenticationPrincipal SecurityUserDetails userDetails) {
        friendService.deleteFriend(userDetails.getId(), uuid);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
