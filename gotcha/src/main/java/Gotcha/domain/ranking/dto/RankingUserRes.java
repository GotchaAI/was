package Gotcha.domain.ranking.dto;

import gotcha_domain.user.User;
import lombok.Builder;

@Builder
public record RankingUserRes(
        Long rank,
        String nickname,
        Long exp,
        Integer level
) {
    public static RankingUserRes of(User user, Long rank) {
        return RankingUserRes.builder()
                .nickname(user.getNickname())
                .exp(user.getExp())
                .level(user.getLevel())
                .rank(rank)
                .build();
    }
}
