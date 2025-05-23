package Gotcha.domain.auth.dto;

import gotcha_domain.user.Role;
import gotcha_domain.user.User;
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
        @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,16}$", message = "비밀번호 확인은 영문, 숫자, 특수문자를 포함하여 8~16자여야 합니다.")
        String passwordCheck,

        @Schema(description = "닉네임", example = "테스터")
        @NotBlank(message = "닉네임은 필수 입력 값입니다.")
        @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,6}$", message = "닉네임은 한글, 영문, 숫자 조합의 2~6자리여야 합니다.")
        String nickname
) {
    public boolean validatePasswordMatch() {
        return password.equals(passwordCheck);
    }

    public User toEntity(String encodePassword, String uuid) {
        return User.builder()
                .email(email)
                .password(encodePassword)
                .nickname(nickname)
                .role(Role.USER)
                .uuid(uuid)
                .build();
    }

    public User toEntityFromGuest(String encodePassword, User guest){
        return User.builder()
                .email(email)
                .password(encodePassword)
                .nickname(nickname)
                .role(Role.USER)
                .uuid(guest.getUuid())
                .build();
    }
}
