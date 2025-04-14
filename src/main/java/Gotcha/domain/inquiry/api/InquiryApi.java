package Gotcha.domain.inquiry.api;

import Gotcha.common.jwt.auth.SecurityUserDetails;
import Gotcha.domain.inquiry.dto.InquiryReq;
import Gotcha.domain.inquiry.dto.InquirySortType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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
                                        "totalPages": 1,
                                        "totalElements": 4,
                                        "first": true,
                                        "last": true,
                                        "size": 10,
                                        "content": [
                                            {
                                                "inquiryId": 3,
                                                "title" : "사랑이 식었다고 말해도 돼",
                                                "writer": "T없e맑은i",
                                                "isPrivate": false
                                                "createdAt": "2025-04-14T18:24:13",
                                                "isSolved": true
                                            },
                                            {
                                                "inquiryId": 2,
                                                "title" : "게임이 너무 재밌는 것 같아요!",
                                                "writer": "S2형준S2",
                                                "isPrivate": false
                                                "createdAt": "2025-04-14T16:13:32",
                                                "isSolved": true
                                            }, 
                                            {
                                                "inquiryId": 1,
                                                "title" : "비밀번호를 잊어버렸어요 ㅠㅠ",
                                                "writer": "zx늑대xz"
                                                "isPrivate": false
                                                "createdAt": "2025-04-04T16:05:35",  
                                                "isSolved": false
                                            },
                                            {
                                                "inquiryId": 0,
                                                "title" : "상점 기능도 추가해주세요",
                                                "writer": "T없e맑은i"
                                                "isPrivate": false
                                                "createdAt": "2025-03-31T10:12:15",  
                                                "isSolved": false
                                            }
                                        ],
                                        "pageable": {
                                            "pageNumber": 0,
                                            "pageSize": 10,
                                            "sort": {
                                                "empty": false,
                                                "unsorted": false,
                                                "sorted": true
                                            },
                                            "offset": 0,
                                            "unpaged": false,
                                            "paged": true
                                        },
                                        "numberOfElements": 4, 
                                        "sort": {
                                            "empty": false,
                                            "sorted": true,
                                            "unsorted": false
                                        },
                                        "number": 0, 
                                        "empty": false     
                                    }
                                    """)
                    })
            )
    })
    ResponseEntity<?> getInquiries(@RequestParam(value = "keyword", required = false) String keyword,
                                   @RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
                                   @RequestParam(value = "sort", defaultValue = "DATE_DESC")InquirySortType sort,
                                   @RequestParam(value = "isSolved", required = false) Boolean isSolved);

    @Operation(summary = "내가 쓴 QnA 목록 조회", description = "내가 쓴 QnA(질문) 목록 조회 API, Keyword 검색 및 날짜순 정렬 가능")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "나의 QnA(질문) 목록 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "totalPages": 1,
                                        "totalElements": 2,
                                        "first": true,
                                        "last": true,
                                        "size": 10,
                                        "content": [
                                            {
                                                "inquiryId": 3,
                                                "title" : "사랑이 식었다고 말해도 돼",
                                                "writer": "T없e맑은i",
                                                "isPrivate": false
                                                "createdAt": "2025-04-14T18:24:13",
                                                "isSolved": true
                                            },
                                            {
                                                "inquiryId": 0,
                                                "title" : "상점 기능도 추가해주세요",
                                                "writer": "T없e맑은i"
                                                "isPrivate": false
                                                "createdAt": "2025-03-31T10:12:15",  
                                                "isSolved": false
                                            }
                                        ],
                                        "pageable": {
                                            "pageNumber": 0,
                                            "pageSize": 10,
                                            "sort": {
                                                "empty": false,
                                                "unsorted": false,
                                                "sorted": true
                                            },
                                            "offset": 0,
                                            "unpaged": false,
                                            "paged": true
                                        },
                                        "numberOfElements": 2, 
                                        "sort": {
                                            "empty": false,
                                            "sorted": true,
                                            "unsorted": false
                                        },
                                        "number": 0, 
                                        "empty": false     
                                    }
                                    """)
                    })
            )
    })
    ResponseEntity<?> getMyInquiries(@RequestParam(value = "keyword", required = false) String keyword,
                                     @RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
                                     @RequestParam(value = "sort", defaultValue = "DATE_DESC")InquirySortType sort,
                                     @RequestParam(value = "isSolved", required = false) Boolean isSolved,
                                     @AuthenticationPrincipal SecurityUserDetails userDetails);

    @Operation(summary = "QnA 조회", description = "QnA ID를 받아 해당 QnA 정보를 조회하는 API")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "QnA 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "inquiryId": 2,
                                        "title" : "게임이 너무 재밌는 것 같아요!",
                                        "writer": "S2형준S2",
                                        "content': "이 편지는 영국에서 최초로 시작되어 일년에 한바퀴를 돌면서 받는 사람에게 행운을 주었고 지금은 당신에게로 옮겨진 이 편지는 4일 안에 당신 곁을 ...더보기",
                                        "isPrivate": false,
                                        "createdAt": "2025-04-14T16:13:32",
                                        "isSolved": true,
                                        "answer":{
                                            "writer": "묘묘",
                                            "content": "감사합니묘! 더 발전하는 갓챠가 되겠습니묘!",
                                            "createdAt": "2025-04-14T18:00:00"
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
                                        "status": "NOT_FOUND",
                                        "message": "존재하지 않는 QnA입니다."
                                    }
                                    """
                            )
                    })
            )
    })
    ResponseEntity<?> getInquiryById(@PathVariable(value = "inquiryId") Long inquiryId);

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
            @ApiResponse(responseCode = "400", description = "필드 검증 오류",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                    {
                        "status": "BAD_REQUEST",
                        "message": "필드 검증 오류입니다.",
                        "fields": {
                            "title": "제목은 필수 입력 사항입니다.",
                            "content": "내용은 필수 입력 사항입니다."
                        }
                    }
                    """)
                    })
            ),
            @ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                    {
                        "status": "FORBIDDEN",
                        "message": "권한이 없습니다."
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
                        "status": "BAD_REQUEST",
                        "message": "필드 검증 오류입니다.",
                        "fields": {
                            "title": "제목은 필수 입력 사항입니다.",
                            "content": "내용은 필수 입력 사항입니다."
                        }
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
