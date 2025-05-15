package socket_server.common.auth;

import gotcha_domain.user.Role;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

//TODO: 이후 WebSocket 연결 시 Authorization 헤더의 JWT를 파싱해 userId와 Role을 설정하도록 수정 예정
//      현재는 구동만을 위한 간략화 코드.
public class StompPrincipalHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {

        // 1) user-id 헤더 또는 쿼리 파라미터로부터 사용자 이름을 꺼냄
        String userId = request.getHeaders().getFirst("user-id");

        // 2) 역할 정보
        Role role = Role.USER;
        return new StompPrincipal(userId, role);
    }

}