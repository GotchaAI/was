package Gotcha.domain.user.api;

import Gotcha.common.jwt.auth.SecurityUserDetails;
import Gotcha.domain.user.dto.NicknameReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "[사용자 API]", description = "사용자 관련 API")
public interface UserApi {

    @Operation(summary = "닉네임 중복 검사", description = "닉네임 중복 검사 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "닉네임 중복 검사 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": "OK",
                                        "message": "사용 가능한 닉네임입니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "409", description = "닉네임 중복",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                         "status": "CONFLICT",
                                         "message": "이미 존재하는 닉네임입니다."
                                     }
                                    """)
                    })),
            @ApiResponse(responseCode = "422", description = "닉네임 형식 오류",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "notBlank", value = """
                                    {
                                        "nickname": "닉네임은 필수 입력 값입니다."
                                    }
                                    """),
                            @ExampleObject(name = "patternError", value = """
                                    {
                                        "nickname": "닉네임은 한글, 영문, 숫자 조합의 2~6자리여야 합니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> checkNickname(@Valid @RequestBody NicknameReq nicknameReq);

    @Operation(summary = "사용자 정보 조회", description = "사용자 정보 조회 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공",
            content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(name = "게스트 조회", value = """
                            {
                                "nickname": "웃긴너구리",
                                "email": null,
                                "role": "GUEST"
                            }
                            """),
                    @ExampleObject(name = "사용자 조회", value = """
                            {
                                "nickname": "테스트",
                                "email": "test@gmail.com",
                                "role": "USER"
                            }
                            """),
                    @ExampleObject(name = "관리자 조회", value = """
                            {
                                "nickname": "관리자",
                                "email": "admin@gmail.com",
                                "role": "ADMIN"
                            }
                            """)
            })),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                         "status": "NOT_FOUND",
                                         "message": "존재하지 않는 사용자입니다."
                                     }
                                    """)
                    })),
    })
    ResponseEntity<?> getUserInfo(@AuthenticationPrincipal SecurityUserDetails userDetails);
}
