package Gotcha.domain.guestUser.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "[게스트 API]", description = "게스트 관련 API")
public interface GuestUserApi {
    @Operation(summary = "게스트 로그인", description = "게스트 로그인 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게스트 로그인 성공",
            content = @Content(mediaType = "application/json", examples = {
                    @ExampleObject(value = """
                            {
                                "accessToken": "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiLsi5zrgYTrn6zsmrTrgpntg4AiLCJyb2xlIjoiR1VFU1QiLCJ1c2VySWQiOjY3OTkyNTU1MjE4ODE1NzMyMDEsImlzcyI6ImdvdGNoYSEiLCJpYXQiOjE3NDI5OTUyMjksImV4cCI6MTc0Mjk5NzAyOX0.jwE4E2ZS0jNaKFifOQjeDUFGhlfaqXOyN_kxgfC6rLw"
                            }
                            """)
            }))
    })
    ResponseEntity<?> guestSignIn();
}
