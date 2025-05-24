package Gotcha.domain.inquiry.api;

import Gotcha.domain.inquiry.dto.InquiryReq;
import Gotcha.domain.inquiry.dto.InquirySortType;
import gotcha_domain.auth.SecurityUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "[QnA API]", description = "QnA 문의 관련 API")
public interface InquiryApi {

    @Operation(summary = "QnA 목록 조회", description = "QnA 목록을 조회하는 API, Keyword로 검색 및 날짜순 정렬 가능.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "QnA 목록 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                         "content": [
                                             {
                                                 "inquiryId": 3,
                                                 "title": "게임 시작",
                                                 "writer": "테스트",
                                                 "isPrivate": true,
                                                 "createdAt": "2025-05-24T13:08:03",
                                                 "isSolved": false
                                             },
                                             {
                                                 "inquiryId": 2,
                                                 "title": "질문있습니다!",
                                                 "writer": "관리자",
                                                 "isPrivate": false,
                                                 "createdAt": "2025-05-24T13:07:17",
                                                 "isSolved": false
                                             }
                                         ],
                                         "page": {
                                             "size": 10,
                                             "number": 0,
                                             "totalElements": 2,
                                             "totalPages": 1
                                         }
                                    }
                                    """)
                    })
            )
    })
    ResponseEntity<?> getInquiries(@RequestParam(value = "keyword", required = false) String keyword,
                                   @RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
                                   @RequestParam(value = "sort", defaultValue = "DATE_DESC") InquirySortType sort,
                                   @RequestParam(value = "isSolved", required = false) Boolean isSolved);

    @Operation(summary = "내가 쓴 QnA 목록 조회", description = "내가 쓴 QnA(질문) 목록 조회 API, Keyword 검색 및 날짜순 정렬 가능")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "나의 QnA(질문) 목록 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                         "content": [
                                             {
                                                 "inquiryId": 3,
                                                 "title": "게임 시작",
                                                 "writer": "테스트",
                                                 "isPrivate": true,
                                                 "createdAt": "2025-05-24T13:08:03",
                                                 "isSolved": false
                                             }
                                         ],
                                         "page": {
                                             "size": 10,
                                             "number": 0,
                                             "totalElements": 1,
                                             "totalPages": 1
                                         }
                                    }
                                    """)
                    })
            )
    })
    ResponseEntity<?> getMyInquiries(@RequestParam(value = "keyword", required = false) String keyword,
                                     @RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
                                     @RequestParam(value = "sort", defaultValue = "DATE_DESC") InquirySortType sort,
                                     @RequestParam(value = "isSolved", required = false) Boolean isSolved,
                                     @AuthenticationPrincipal SecurityUserDetails userDetails);

    @Operation(summary = "QnA 조회", description = "QnA ID를 받아 해당 QnA 정보를 조회하는 API")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "QnA 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                         "inquiryId": 3,
                                         "title": "게임 시작",
                                         "writer": "테스트",
                                         "content": "게임 시작 어떻게 해요??",
                                         "isPrivate": true,
                                         "createdAt": "2025-05-24T13:08:03",
                                         "isSolved": true,
                                         "answer": {
                                             "writer": "관리자",
                                             "content": "잘 시작해보십시오",
                                             "createdAt": "2025-05-24T13:31:12"
                                         }
                                    }
                                    """
                            )
                    })
            ),
            @ApiResponse(
                    responseCode = "404", description = "존재하지 않는 QnA",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "code": "QNA-404-001",
                                        "status": "NOT_FOUND",
                                        "message": "존재하지 않는 QnA 입니다."
                                    }
                                    """
                            )
                    })
            ),
            @ApiResponse(
                    responseCode = "403", description = "권한이 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "code": "QNA-403-001",
                                        "status": "FORBIDDEN",
                                        "message": "권한이 없습니다."
                                    }
                                    """)
                    })
            )
    })
    ResponseEntity<?> getInquiryById(@PathVariable(value = "inquiryId") Long inquiryId, @AuthenticationPrincipal SecurityUserDetails userDetails);

    @Operation(summary = "QnA 생성", description = "QnA 생성 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "QnA 생성 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": "OK",
                                        "message": "QnA 생성에 성공했습니다."
                                    }
                                    """)
                    })
            ),
            @ApiResponse(responseCode = "422", description = "필드 검증 오류",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "title": "제목은 필수 입력 사항입니다.",
                                        "content": "내용은 필수 입력 사항입니다."
                                    }
                                    """)
                    })
            )
    })
    ResponseEntity<?> createInquiry(@Valid @RequestBody InquiryReq inquiryReq,
                                    @AuthenticationPrincipal SecurityUserDetails userDetails);

    @Operation(summary = "QnA 수정", description = "QnA 수정 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "QnA 수정 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": "OK",
                                        "message": "QnA 수정에 성공했습니다."
                                    }
                                    """)
                    })
            ),
            @ApiResponse(responseCode = "400", description = "필드 검증 오류",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                         "title": "제목은 필수 입력 사항입니다.",
                                         "content": "내용은 필수 입력 사항입니다."
                                    }
                                    """)
                    })
            ),
            @ApiResponse(responseCode = "403", description = "작성자 불일치 또는 수정 권한 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": "FORBIDDEN",
                                        "message": "권한이 없습니다."
                                    }
                                    """)
                    })
            ),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 QnA",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """                
                                    {
                                        "status": "NOT_FOUND",
                                        "message": "존재하지 않는 QnA입니다."
                                    }
                                    """)
                    })
            )
    })
    ResponseEntity<?> updateInquiry(
            @PathVariable(value = "inquiryId") Long inquiryId,
            @Valid @RequestBody InquiryReq inquiryReq,
            @AuthenticationPrincipal SecurityUserDetails userDetails);

    @Operation(summary = "QnA 삭제", description = "QnA 삭제 API")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "QnA 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "작성자 불일치 또는 삭제 권한 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": "FORBIDDEN",
                                        "message": "권한이 없습니다."
                                    }
                                    """)
                    })
            ),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 QnA",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """                
                                    {
                                        "status": "NOT_FOUND",
                                        "message": "존재하지 않는 QnA 입니다."
                                    }
                                    """)
                    })
            )
    })
    ResponseEntity<?> deleteInquiry(
            @PathVariable(value = "inquiryId") Long inquiryId,
            @AuthenticationPrincipal SecurityUserDetails userDetails);
}
