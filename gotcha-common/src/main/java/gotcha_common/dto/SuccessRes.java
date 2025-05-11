package gotcha_common.dto;

import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record SuccessRes(
        HttpStatus status,
        String message
) {
    public static SuccessRes from(String message) {
        return SuccessRes.builder()
                .status(HttpStatus.OK)
                .message(message)
                .build();
    }
}
