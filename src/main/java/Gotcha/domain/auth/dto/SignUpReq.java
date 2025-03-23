package Gotcha.domain.auth.dto;

import Gotcha.common.exception.CustomException;
import Gotcha.domain.auth.exception.AuthExceptionCode;
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
        @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,16}$", message = "비밀번호는 영문, 숫자, 특수문자를 포함하여 8~16자여야 합니다.")
        String password,

        @Schema(description = "비밀번호 확인", example = "password123@")
        @NotBlank(message = "비밀번호 확인은 필수 입력 값입니다.")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,16}$", message = "비밀번호는 영문, 숫자, 특수문자를 포함하여 8~16자여야 합니다.")
        String passwordCheck,

        @Schema(description = "닉네임", example = "테스터")
        @NotBlank(message = "닉네임은 필수 입력 값입니다.")
        String nickname
) {
    public void validatePasswordMatch() {
        if (!password.equals(passwordCheck)) {
            throw new CustomException(AuthExceptionCode.PASSWORD_NOT_MATCH);
        }
    }

    public User toEntity(String encodePassword) {
        return User.builder()
                .email(email)
                .password(encodePassword)
                .nickname(nickname)
                .role(Role.USER)
                .build();
    }
}
