package Gotcha.domain.room.api;

import socket_server.domain.room.dto.RoomListReq;
import gotcha_domain.auth.SecurityUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "[대기방 API]", description = "대기방 관련 API")
public interface RoomApi {
    @Operation(summary = "대기방 초기 목록 조회", description = "로비 입장 시 초기 대기방 목록 조회 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "대기방 목록 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    [
                                         {
                                             "roomId": "7390",
                                             "title": "고양이를 속여보자",
                                             "owner": "관리자",
                                             "gameType": "TRICK_MYOMYO",
                                             "difficulty": "BASIC",
                                             "hasPassword": false,
                                             "maxUser": 2,
                                             "currentUser": 1
                                         },
                                         {
                                             "roomId": "7705",
                                             "title": "고양이의 비밀방",
                                             "owner": "테스터",
                                             "gameType": "TRICK_MYOMYO",
                                             "difficulty": "ADVANCED",
                                             "hasPassword": false,
                                             "maxUser": 2,
                                             "currentUser": 1
                                         }
                                    ]
                                    """)
                    }))
    })
    ResponseEntity<?> getRoomSummaries(@RequestBody RoomListReq roomListReq);

    @Operation(summary = "대기방 상세 정보 조회", description = "대기방 입장 시 초기 상세 정보 조회 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "대기방 상세 정보 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "roomInfo": {
                                            "roomId": "5876",
                                            "title": "고양이의 비밀방",
                                            "ownerUuid": "3",
                                            "hasPassword": true,
                                            "gameType": "TRICK_MYOMYO",
                                            "difficulty": "ADVANCED",
                                            "roundCount": 3,
                                            "maxUser": 2,
                                            "minUser": 2
                                        },
                                        "userInfos": [
                                            {
                                                "userUuid": "3",
                                                "nickname": "관리자",
                                                "ready": true
                                            },
                                            {
                                                "userUuid": "2",
                                                "nickname": "테스터",
                                                "ready": false
                                            }
                                        ]
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "유효하지 않은 대기방", value = """
                                    {
                                        "code": "ROOM_400_004",
                                        "status": "BAD_REQUEST",
                                        "message": "유효하지 않은 고유 방 코드 입니다."
                                    }
                                    """),
                            @ExampleObject(name = "대기방에 참여하지 않음", value = """
                                    {
                                        "code": "ROOM_400_003",
                                        "status": "BAD_REQUEST",
                                        "message": "방에 존재하지 않는 유저입니다."
                                    }
                                    """)
                    })),
    })
    ResponseEntity<?> getRoomInfo(@PathVariable(value = "roomId") String roomId,
                                  @AuthenticationPrincipal SecurityUserDetails userDetails);
}
