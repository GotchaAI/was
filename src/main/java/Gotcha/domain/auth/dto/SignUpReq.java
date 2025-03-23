package Gotcha.domain.auth.dto;

import Gotcha.domain.user.entity.Role;
import Gotcha.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SignUpReq(
        @Schema(description = "이메일", example = "test@naver.com")
        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        @Email(message = "유효한 이메일 형식이 아닙니다.")
        String email,

        @Schema(description = "비밀번호", example = "password123@")
        @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        String password,

        @Schema(description = "닉네임", example = "테스터")
        @NotBlank(message = "닉네임은 필수 입력 값입니다.")
        @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,6}$", message = "닉네임은 한글, 영문, 숫자 조합의 2~6자리여야 합니다.")
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
