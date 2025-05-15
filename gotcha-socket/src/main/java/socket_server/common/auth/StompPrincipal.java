package socket_server.common.auth;

import gotcha_domain.user.Role;
import java.security.Principal;

public class StompPrincipal implements Principal {
    private final String name;
    private final Role role;

    public StompPrincipal(String name, Role roles) {
        this.name = name;
        this.role = roles;
    }

    @Override
    public String getName() {
        return name;
    }

    public Role getRoles() {
        return role;
    }
}