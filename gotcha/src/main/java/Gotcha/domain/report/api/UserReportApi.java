package Gotcha.domain.report.api;

import Gotcha.domain.report.dto.UserReportReq;
import gotcha_domain.auth.SecurityUserDetails;
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

@Tag(name = "[유저 신고 API]", description = "유저 신고 관련 API")
public interface UserReportApi {

    @Operation(summary = "유저 신고", description = "채팅의 유저 신고 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "유저 신고 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": "OK",
                                        "message": "성공적으로 신고하였습니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "자기 자신을 신고할 수 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "code": "REPORT-400-001",
                                        "status": "BAD_REQUEST",
                                        "message": "자기 자신을 신고할 수 없습니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> reportUser(@RequestBody @Valid UserReportReq reportReq,
                                 @AuthenticationPrincipal SecurityUserDetails userDetails);
}
