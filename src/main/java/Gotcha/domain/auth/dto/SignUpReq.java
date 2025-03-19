package Gotcha.domain.auth.dto;

import Gotcha.domain.user.entity.Role;
import Gotcha.domain.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignUpReq(

        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        @Email(message = "유효한 이메일 형식이 아닙니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        String password,

        @NotBlank(message = "닉네임은 필수 입력 값입니다.")
        String nickname
) {
    public User toEntity(String encodePassword) {
        return User.builder()
                .email(email)
                .password(encodePassword)
                .nickname(nickname)
                .role(Role.USER)
                .build();
    }
}
