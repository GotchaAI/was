package Gotcha.domain.mypage.api;

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
                                            "gameType": "속여라",
                                            "difficulty": "EASY",
                                            "playedAt": "2025-05-14T15:34:26",
                                            "score": 100
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
                                        "gameType": "속여라",
                                        "difficulty": "EASY",
                                        "playedAt": "2025-05-14T15:34:26",
                                        "score": 100,
                                        "rounds": [
                                            {
                                                "round": 1,
                                                "topic": "고양이",
                                                "isSuccess": true,
                                                "similarity": 0.92,
                                                "prediction": [
                                                    "고양이",
                                                    "동물",
                                                    "귀엽다"
                                                ],
                                                "picture": "cat.jpg"
                                            },
                                            {
                                                "round": 2,
                                                "topic": "사과",
                                                "isSuccess": false,
                                                "similarity": 0.45,
                                                "prediction": [
                                                    "바나나",
                                                    "과일",
                                                    "빨간색"
                                                ],
                                                "picture": "apple.jpg"
                                            },
                                            {
                                                "round": 3,
                                                "topic": "자동차",
                                                "isSuccess": true,
                                                "similarity": 0.87,
                                                "prediction": [
                                                    "차",
                                                    "운전",
                                                    "교통수단"
                                                ],
                                                "picture": "car.jpg"
                                            }
                                        ]
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "게임 정보를 찾을 수 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "code": "GAME-404-001",
                                        "status": "NOT_FOUND",
                                        "message": "게임 정보가 존재하지 않습니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> getUserGameDetails(@PathVariable(value = "id") Long gameId,
                                         @AuthenticationPrincipal SecurityUserDetails userDetails);
}
