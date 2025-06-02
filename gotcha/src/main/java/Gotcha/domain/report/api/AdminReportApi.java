package Gotcha.domain.report.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "[어드민 신고 API]", description = "어드민 신고 관리 관련 API")
public interface AdminReportApi {
    @Operation(summary = "유저 신고 목록 조회", description = "유저 신고 목록 조회 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "유저 신고 목록 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "content": [
                                            {
                                                "userReportId": 1,
                                                "reportedAt": "2025-06-01T16:55:41.962938",
                                                "nickname": "테스터",
                                                "reportType": "OTHER",
                                                "detail": "그냥",
                                                "chatLog": [
                                                    {
                                                        "nickname": "관리자",
                                                        "content": "a",
                                                        "chatType": "ALL",
                                                        "sentAt": "2025-06-01T16:22:59.079033"
                                                    },
                                                    {
                                                        "nickname": "관리자",
                                                        "content": "a",
                                                        "chatType": "ALL",
                                                        "sentAt": "2025-06-01T16:22:59.20903"
                                                    },
                                                    {
                                                        "nickname": "관리자",
                                                        "content": "a",
                                                        "chatType": "ALL",
                                                        "sentAt": "2025-06-01T16:22:59.344945"
                                                    },
                                                    {
                                                        "nickname": "관리자",
                                                        "content": "a",
                                                        "chatType": "ALL",
                                                        "sentAt": "2025-06-01T16:22:59.474599"
                                                    },
                                                    {
                                                        "nickname": "관리자",
                                                        "content": "a",
                                                        "chatType": "ALL",
                                                        "sentAt": "2025-06-01T16:22:59.612039"
                                                    },
                                                    {
                                                        "nickname": "관리자",
                                                        "content": "a",
                                                        "chatType": "ALL",
                                                        "sentAt": "2025-06-01T16:22:59.762052"
                                                    },
                                                    {
                                                        "nickname": "관리자",
                                                        "content": "a",
                                                        "chatType": "ALL",
                                                        "sentAt": "2025-06-01T16:22:59.860273"
                                                    },
                                                    {
                                                        "nickname": "테스터",
                                                        "content": "who ar eyou",
                                                        "chatType": "ALL",
                                                        "sentAt": "2025-06-01T16:23:27.997828"
                                                    },
                                                    {
                                                        "nickname": "테스터",
                                                        "content": "who ar eyou",
                                                        "chatType": "ALL",
                                                        "sentAt": "2025-06-01T16:23:28.376081"
                                                    },
                                                    {
                                                        "nickname": "테스터",
                                                        "content": "who ar eyou",
                                                        "chatType": "ALL",
                                                        "sentAt": "2025-06-01T16:23:28.740553"
                                                    },
                                                    {
                                                        "nickname": "테스터",
                                                        "content": "who ar eyou",
                                                        "chatType": "ALL",
                                                        "sentAt": "2025-06-01T16:23:29.108754"
                                                    },
                                                    {
                                                        "nickname": "테스터",
                                                        "content": "who ar eyou",
                                                        "chatType": "ALL",
                                                        "sentAt": "2025-06-01T16:23:29.578639"
                                                    }
                                                ]
                                            }
                                        ],
                                        "page": {
                                            "size": 10,
                                            "number": 0,
                                            "totalElements": 1,
                                            "totalPages": 1
                                        }
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "code": "AUTH-403-001",
                                        "status": "FORBIDDEN",
                                        "message": "접근 권한이 없습니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> getUserReports(@RequestParam(value = "keyword", required = false) String keyword,
                                     @RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page);
}
