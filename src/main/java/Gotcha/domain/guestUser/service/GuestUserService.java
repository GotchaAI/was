package Gotcha.domain.guestUser.service;

import Gotcha.common.jwt.JwtHelper;
import Gotcha.common.util.RedisUtil;
import Gotcha.domain.auth.dto.TokenDto;
import Gotcha.domain.guestUser.entity.GuestUser;
import Gotcha.domain.guestUser.util.RandomNicknameGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class GuestUserService {
    private final JwtHelper jwtHelper;
    private  final RedisUtil redisUtil;

    private static final long GUEST_TTL_SECONDS = 30 * 60;

    public TokenDto createGuestAccessToken() {
        //무작위 아이디 값 생성
        Long guestId;
        do{
             guestId = UUID.randomUUID().getMostSignificantBits();
        }while(redisUtil.existed("guest:" + guestId));
        //무작위 닉네임 생성
        String nickname = RandomNicknameGenerator.generateNickname();

        //게스트 유저 생성
        GuestUser guestUser = GuestUser.builder()
                .guestId(guestId)
                .guestNickname(nickname)
                .build();

        //게스트 유저 정보를 Redis에 저장
        redisUtil.setData("guest:" + guestId, guestUser);
        redisUtil.setDataExpire("guest:" + guestId, GUEST_TTL_SECONDS);

        //게스트 유저 토큰 생성
        return jwtHelper.createGuestToken(guestUser);
    }

    public GuestUser getGuestUser(Long guestId){
        //매 요청 시 게스트가 활동 중이라는 의미이므로 데이터가 삭제되지 않게 하기 위해 TTL을 갱신
        redisUtil.setDataExpire("guest:" + guestId, GUEST_TTL_SECONDS);
        return (GuestUser) redisUtil.getData("guest:" + guestId);
    }
}
