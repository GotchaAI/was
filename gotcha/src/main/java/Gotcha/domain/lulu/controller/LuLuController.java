package Gotcha.domain.lulu.controller;


import Gotcha.domain.lulu.api.LuLuApi;
import Gotcha.domain.lulu.dto.TaskEvalReq;
import Gotcha.domain.lulu.dto.TaskEvalRes;
import Gotcha.domain.lulu.dto.TaskStartRes;
import Gotcha.domain.lulu.service.LuLuAIClientService;
import gotcha_common.dto.SuccessRes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/game")
public class LuLuController implements LuLuApi {

    private final LuLuAIClientService luLuAIClientService;

    @GetMapping("/lulu/start")
    public ResponseEntity<?> startGame() {
        return ResponseEntity.ok().body(SuccessRes.from(luLuAIClientService.startGame().gameId()));
    }

    @GetMapping("/lulu/task/{gameId}")
    public ResponseEntity<?> generateTask(@PathVariable String gameId) {
        return ResponseEntity.ok().body(luLuAIClientService.generateTask(gameId));
    }


    @PostMapping("/lulu/evaluate/{gameId}")
    public ResponseEntity<?> evaluateTask(@PathVariable String gameId, @RequestBody TaskEvalReq taskEvalReq) {
        return ResponseEntity.ok().body(luLuAIClientService.evaluateTask(gameId, taskEvalReq));
    }


}
