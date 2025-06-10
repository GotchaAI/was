package Gotcha.domain.ws.api;

import gotcha_domain.auth.SecurityUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Tag(name = "[소켓 재접속 API]", description = "소켓 재접속 관련 API")
public interface ReconnectApi {

    @Operation(summary = "재접속 처리", description = "소켓 연결 해제 후 재접속 시 기존 대기방 반환 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재접속 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "roomId": "7705"
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "403", description = "재접속 가능한 방이 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "code": "ROOM_400_012",
                                        "status": "BAD_REQUEST",
                                        "message": "재접속 가능한 방 정보가 없습니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> reconnect(@AuthenticationPrincipal SecurityUserDetails userDetails);
}
