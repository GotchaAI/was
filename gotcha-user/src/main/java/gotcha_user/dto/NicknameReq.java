package gotcha_user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record NicknameReq(
        @Schema(description = "닉네임", example = "테스터")
        @NotBlank(message = "닉네임은 필수 입력 값입니다.")
        @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,6}$", message = "닉네임은 한글, 영문, 숫자 조합의 2~6자리여야 합니다.")
        String nickname
) {
}
