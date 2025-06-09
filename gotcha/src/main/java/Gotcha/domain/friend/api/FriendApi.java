package Gotcha.domain.friend.api;

import Gotcha.domain.friend.dto.FriendReq;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "[친구 API]", description = "친구 관련 API")
public interface FriendApi {

    @Operation(summary = "친구 목록 조회", description = "친구 목록 조회 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "친구 목록 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                                        
                                    """)
                    }))
    })
    ResponseEntity<?> getFriends(@AuthenticationPrincipal SecurityUserDetails userDetails);

    @Operation(summary = "사용자 검색", description = "친구가 아닌 사용자 검색 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 검색 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    [
                                        {
                                            "nickname": "테스터",
                                            "uuid": "2",
                                            "lastLogout": "2025-06-09T13:11:24"
                                        },
                                        {
                                            "nickname": "테스터다",
                                            "uuid": "1",
                                            "lastLogout": null
                                        }
                                    ]
                                    """)
                    }))
    })
    ResponseEntity<?> searchUser(@AuthenticationPrincipal SecurityUserDetails userDetails,
                                   @RequestParam(value = "keyword") String keyword);

    @Operation(summary = "친구 신청 목록 조회", description = "친구 신청 목록 조회 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "친구 신청 목록 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    [
                                        {
                                            "id": 7,
                                            "nickname": "테스트",
                                            "uuid": "2"
                                        }
                                    ]
                                    """)
                    }))
    })
    ResponseEntity<?> getFriendRequests(@AuthenticationPrincipal SecurityUserDetails userDetails);

    @Operation(summary = "친구 신청 요청", description = "친구 신청 요청 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "친구 신청 요청 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": "OK",
                                        "message": "친구 신청을 성공적으로 보냈습니다."
                                    }                            
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "자기 자신에게 친구 요청 보낼 수 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "code": "FRIEND-400-001",
                                        "status": "BAD_REQUEST",
                                        "message": "자기 자신에게는 친구 요청을 보낼 수 없습니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "code": "USER-404-001",
                                        "status": "NOT_FOUND",
                                        "message": "존재하지 않는 사용자입니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "409", description = "이미 친구 관계인 사용자",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "code": "FRIEND-409-002",
                                        "status": "CONFLICT",
                                        "message": "이미 친구인 사용자입니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> requestFriend(@AuthenticationPrincipal SecurityUserDetails userDetails,
                                    @Valid @RequestBody FriendReq friendReq);


    @Operation(summary = "친구 요청 수락", description = "친구 요청 수락 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "친구 요청 수락 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": "OK",
                                        "message": "친구 신청을 성공적으로 수락하였습니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 친구 요청",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "code": "FIREND-404-001",
                                        "status": "NOT_FOUND",
                                        "message": "친구 요청을 찾을 수 없습니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "409", description = "이미 친구 관계인 사용자",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "code": "FRIEND-409-002",
                                        "status": "CONFLICT",
                                        "message": "이미 친구인 사용자입니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "403", description = "이미 친구 관계인 사용자",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "code": "FRIEND-403-001",
                                        "status": "FORBIDDEN",
                                        "message": "해당 친구 요청에 접근 권한이 없습니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> acceptFriend(@PathVariable(value = "id") Long friendRequestId,
                                   @AuthenticationPrincipal SecurityUserDetails userDetails);

    @Operation(summary = "친구 요청 거절", description = "친구 요청 거절 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "친구 요청 거절 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": "OK",
                                        "message": "친구 신청을 거절하였습니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 친구 요청",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "code": "FIREND-404-001",
                                        "status": "NOT_FOUND",
                                        "message": "친구 요청을 찾을 수 없습니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "403", description = "이미 친구 관계인 사용자",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "code": "FRIEND-403-001",
                                        "status": "FORBIDDEN",
                                        "message": "해당 친구 요청에 접근 권한이 없습니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> rejectFriend(@PathVariable(value = "id") Long friendRequestId,
                                   @AuthenticationPrincipal SecurityUserDetails userDetails);

    @Operation(summary = "친구 삭제", description = "친구 삭제 API")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "친구 삭제 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                                                
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "친구가 아닌 사용자",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "code": "FRIEND-400-002",
                                        "status": "BAD_REQUEST,
                                        "message": "해당 사용자와는 친구가 아닙니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> deleteFriend(@PathVariable(value = "uuid") String uuid,
                                   @AuthenticationPrincipal SecurityUserDetails userDetails);
}
