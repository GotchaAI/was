package Gotcha.domain.mypage.api;

import Gotcha.domain.mypage.dto.ChatSettingReq;
import gotcha_domain.auth.SecurityUserDetails;
import gotcha_user.dto.NicknameReq;
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

@Tag(name = "[마이페이지 API]", description = "마이페이지 관련 API")
public interface MypageApi {
    @Operation(summary = "게임 전적 목록 조회", description = "게임 전적 목록 조회 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게임 전적 목록 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    [
                                        {
                                            "gameId": 1,
                                            "gameType": "TRICK_MYOMYO",
                                            "difficulty": "BASIC",
                                            "playedAt": "2025-06-09T15:33:30.048225",
                                            "score": 170
                                        }
                                    ]
                                    """)
                    }))
    })
    ResponseEntity<?> getUserGameSummaries(@AuthenticationPrincipal SecurityUserDetails userDetails);

    @Operation(summary = "게임 전적 조회", description = "게임 전적 조회 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게임 전적 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                         "gameId": 1,
                                         "gameType": "TRICK_MYOMYO",
                                         "difficulty": "BASIC",
                                         "playedAt": "2025-06-09T15:33:30.048225",
                                         "playerWon": true,
                                         "aiScore": 0,
                                         "playerScore": 170,
                                         "rounds": [
                                             {
                                                 "roundIndex": 0,
                                                 "words": [
                                                     {
                                                         "wordIndex": 0,
                                                         "word": "악어",
                                                         "drawerUuid": "HEjCRCjE",
                                                         "submitted": true,
                                                         "imageUrl": "https://gotchaai-image-bucket.s3.ap-northeast-2.amazonaws.com/z2E63KqK/9228e506-059c-4a0f-9874-65d68aff372b.png",
                                                         "aiGuesses": [
                                                             {
                                                                 "guesserUuid": "AI",
                                                                 "guessWord": "컵",
                                                                 "attempts": 1,
                                                                 "correct": false
                                                             }
                                                         ],
                                                         "playerGuesses": [
                                                             {
                                                                 "guesserUuid": "gLF0rt9Y",
                                                                 "guessWord": "악어",
                                                                 "attempts": 1,
                                                                 "correct": true
                                                             }
                                                         ],
                                                         "aiPredictions": [
                                                             {
                                                                 "predicted": "컵",
                                                                 "confidence": 98.141610622406
                                                             },
                                                             {
                                                                 "predicted": "침대",
                                                                 "confidence": 0.504483375698328
                                                             },
                                                             {
                                                                 "predicted": "노트북",
                                                                 "confidence": 0.47213733196258545
                                                             }
                                                         ]
                                                     },
                                                     {
                                                         "wordIndex": 1,
                                                         "word": "사자",
                                                         "drawerUuid": "gLF0rt9Y",
                                                         "submitted": true,
                                                         "imageUrl": "https://gotchaai-image-bucket.s3.ap-northeast-2.amazonaws.com/z2E63KqK/9228e506-059c-4a0f-9874-65d68aff372b.png",
                                                         "aiGuesses": [
                                                             {
                                                                 "guesserUuid": "AI",
                                                                 "guessWord": "노트북",
                                                                 "attempts": 1,
                                                                 "correct": false
                                                             },
                                                             {
                                                                 "guesserUuid": "AI",
                                                                 "guessWord": "지도",
                                                                 "attempts": 2,
                                                                 "correct": false
                                                             }
                                                         ],
                                                         "playerGuesses": [
                                                             {
                                                                 "guesserUuid": "HEjCRCjE",
                                                                 "guessWord": "악어",
                                                                 "attempts": 1,
                                                                 "correct": false
                                                             },
                                                             {
                                                                 "guesserUuid": "HEjCRCjE",
                                                                 "guessWord": "사자",
                                                                 "attempts": 2,
                                                                 "correct": true
                                                             }
                                                         ],
                                                         "aiPredictions": [
                                                             {
                                                                 "predicted": "노트북",
                                                                 "confidence": 98.7697184085846
                                                             },
                                                             {
                                                                 "predicted": "지도",
                                                                 "confidence": 0.8258135989308357
                                                             },
                                                             {
                                                                 "predicted": "달력",
                                                                 "confidence": 0.21645468659698963
                                                             }
                                                         ]
                                                     }
                                                 ]
                                             },
                                             {
                                                 "roundIndex": 1,
                                                 "words": [
                                                     {
                                                         "wordIndex": 0,
                                                         "word": "수풀",
                                                         "drawerUuid": "HEjCRCjE",
                                                         "submitted": true,
                                                         "imageUrl": "https://gotchaai-image-bucket.s3.ap-northeast-2.amazonaws.com/z2E63KqK/9228e506-059c-4a0f-9874-65d68aff372b.png",
                                                         "aiGuesses": [
                                                             {
                                                                 "guesserUuid": "AI",
                                                                 "guessWord": "컵",
                                                                 "attempts": 1,
                                                                 "correct": false
                                                             }
                                                         ],
                                                         "playerGuesses": [
                                                             {
                                                                 "guesserUuid": "gLF0rt9Y",
                                                                 "guessWord": "수풀",
                                                                 "attempts": 1,
                                                                 "correct": true
                                                             }
                                                         ],
                                                         "aiPredictions": [
                                                             {
                                                                 "predicted": "컵",
                                                                 "confidence": 94.669908285141
                                                             },
                                                             {
                                                                 "predicted": "노트북",
                                                                 "confidence": 1.6384916380047798
                                                             },
                                                             {
                                                                 "predicted": "지도",
                                                                 "confidence": 1.1955133639276028
                                                             }
                                                         ]
                                                     },
                                                     {
                                                         "wordIndex": 1,
                                                         "word": "고양이",
                                                         "drawerUuid": "gLF0rt9Y",
                                                         "submitted": true,
                                                         "imageUrl": "https://gotchaai-image-bucket.s3.ap-northeast-2.amazonaws.com/z2E63KqK/9228e506-059c-4a0f-9874-65d68aff372b.png",
                                                         "aiGuesses": [
                                                             {
                                                                 "guesserUuid": "AI",
                                                                 "guessWord": "지도",
                                                                 "attempts": 1,
                                                                 "correct": false
                                                             }
                                                         ],
                                                         "playerGuesses": [
                                                             {
                                                                 "guesserUuid": "HEjCRCjE",
                                                                 "guessWord": "고양이",
                                                                 "attempts": 1,
                                                                 "correct": true
                                                             }
                                                         ],
                                                         "aiPredictions": [
                                                             {
                                                                 "predicted": "지도",
                                                                 "confidence": 46.41824662685394
                                                             },
                                                             {
                                                                 "predicted": "노트북",
                                                                 "confidence": 18.52540224790573
                                                             },
                                                             {
                                                                 "predicted": "얼굴",
                                                                 "confidence": 12.265511602163317
                                                             }
                                                         ]
                                                     }
                                                 ]
                                             },
                                             {
                                                 "roundIndex": 2,
                                                 "words": [
                                                     {
                                                         "wordIndex": 0,
                                                         "word": "산",
                                                         "drawerUuid": "HEjCRCjE",
                                                         "submitted": true,
                                                         "imageUrl": "https://gotchaai-image-bucket.s3.ap-northeast-2.amazonaws.com/z2E63KqK/9228e506-059c-4a0f-9874-65d68aff372b.png",
                                                         "aiGuesses": [
                                                             {
                                                                 "guesserUuid": "AI",
                                                                 "guessWord": "노트북",
                                                                 "attempts": 1,
                                                                 "correct": false
                                                             }
                                                         ],
                                                         "playerGuesses": [
                                                             {
                                                                 "guesserUuid": "gLF0rt9Y",
                                                                 "guessWord": "산",
                                                                 "attempts": 1,
                                                                 "correct": true
                                                             }
                                                         ],
                                                         "aiPredictions": [
                                                             {
                                                                 "predicted": "노트북",
                                                                 "confidence": 44.12991106510162
                                                             },
                                                             {
                                                                 "predicted": "컵",
                                                                 "confidence": 39.702245593070984
                                                             },
                                                             {
                                                                 "predicted": "지도",
                                                                 "confidence": 6.604337692260742
                                                             }
                                                         ]
                                                     },
                                                     {
                                                         "wordIndex": 1,
                                                         "word": "천사",
                                                         "drawerUuid": "gLF0rt9Y",
                                                         "submitted": true,
                                                         "imageUrl": "https://gotchaai-image-bucket.s3.ap-northeast-2.amazonaws.com/z2E63KqK/9228e506-059c-4a0f-9874-65d68aff372b.png",
                                                         "aiGuesses": [
                                                             {
                                                                 "guesserUuid": "AI",
                                                                 "guessWord": "노트북",
                                                                 "attempts": 1,
                                                                 "correct": false
                                                             }
                                                         ],
                                                         "playerGuesses": [
                                                             {
                                                                 "guesserUuid": "HEjCRCjE",
                                                                 "guessWord": "천사",
                                                                 "attempts": 1,
                                                                 "correct": true
                                                             }
                                                         ],
                                                         "aiPredictions": [
                                                             {
                                                                 "predicted": "노트북",
                                                                 "confidence": 41.66520535945892
                                                             },
                                                             {
                                                                 "predicted": "달력",
                                                                 "confidence": 36.07162833213806
                                                             },
                                                             {
                                                                 "predicted": "침대",
                                                                 "confidence": 6.018155440688133
                                                             }
                                                         ]
                                                     }
                                                 ]
                                             }
                                         ]
                                     }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "게임 정보를 찾을 수 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "code": "GAME-HISTORY-404-001",
                                        "status": "NOT_FOUND",
                                        "message": "게임 정보가 존재하지 않습니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> getUserGameDetails(@PathVariable(value = "id") Long gameId,
                                         @AuthenticationPrincipal SecurityUserDetails userDetails);


    @Operation(summary = "닉네임 변경", description = "닉네임 변경 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "닉네임 변경 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": "OK",
                                        "message": "성공적으로 수정되었습니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "닉네임 중복 확인 안됨",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "code": "USER-400-001",
                                        "status": "BAD_REQUEST",
                                        "message": "요청한 필드 값이 유효하지 않습니다.",
                                        "fields": {
                                            "nickname": "닉네임 중복 확인이 완료되지 않았습니다."
                                        }
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "409", description = "닉네임 중복",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(name = "존재하는 닉네임", value = """
                                    {
                                             "code": "USER-409-001",
                                             "status": "CONFLICT",
                                             "message": "이미 존재하는 닉네임입니다."
                                    }
                                    """),
                            @ExampleObject(name = "현재 닉네임과 중복", value = """
                                    {
                                             "code": "USER-409-003",
                                             "status": "CONFLICT",
                                             "message": "현재 닉네임과 동일합니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> modifyUserNickname(@Valid @RequestBody NicknameReq nicknameReq,
                                         @AuthenticationPrincipal SecurityUserDetails userDetails);

