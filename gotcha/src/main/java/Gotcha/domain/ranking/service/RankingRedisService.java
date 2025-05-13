package Gotcha.domain.ranking.service;

import Gotcha.domain.ranking.dto.RankingUserRes;
import gotcha_domain.user.User;
import gotcha_user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static gotcha_common.redis.RedisProperties.RANKING_KEY_PREFIX;

@Service
@RequiredArgsConstructor
public class RankingRedisService {
    private final StringRedisTemplate stringRedisTemplate;
    private final UserRepository userRepository;

    private static final int SIZE = 3;

    //경험치 기반 랭킹 등록/업데이트
    public void updateUserExpRanking(Long userId, Long exp) {
        stringRedisTemplate.opsForZSet().add(RANKING_KEY_PREFIX, userId.toString(), exp);
    }

    //상위 N명 조회
    public Set<ZSetOperations.TypedTuple<String>> getTopUsers(int topN) {
        return stringRedisTemplate.opsForZSet().reverseRangeWithScores(RANKING_KEY_PREFIX, 0, topN - 1);
    }

    //특정 페이지 랭킹 조회
    public List<RankingUserRes> getUserRankingPage(int page) {
        int start = page * SIZE;
        int end = start + SIZE - 1;

        //페이지에 해당하는 랭킹 정보 가져오기
        Set<ZSetOperations.TypedTuple<String>> range = stringRedisTemplate.opsForZSet()
                .reverseRangeWithScores(RANKING_KEY_PREFIX, start, end);

        if (range.isEmpty()) {
            return Collections.emptyList();
        }

        //각 사용자 정보 조회
        List<Long> userIds = range.stream()
                .map(tuple -> Long.parseLong(tuple.getValue()))
                .toList();
        List<User> users = userRepository.findAllById(userIds);

        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        //각 랭킹에 해당하는 사용자 정보 및 랭킹 저장
        List<RankingUserRes> result = new ArrayList<>();
        int index = 0;
        for (ZSetOperations.TypedTuple<String> tuple : range) {
            Long userId = Long.parseLong(tuple.getValue());
            User user = userMap.get(userId);
            if (user != null) {
                long rank = start + index + 1; // 실제 순위
                result.add(RankingUserRes.of(user, rank));
            }
            index++;
        }

        return result;
    }

    //사용자 랭킹 조회
    public Long getUserRank(Long userId) {
        return stringRedisTemplate.opsForZSet().reverseRank(RANKING_KEY_PREFIX, userId.toString());
    }

    //사용자 경험치 조회
    public Double getUserExp(Long userId) {
        return stringRedisTemplate.opsForZSet().score(RANKING_KEY_PREFIX, userId.toString());
    }
}
