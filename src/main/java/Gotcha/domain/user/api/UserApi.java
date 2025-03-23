package Gotcha.domain.user.api;

import Gotcha.domain.user.dto.NicknameReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "[사용자 API]", description = "사용자 관련 API")
public interface UserApi {

    @Operation(summary = "닉네임 중복 검사", description = "닉네임 중복 검사 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "닉네임 중복 검사 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject()
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
}
