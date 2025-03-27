package Gotcha.domain.notification.api;

import Gotcha.common.jwt.SecurityUserDetails;
import Gotcha.domain.notification.dto.NotificationReq;
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

@Tag(name = "[관리자 공지사항 API]", description = "관리자용 공지사항 관련 API")
interface AdminNotificationApi {

    @Operation(summary = "공지사항 생성", description = "공지사항 생성 API")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "공지사항 생성 성공",
            content = @Content(mediaType = "application/json", examples = {
                @ExampleObject(value = """
                    {
                        "status": "OK",
                        "message": "공지사항 생성에 성공했습니다."
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
                            "title": "제목은 필수 입력 사항입니다.",
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
    ResponseEntity<?> createNotification(
            @Valid @RequestBody NotificationReq notificationReq,
            @AuthenticationPrincipal SecurityUserDetails userDetails);

    @Operation(summary = "공지사항 수정", description = "공지사항 수정 API")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "공지사항 수정 성공",
            content = @Content(mediaType = "application/json", examples = {
                @ExampleObject(value = """
                    {
                        "status": "OK",
                        "message": "공지사항 수정에 성공했습니다."
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
                            "title": "제목은 필수 입력 사항입니다.",
                            "content": "내용은 필수 입력 사항입니다."
                        }
                    }
                    """)
            })
        ),
        @ApiResponse(responseCode = "403", description = "작성자 불일치 또는 수정 권한 없음",
            content = @Content(mediaType = "application/json", examples = {
                @ExampleObject(value = """
                    {
                        "status": "FORBIDDEN",
                        "message": "권한이 없습니다."
                    }
                    """)
            })
        ),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 공지사항",
            content = @Content(mediaType = "application/json", examples = {
                @ExampleObject(value = """                
                    {
                        "status": "NOT_FOUND",
                        "message": "존재하지 않는 공지사항입니다."
                    }
                    """)
            })
        )
    })
    ResponseEntity<?> updateNotification(
            @PathVariable(value = "notificationId") Long notificationId,
            @Valid @RequestBody NotificationReq notificationReq,
            @AuthenticationPrincipal SecurityUserDetails userDetails);

    @Operation(summary = "공지사항 삭제", description = "공지사항 삭제 API")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "공지사항 삭제 성공"),
        @ApiResponse(responseCode = "403", description = "작성자 불일치 또는 삭제 권한 없음",
            content = @Content(mediaType = "application/json", examples = {
                @ExampleObject(value = """
                    {
                        "status": "FORBIDDEN",
                        "message": "권한이 없습니다."
                    }
                    """)
            })
        ),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 공지사항",
            content = @Content(mediaType = "application/json", examples = {
                @ExampleObject(value = """                
                    {
                        "status": "NOT_FOUND",
                        "message": "존재하지 않는 공지사항입니다."
                    }
                    """)
            })
        )
    })
    ResponseEntity<?> deleteNotification(
            @PathVariable(value = "notificationId") Long notificationId,
            @AuthenticationPrincipal SecurityUserDetails userDetails);


}
