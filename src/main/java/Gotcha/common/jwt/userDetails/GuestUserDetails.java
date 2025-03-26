package Gotcha.common.jwt.userDetails;

import Gotcha.common.security.CustomGrantedAuthority;
import Gotcha.domain.guestUser.entity.GuestUser;
import Gotcha.domain.user.entity.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GuestUserDetails implements UserDetails {
    private GuestUser guestUser;

    public GuestUserDetails(GuestUser guestUser) {
        this.guestUser = guestUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new CustomGrantedAuthority(Role.GUEST.getValue()));
    }

    @Override
    public String getPassword() {
        return null;
    }

    public Long getGuestId() {
        return guestUser.getGuestId();
    }

    @Override
    public String getUsername() {
        return guestUser.getGuestNickname();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
