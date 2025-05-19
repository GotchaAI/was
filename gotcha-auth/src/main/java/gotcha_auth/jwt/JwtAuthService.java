package gotcha_auth.jwt;

import gotcha_auth.exception.JwtExceptionCode;
import gotcha_domain.user.Role;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtAuthService {
    private static final String SPECIAL_CHARACTERS_PATTERN = "[`':;|~!@#$%()^&*+=?/{}\\[\\]\\\"\\\\\"]+$";

    private final TokenProvider tokenProvider;
    private final BlackListTokenService blackListTokenService;
    private final UserDetailsService userDetailsService;
    private final UserDetailsService guestDetailsService;

    public JwtAuthService(TokenProvider tokenProvider,
                          BlackListTokenService blackListTokenService,
                          @Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService,
                          @Qualifier("guestDetailsService") UserDetailsService guestDetailsService) {
        this.tokenProvider = tokenProvider;
        this.blackListTokenService = blackListTokenService;
        this.userDetailsService = userDetailsService;
        this.guestDetailsService = guestDetailsService;
    }

    public Authentication authenticate(String accessTokenHeader) {

        if (accessTokenHeader == null || !accessTokenHeader.startsWith("Bearer ")) {
            throw new AuthenticationServiceException(JwtExceptionCode.ACCESS_TOKEN_NOT_FOUND.getMessage());
        }

        String accessToken = resolveAccessToken(accessTokenHeader);

        String role = tokenProvider.getRole(accessToken);
        UserDetails userDetails;

        System.out.println(role);

        String uuid = tokenProvider.getUuid(accessToken);
        userDetails = role.equals(String.valueOf(Role.GUEST))
                ? guestDetailsService.loadUserByUsername(uuid)
                : userDetailsService.loadUserByUsername(uuid);

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    private String resolveAccessToken(String accessTokenGetHeader) {
        String accessToken = accessTokenGetHeader.substring(JwtProperties.TOKEN_PREFIX.length()).trim();

        accessToken = accessToken.replaceAll(SPECIAL_CHARACTERS_PATTERN, "");

        if (tokenProvider.isExpired(accessToken)) {
            throw new ExpiredJwtException(null, null, JwtExceptionCode.ACCESS_TOKEN_EXPIRED.getMessage());
        }

        if (blackListTokenService.existsBlackListCheck(accessToken)) {
            throw new ExpiredJwtException(null, null, JwtExceptionCode.ACCESS_TOKEN_EXPIRED.getMessage());
        }
        return accessToken;
    }
}

