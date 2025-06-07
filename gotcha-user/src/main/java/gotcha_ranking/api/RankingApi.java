package gotcha_ranking.api;

import gotcha_domain.auth.SecurityUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "[랭킹 API]", description = "랭킹 관련 API")
public interface RankingApi {

    @Operation(summary = "전체 랭킹 조회", description = "전체 랭킹 페이지 별 조회 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전체 랭킹 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    [
                                        {
                                            "rank": 1,
                                            "nickname": "관리자",
                                            "exp": 2222,
                                            "level": 11
                                        },
                                        {
                                            "rank": 2,
                                            "nickname": "테스트",
                                            "exp": 992,
                                            "level": 7
                                        },
                                        {
                                            "rank": 3,
                                            "nickname": "테스터다",
                                            "exp": 722,
                                            "level": 6
                                        },
                                        {
                                            "rank": 4,
                                            "nickname": "김승제",
                                            "exp": 0,
                                            "level": 1
                                        },
                                        {
                                            "rank": 5,
                                            "nickname": "김승제임",
                                            "exp": 0,
                                            "level": 1
                                        },
                                        {
                                            "rank": 6,
                                            "nickname": "dryice",
                                            "exp": 0,
                                            "level": 1
                                        }
                                    ]
                                    """)
                    }))
    })
    ResponseEntity<?> getUserRankingPage(@RequestParam(defaultValue = "0") int page);


    @Operation(summary = "자기 랭킹 조회", description = "자기 자신의 랭킹 및 전체 경험치 조회 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "자기 랭킹 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "rank": 2,
                                        "nickname": "테스트",
                                        "exp": 992,
                                        "level": 7
                                    }
                                    """)
                    })),
            @ApiResponse(responseCode = "404", description = "랭킹에 등록되지 않은 사용자",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "code": "RANK-404-001",
                                        "status": "NOT_FOUND",
                                        "message": "랭킹에 등록되지 않은 사용자입니다."
                                    }
                                    """)
                    }))
    })
    ResponseEntity<?> getMyRanking(@AuthenticationPrincipal SecurityUserDetails userDetails);
}
