package gotcha_common.security;

import org.springframework.security.core.GrantedAuthority;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;

public class CustomGrantedAuthority implements GrantedAuthority {

    private final String authority;

    @JsonCreator
    public CustomGrantedAuthority(@JsonProperty("authority") String role) {
        Assert.hasText(role, "role은 비어있을 수 없습니다.");
        this.authority = role;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

}