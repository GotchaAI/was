package Gotcha.domain.sanction.api;

import Gotcha.domain.sanction.dto.SanctionReq;
import Gotcha.domain.sanction.dto.SanctionRes;
import gotcha_domain.auth.SecurityUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;



@Tag(name = "[관리자 제재 API]", description = "관리자용 사용자 제재 관련 API")
public interface SanctionApi {

    @Operation(summary = "사용자 제재 적용 API", description = "특정 사용자에게 경고, 임시 정지, 영구 정지 등의 제재를 가합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "제재 조치 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "id": 1,
                                        "targetUserName": "제재받은유저",
                                        "adminUserName": "관리자",
                                        "sanctionType": "TEMP_BAN",
                                        "reason": "부적절한 언어 사용",
                                        "expiresAt": "2025-11-18T10:00:00",
                                        "createdAt": "2025-11-11T10:00:00"
                                    }
                                    """)
                    })
            ),
            @ApiResponse(responseCode = "422", description = "필드 검증 오류 ",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "reason": "제재 사유는 필수입니다."
                                    }
                                    """)
                    })
            ),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "code": "AUTH-403-001",
                                        "status": "FORBIDDEN",
                                        "message": "접근 권한이 없습니다."
                                    }
                                    """)
                    })
            ),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "code": "USER-404-001",
                                        "status": "NOT_FOUND",
                                        "message": "해당 사용자를 찾을 수 없습니다."
                                    }
                                    """)
                    })
            ),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 신고 건",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "code": "REPORT-404-001",
                                        "status": "NOT_FOUND",
                                        "message": "신고 내역을 찾을 수 없습니다."
                                    }
                                    """)
                    })
            )
    })
    ResponseEntity<SanctionRes> applySanction(
            @Valid @RequestBody SanctionReq sanctionReq,
            SecurityUserDetails userDetails);
}
