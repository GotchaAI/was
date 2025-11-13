package Gotcha.domain.auth.exception;

import gotcha_common.exception.CustomException;
import gotcha_common.exception.exceptionCode.ExceptionCode;
import lombok.Getter;

import java.util.Map;

@Getter
public class UserAccountStatusException extends CustomException {

    private final Map<String, Object> details;

    public UserAccountStatusException(ExceptionCode exceptionCode, Map<String, Object> details) {
        super(exceptionCode);
        this.details = details;
    }

}

