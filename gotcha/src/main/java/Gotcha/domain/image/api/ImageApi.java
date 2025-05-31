package Gotcha.domain.image.api;


import gotcha_domain.auth.SecurityUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "[이미지 업로드 API]", description = "이미지 업로드 API")
public interface ImageApi {

    @Operation(summary = "이미지 업로드", description = "이미지 업로드 API, png, jpg, jpeg 파일만 허용합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이미지 업로드 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": "OK",
                                        "message": "https://gotchaai-image-bucket.s3.ap-northeast-2.amazonaws.com/useruuid/6a0f9e3d-1b2c-4d5e-8f2a-8b02f60d92e4.jpg
                                                    "
                                    }
                                    """)
                    })
            ),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 파일 확장자(png, jpg, jpeg만 허용)",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": "BAD_REQUEST",
                                        "code" : "IMAGE-400-001",
                                        "message": "이미지 타입이 유효하지 않습니다.(png, jpg, jpeg만 허용)"
                                    }
                                    """)
                    })
            ),
    })
    ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file, SecurityUserDetails userDetails);
}
