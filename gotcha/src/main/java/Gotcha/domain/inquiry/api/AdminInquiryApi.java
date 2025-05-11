package Gotcha.domain.inquiry.api;

import Gotcha.domain.inquiry.dto.AnswerReq;
import gotcha_domain.auth.SecurityUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "[관리자 QnA API]", description = "관리자 QnA 처리 관련 API")
public interface AdminInquiryApi {

    @Operation(summary = "QnA 답변 생성", description = "QnA 답변 생성 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "QnA 답변 생성 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                    {
                        "status": "OK",
                        "message": "QnA 답변 생성에 성공했습니다."
                    }
                    """)
                    })
            ),
            @ApiResponse(responseCode = "400", description = "필드 검증 오류",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                    {
                        "status": "BAD_REQUEST",
                        "message": "필드 검증 오류입니다.",
                        "fields": {
                            "content": "내용은 필수 입력 사항입니다."
                        }
                    }
                    """)
                    })
            ),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                    {
                        "status": "FORBIDDEN",
                        "message": "권한이 없습니다."
                    }
                    """)
                    })
            )
    })
    ResponseEntity<?> createAnswer(@Valid @RequestBody AnswerReq answerReq, @PathVariable(value = "inquiryId")Long inquiryId,
                                   @AuthenticationPrincipal SecurityUserDetails userDetails);



}