    @Operation(summary = "채팅 설정 변경", description = "사용자의 전체 채팅 및 귓속말 허용 여부 설정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "채팅 설정 변경 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """ 
                                    {
                                        "status": "OK",
                                        "message": "성공적으로 수정되었습니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "400", description = "잘못된 enum 값 입력",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """ 
                                    {
                                        "status": 400,
                                        "code": "INVALID_INPUT_VALUE",
                                        "message": "필드 'chatOption'에 잘못된 값 'WRONG_VALUE'이(가) 입력되었습니다. 허용되는 값: [ALLOW_ALL, ALLOW_FRIEND_ONLY, DISALLOW]",
                                        "field": "chatOption"
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """ 
                                    {
                                        "status": 401,
                                        "code": "AUTH-401-001",
                                        "message": "인증이 필요합니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """ 
                                    {
                                        "status": "NOT_FOUND",
                                        "code": "USER-404-001",
                                        "message": "존재하지 않는 사용자입니다."
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """ 
                                    {
                                        "status": "INTERNAL_SERVER_ERROR",
                                        "code": "GLOBAL-500-001",
                                        "message": "서버 내부 오류가 발생했습니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> modifyUserChatSetting(@Valid @RequestBody ChatSettingReq chatSettingReq,
                                            @AuthenticationPrincipal SecurityUserDetails userDetails);
}