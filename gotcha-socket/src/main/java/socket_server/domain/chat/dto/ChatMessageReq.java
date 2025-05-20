package socket_server.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ChatMessageReq (
        String receiverUuid,
        String content
) {
}
