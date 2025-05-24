package Gotcha.domain.ranking.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "[랭킹 API]", description = "랭킹 관련 API")
public interface RankingApi {
    @Operation(summary = "전체 랭킹 조회", description = "전체 랭킹 조회 API")
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
}
