package Gotcha.domain.user.controller;

import Gotcha.domain.user.api.UserApi;
import Gotcha.domain.user.dto.NicknameReq;
import Gotcha.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController implements UserApi {
    private final UserService userService;

    @Override
    @PostMapping("/nickname-check")
    public ResponseEntity<?> checkNickname(@Valid @RequestBody NicknameReq nicknameReq) {
        userService.checkNickname(nicknameReq.nickname());

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
