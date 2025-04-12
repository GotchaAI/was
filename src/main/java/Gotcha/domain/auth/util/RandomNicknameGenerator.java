package Gotcha.domain.auth.util;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RandomNicknameGenerator {
    private static final List<String> adjectives = Arrays.asList(
            "귀여운", "무서운", "배고픈", "졸린", "쓸쓸한", "취한", "웃긴", "멋진", "시끄러운", "느린"
    );

    private static final List<String> nouns = Arrays.asList(
            "고양이", "강아지", "곰", "너구리", "햄스터", "호랑이", "펭귄", "낙타", "토끼", "벌새"
    );

    private static final Random random = new Random();

    public static String generateNickname() {
        String nickname;

        do {
            String adj = adjectives.get(random.nextInt(adjectives.size()));
            String noun = nouns.get(random.nextInt(nouns.size()));
            nickname = adj + noun;
        } while (nickname.length() > 6);

        return nickname;
    }
}
