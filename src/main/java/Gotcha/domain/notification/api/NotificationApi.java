package Gotcha.domain.notification.api;


import Gotcha.domain.notification.dto.NotificationSortType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "[공지사항 API]", description = "공지사항 관련 API")
public interface NotificationApi {

    @Operation(summary = "공지사항 목록", description = "공지사항 목록을 조회하는 API. Keyword로 검색 및 정렬 가능.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "공지사항 목록 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                        @ExampleObject(value = """
                                {
                                    "totalPages": 1, 
                                    "totalElements": 3, 
                                    "first": true, 
                                    "last": true, 
                                    "size": 10, 
                                    "content": [
                                        {
                                            "notificationId": 2,
                                            "title" : "걍 공지사항이다",
                                            "createdAt": "2025-03-27T16:13:32",
                                            "writer" : "묘묘"
                                        }, 
                                        {
                                            "notificationId": 1,
                                            "title" : "서버 점검 안내",
                                            "createdAt": "2025-03-27T16:05:35",  
                                            "writer": "루루"
                                        },
                                        {
                                            "notificationId": 0,
                                            "title": "이달의 인공지능 선정 결과",
                                            "createdAt" : "2001-11-16T16:02:26",
                                            "writer": "킹형준"
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
                                    "numberOfElements": 3, 
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
    ResponseEntity<?> getNotifications(@RequestParam(value = "keyword", required = false) String keyword,
                                       @RequestParam(value = "page",defaultValue = "0") @Min(0) Integer page,
                                       @RequestParam(value = "sort", defaultValue = "DATE_DESC") NotificationSortType sort);



    @Operation(summary = "공지사항 조회", description = "공지사항 ID를 받아 해당 공지사항을 조회하는 API")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "공지사항 조회 성공",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "title": "걍 공지사항이다",
                                        "content": "걍 공지사항이다 인마",
                                        "createdAt": "2025-03-27T16:13:32",
                                        "modifiedAt": "2025-11-16T16:13:32",
                                        "writer": "묘묘"
                                    }
                                    """
                            )
                    })
            ),
            @ApiResponse(
                    responseCode = "404", description = "존재하지 않는 공지사항",
                    content = @Content(mediaType = "application/json", examples = {
                            @ExampleObject(value = """
                                    {
                                        "status": "NOT_FOUND",
                                        "message": "존재하지 않는 공지사항입니다."
                                    }
                                    """
                            )
                    })
            )

    })
    ResponseEntity<?> getNotificationById(@PathVariable(value = "notificationId") Long notificationId);


}
