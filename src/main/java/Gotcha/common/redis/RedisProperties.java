package Gotcha.common.redis;

public interface RedisProperties {

    String REFRESH_TOKEN_KEY_PREFIX = "refreshToken::";
    String BLACKLIST_KEY_PREFIX = "blackList::";
    String CODE_KEY_PREFIX = "code::";
    String EMAIL_VERIFY_KEY_PREFIX = "emailVerify::";
    String NICKNAME_VERIFY_KEY_PREFIX = "nicknameVerify::";
    String GUEST_KEY_PREFIX = "guest::";

    long CODE_EXPIRATION_TIME = 5*60;

    long GUEST_TTL_SECONDS = 30 * 60;
}