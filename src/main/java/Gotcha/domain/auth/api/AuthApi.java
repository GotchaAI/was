package Gotcha.domain.auth.api;

import Gotcha.domain.auth.dto.SignInReq;
import Gotcha.domain.auth.dto.SignUpReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;

import static Gotcha.common.jwt.JwtProperties.REFRESH_COOKIE_VALUE;

@Tag(name = "[인증 API]", description = "인증 관련 API")
public interface AuthApi {
    @Operation(summary = "회원가입", description = "회원가입 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                         "accessToken": "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsInJvbGUiOiJVU0VSIiwidXNlcklkIjo0LCJpc3MiOiJnb3RjaGEhIiwiaWF0IjoxNzQyMzg2ODQzLCJleHAiOjE3NDIzODg2NDN9.u2fI9xyTKeKT6ZXPhp5mybVaGTpbJfX_0vtLlwHbKIM"
                                     }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "유효성검사 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "notBlank", value = """
                                        {
                                            "email": "이메일은 필수 입력 값입니다.",
                                            "password": "비밀번호는 필수 입력 값입니다.",
                                            "nickname": "닉네임은 필수 입력 값입니다."
                                        }
                                    """),
                            @ExampleObject(name = "patternError", value = """
                                        {
                                            "email": "유효한 이메일 형식이 아닙니다.",
                                            "password": "비밀번호는 영문, 숫자, 특수문자를 포함하여 8~16자여야 합니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> signUp(@Valid @RequestBody SignUpReq signUpReq);

    @Operation(summary = "로그인", description = "로그인 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                         "accessToken": "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsInJvbGUiOiJVU0VSIiwidXNlcklkIjo0LCJpc3MiOiJnb3RjaGEhIiwiaWF0IjoxNzQyMzg2ODQzLCJleHAiOjE3NDIzODg2NDN9.u2fI9xyTKeKT6ZXPhp5mybVaGTpbJfX_0vtLlwHbKIM"
                                     }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "유효성검사 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "notBlank", value = """
                                        {
                                            "email": "이메일를 입력해주세요.",
                                            "password": "비밀번호를 입력해주세요."
                                        }
                                    """),
                            @ExampleObject(name = "patternError", value = """
                                        {
                                            "email": "유효한 이메일 형식이 아닙니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자 혹은 비밀번호 불일치",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "message": "아이디 또는 비밀번호가 일치하지 않습니다."
                                    }
                                    """)
                    })
            )
    })
    ResponseEntity<?> signIn(@Valid @RequestBody SignInReq signInReq);

    @Operation(summary = "토큰 재발급", description = "토큰 재발급 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                         "accessToken": "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsInJvbGUiOiJVU0VSIiwidXNlcklkIjo0LCJpc3MiOiJnb3RjaGEhIiwiaWF0IjoxNzQyMzg2ODQzLCJleHAiOjE3NDIzODg2NDN9.u2fI9xyTKeKT6ZXPhp5mybVaGTpbJfX_0vtLlwHbKIM"
                                     }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "유효성검사 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "message": "리프레시 토큰이 존재하지 않습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> reIssueToken(@CookieValue(name = REFRESH_COOKIE_VALUE, required = false) String refreshToken);
}
