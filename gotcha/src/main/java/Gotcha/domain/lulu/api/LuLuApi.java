package Gotcha.domain.lulu.api;


import Gotcha.domain.lulu.dto.TaskEvalReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "[LULU GAME API]", description = "LULU GAME 관련 API")
public interface LuLuApi {

    @Operation(summary = "게임 시작", description = "게임 시작 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게임 시작 성공, message 필드에는 발급된 gameID가 반환됩니다.",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": "OK",
                                        "message": "1234"
                                    }
                                """)
                    })
            )
    })
    ResponseEntity<?> startGame();


    @Operation(summary = "키워드 제시", description = "키워드 제시 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "키워드 제시 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "game_id": "7550",
                                        "keyword": "나비",
                                        "situation": "달빛이 흠뻑 젖은 새벽, 투명한 숨결이 바람에 실려 흔들린다. 무엇인가 아주 가벼운 것이, 찢어진 구름 사이를 맴돌며 잊혀진 약속을 속삭인다. 색도 소리도 없는 경계에서, 그 존재는 언제나 날갯짓 하나로 모든 것을 바꿀 듯 아슬아슬하게 머문다."
                                    }
                                """)
                    })
            )
    })
    ResponseEntity<?> generateTask(@PathVariable(value="gameId") String gameId);


    @Operation(summary = "키워드 평가", description = "키워드 평가 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "키워드 평가 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "score": 80,
                                        "feedback": "Good Job",
                                        "game_id": "7550"
                                    }
                                """)
                    })
            )
    })
    ResponseEntity<?> evaluateTask(@PathVariable(value="gameId") String gameId, @RequestBody TaskEvalReq taskEvalReq);
}
