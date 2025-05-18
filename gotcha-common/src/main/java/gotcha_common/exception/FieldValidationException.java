package gotcha_common.exception;

import lombok.Getter;

import java.util.Map;


@Getter
public class FieldValidationException extends RuntimeException{
    private final Map<String, String> fieldErrors;

    public FieldValidationException(String field, String message) {
        super("필드 검증에 실패했습니다.");
        this.fieldErrors = Map.of(field, message);
    }

    public FieldValidationException(Map<String, String> fieldErrors) {
        super("입력값 검증에 실패했습니다.");
        this.fieldErrors = fieldErrors;
    }

}
