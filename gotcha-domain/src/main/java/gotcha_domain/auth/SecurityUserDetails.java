package gotcha_domain.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gotcha_common.security.CustomGrantedAuthority;
import gotcha_domain.user.Role;
import gotcha_domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SecurityUserDetails implements UserDetails {
    private User user;
    private Collection<? extends GrantedAuthority> authorities;

    public SecurityUserDetails(User user) {
        this.user = user;
        this.authorities = List.of(new CustomGrantedAuthority(user.getRole().getValue()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    public String getNickname(){
        return user.getNickname();
    }

    public Long getId(){
        return user.getId();
    }

    public Role getRole() {
        return user.getRole();
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