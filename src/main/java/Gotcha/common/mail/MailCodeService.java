package Gotcha.common.mail;

import Gotcha.common.exception.CustomException;
import Gotcha.common.exception.exceptionCode.GlobalExceptionCode;
import Gotcha.common.mail.exception.MailExceptionCode;
import Gotcha.common.util.RedisUtil;
import Gotcha.domain.auth.dto.EmailCodeVerifyReq;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static Gotcha.common.redis.RedisProperties.CODE_EXPIRATION_TIME;
import static Gotcha.common.redis.RedisProperties.CODE_KEY_PREFIX;
import static Gotcha.common.redis.RedisProperties.EMAIL_VERIFY_KEY_PREFIX;

@Service
@RequiredArgsConstructor
public class MailCodeService {
    private final MailService mailService;
    private final RedisUtil redisUtil;

    private static final int RESEND_THRESHOLD_SECONDS = 4 * 60;
    private static final long EMAIL_VERIFY_EXPIRATION_TIME = 10 * 60;

    public void sendCodeToMail(String email) {
        if (!checkRetryEmail(email)) {
            throw new CustomException(MailExceptionCode.ALREADY_MAIL_REQUEST);
        }

        String authCode = createCode();

        mailService.sendMail(email, authCode);

        String key = CODE_KEY_PREFIX + email;
        redisUtil.setData(key, authCode);
        redisUtil.setDataExpire(key, CODE_EXPIRATION_TIME);
    }

    private boolean checkRetryEmail(String email) {
        String key = CODE_KEY_PREFIX + email;

        long expireTime = redisUtil.getExpire(key, TimeUnit.SECONDS);
        //-2인 경우 : 해당 email에 대한 키 존재X
        return expireTime == -2 || expireTime <= RESEND_THRESHOLD_SECONDS;
    }

    private String createCode() {
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                sb.append(random.nextInt(10));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new CustomException(GlobalExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    public void verifiedCode(EmailCodeVerifyReq emailCodeVerifyReq) {
        String key = CODE_KEY_PREFIX + emailCodeVerifyReq.email();
        Optional<String> storedCode = Optional.ofNullable((String) redisUtil.getData(key));

        if (storedCode.isEmpty()) {
            throw new CustomException(MailExceptionCode.CODE_EXPIRED);
        }
        if (!storedCode.get().equals(emailCodeVerifyReq.code())) {
            throw new CustomException(MailExceptionCode.INVALID_CODE);
        }

        redisUtil.deleteData(key);

        String verifiedKey = EMAIL_VERIFY_KEY_PREFIX + emailCodeVerifyReq.email();
        redisUtil.setData(verifiedKey, "true");
        redisUtil.setDataExpire(verifiedKey, EMAIL_VERIFY_EXPIRATION_TIME);
    }
}
