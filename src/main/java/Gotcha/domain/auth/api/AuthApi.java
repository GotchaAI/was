package Gotcha.domain.auth.api;

import Gotcha.domain.auth.dto.EmailCodeVerifyReq;
import Gotcha.domain.auth.dto.EmailReq;
import Gotcha.domain.auth.dto.SignInReq;
import Gotcha.domain.auth.dto.SignUpReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import static Gotcha.common.jwt.token.JwtProperties.ACCESS_HEADER_VALUE;
import static Gotcha.common.jwt.token.JwtProperties.REFRESH_COOKIE_VALUE;

@Tag(name = "[인증 API]", description = "인증 관련 API")
public interface AuthApi {
    @Operation(summary = "회원가입", description = "회원가입 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                         "accessToken": "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsInJvbGUiOiJVU0VSIiwidXNlcklkIjo0LCJpc3MiOiJnb3RjaGEhIiwiaWF0IjoxNzQyMzg2ODQzLCJleHAiOjE3NDIzODg2NDN9.u2fI9xyTKeKT6ZXPhp5mybVaGTpbJfX_0vtLlwHbKIM"
                                     }
                                    """)
                    })),
            @ApiResponse(responseCode = "422", description = "유효성검사 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "notBlank", value = """
                                        {
                                            "email": "이메일은 필수 입력 값입니다.",
                                            "password": "비밀번호는 필수 입력 값입니다.",
                                            "passwordCheck": "비밀번호 확인은 필수 입력 값입니다.",
                                            "nickname": "닉네임은 필수 입력 값입니다."
                                        }
                                    """),
                            @ExampleObject(name = "patternError", value = """
                                        {
                                             "password": "비밀번호는 영문, 숫자, 특수문자를 포함하여 8~16자여야 합니다.",
                                             "passwordCheck": "비밀번호 확인은 영문, 숫자, 특수문자를 포함하여 8~16자여야 합니다.",
                                             "nickname": "닉네임은 한글, 영문, 숫자 조합의 2~6자리여야 합니다.",
                                             "email": "유효한 이메일 형식이 아닙니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "필드 검증 오류",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                         "status": "BAD_REQUEST",
                                         "message": "필드 검증 오류입니다.",
                                         "fields": {
                                             "password": "비밀번호가 일치하지 않습니다.",
                                             "nickname": "닉네임 중복 확인이 완료되지 않았습니다.",
                                             "email": "이메일 인증이 완료되지 않았습니다."
                                         }
                                    }
                                    """)
                    })),
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
            @ApiResponse(responseCode = "422", description = "유효성검사 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "notBlank", value = """
                                        {
                                             "password": "비밀번호를 입력해주세요.",
                                             "email": "이메일을 입력해주세요."
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
                                         "status": "NOT_FOUND",
                                         "message": "아이디 또는 비밀번호가 유효하지 않습니다."
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
                                             "status": "NOT_FOUND",
                                             "message": "Refresh Token을 찾을 수 없습니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "401", description = "refreshToken 만료",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                             "status": "UNAUTHORIZED",
                                             "message": "Refresh Token이 만료되었습니다."
                                        }
                                    """)
                    })
            )
    })
    ResponseEntity<?> reIssueToken(@CookieValue(name = REFRESH_COOKIE_VALUE, required = false) String refreshToken);

    @Operation(summary = "이메일 인증번호 발송", description = "이메일 인증번호 발송 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이메일 인증번호 발송 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": "OK",
                                        "message": "인증 코드가 발송되었습니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "422", description = "유효성검사 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                             "email": "이메일은 필수 입력 값입니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "이메일 중복 확인 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                            "status": "CONFLICT",
                                            "message": "이미 가입된 이메일입니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "429", description = "이메일 인증번호는 1분 이후 다시 전송할 수 있음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                        {
                                             "status": "TOO_MANY_REQUESTS",
                                             "message": "이미 메일을 요청하셨습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> sendEmail(@Valid @RequestBody EmailReq emailReq);

    @Operation(summary = "이메일 인증", description = "이메일 인증 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이메일 인증 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": "OK",
                                        "message": "이메일이 인증되었습니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "422", description = "유효성검사 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "nonBlank", value = """
                                        {
                                             "email": "이메일은 필수 입력 값입니다.",
                                             "code": "인증번호는 필수 입력 값입니다."
                                        }
                                    """),
                            @ExampleObject(name = "patternError", value = """
                                        {
                                             "code": "인증번호는 숫자 6자리여야 합니다."
                                        }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "이메일 인증 실패",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "codeIncorrect", value = """
                                        {
                                             "status": "BAD_REQUEST",
                                             "message": "인증번호가 일치하지 않습니다."
                                        }
                                    """),
                            @ExampleObject(name = "codeExpired", value = """
                                        {
                                             "status": "BAD_REQUEST",
                                             "message": "인증번호가 만료되었습니다."
                                        }
                                    """)
                    }))
    })
    ResponseEntity<?> verifyEmail(@Valid @RequestBody EmailCodeVerifyReq emailCodeVerifyReq);

    @Operation(summary = "로그아웃", description = "로그아웃 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": "OK",
                                        "message": "로그아웃 되었습니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> signOut(@RequestHeader(value = ACCESS_HEADER_VALUE, required = false) String accessToken,
                              @CookieValue(name = REFRESH_COOKIE_VALUE, required = false) String refreshToken,
                              HttpServletResponse response);
}
