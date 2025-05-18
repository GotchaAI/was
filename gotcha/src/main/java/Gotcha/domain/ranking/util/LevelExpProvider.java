package Gotcha.domain.ranking.util;

import java.util.List;

public class LevelExpProvider {
/* 경험치 계산 수식
// 계수들
double F1 = (level % 5 == 0) ? 1.2 : 1.0;         // 5레벨마다 보너스
double F2 = (level <= 10) ? 0.7 : 1.0;            // 초반 빠른 성장
double F3 = (level % 20 == 0) ? 1.4 : 1.0;        // 20레벨마다 성장 폭발
double F4 = Math.min(2.0, 1 + Math.log(level));   // 로그 증가 계수 (점진적 성장)

// 기본 식
int term1 = 100;
int term2 = (int) Math.pow(level, 1.5);               // 초반 성장
int term3 = (level * level / 100) * 50;               // 중후반 급격 성장
int term4 = (int) (Math.pow(level, 1.1) * 2);          // 미세 보정
int base = term1 + term2 + term3 + term4;

return F1 * F2 * F3 * F4 * base;
*/


    //누적 경험치 리스트 (0레벨부터 시작)
    private static final List<Integer> CUMULATIVE_EXP_LIST = List.of(
            0, 72, 197, 352, 515, 719, 898, 1086, 1284, 1493, 1806,  // 1~11
            2226, 2665, 3123, 3601, 4200, 4824, 5473, 6149, 6851, 8022,  // 12~21
            9243, 10516, 11789, 13115, 15096, 17154, 19289, 21498, 23792, 26852, // 22~31
            30019, 33294, 36678, 40172, 44575, 49114, 53790, 58605, 63558, 69571, // 32~41
            75751, 82098, 88613, 95298, 103192, 111281, 119566, 128047, 136725, 146725, // 42~51
            156925, 167325, 177925, 188725, 199725, 210975, 222475, 234225, 246225, 259225, // 52~61
            272725, 286725, 301225, 316225, 331725, 347725, 364225, 381225, 398725, 416725, // 62~71
            435225, 454225, 473725, 493725, 514725, 536725, 559225, 582225, 605725, 629725, // 72~81
            654225, 679225, 704725, 730725, 757225, 784225, 811725, 839725, 868225, 897225, // 82~91
            926725, 956725, 987225, 1018225, 1050225, 1082725, 1115725, 1149225, 1183225, 1217725 // 92~101
    );

    //현재 레벨까지 위한 누적 경험치 반환
    public static int getCumulativeExp(int level) {
        if (level < 0 || level >= CUMULATIVE_EXP_LIST.size()) return 0;
        return CUMULATIVE_EXP_LIST.get(level);
    }

    //누적 경험치로 레벨 계산
    public static int getLevelByExp(int exp) {
        for (int i = 1; i < CUMULATIVE_EXP_LIST.size(); i++) {
            if (exp < CUMULATIVE_EXP_LIST.get(i)) {
                return i - 1;
            }
        }
        return CUMULATIVE_EXP_LIST.size() - 1;
    }

    //다음 레벨까지 필요한 경험치
    public static int getExpToNextLevel(int exp) {
        int currentLevel = getLevelByExp(exp);
        if (currentLevel + 1 >= CUMULATIVE_EXP_LIST.size()) return 0;
        return CUMULATIVE_EXP_LIST.get(currentLevel + 1) - exp;
    }


    //현재 레벨 내 경험치 퍼센트
    public static float getProgressInLevel(int exp) {
        int level = getLevelByExp(exp);
        int start = CUMULATIVE_EXP_LIST.get(level);
        int end = level + 1 < CUMULATIVE_EXP_LIST.size() ? CUMULATIVE_EXP_LIST.get(level + 1) : start;
        if (end == start) return 1.0f;
        return (float) (exp - start) / (end - start);
    }
}
