package gotcha_domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Role {
    USER("ROLE_USER"), ADMIN("ROLE_ADMIN"), GUEST("ROLE_GUEST");

    private final String value;
}
