package gotcha_common.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserDisconnectedEvent {
    private final String userUuid;
}
