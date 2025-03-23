package Gotcha.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignInReq(
        @Schema(description = "이메일", example = "test@naver.com")
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "유효한 이메일 형식이 아닙니다.")
        String email,

        @Schema(description = "비밀번호", example = "password123@")
        @NotBlank(message = "비밀번호를 입력해주세요.")
        String password
) {
}
