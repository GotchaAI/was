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

    @Operation(summary = "사용자 제재 적용 API", description = "특정 사용자에게 제재(경고, 임시 정지, 영구 정지)를 부여합니다.\n"
            + "\n"
            + "- targetUserUuid: 제재 대상 사용자의 UUID입니다.\n"
            + "- sanctionType: 제재 유형을 나타내는 Enum입니다.\n"
            + "    • WARNING   : 경고 1회를 부여합니다.\n"
            + "    • TEMP_BAN  : 일정 기간 동안 계정을 정지시킵니다. durationDays가 필요합니다.\n"
            + "    • TEMP_BAN_CANCEL  : 일정 기간 동안 정지된 계정을 다시 활성화 시킵니다. 정지 상태가 아니었다면 에러가 반환됩니다.\n"
            + "    • PERM_BAN  : 계정을 영구 정지(영구 차단)합니다.\n"
            + "\n"
            + "- sourceReportId: 제재의 근거가 되는 신고 ID입니다. 제재가 신고 기반일 경우 사용되며, 없을 수도 있습니다.\n"
            + "- durationDays: TEMP_BAN 제재 시 적용되는 정지 기간(일 단위)입니다. WARNING, PERM_BAN에서는 사용되지 않습니다.\n")
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
