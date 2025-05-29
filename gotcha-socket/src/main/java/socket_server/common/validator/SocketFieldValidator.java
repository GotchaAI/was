package socket_server.common.validator;

import jakarta.validation.ConstraintViolation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import jakarta.validation.Validator;
import socket_server.common.exception.ErrorType;
import socket_server.common.exception.SocketFieldValidationException;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SocketFieldValidator {
    private final Validator validator;

    public <T> void validateOrThrow(T target, ErrorType source) {
        Set<ConstraintViolation<T>> violations = validator.validate(target);
        if (!violations.isEmpty()) {
            Map<String, String> fieldErrors = violations.stream()
                    .collect(Collectors.toMap(
                            v -> v.getPropertyPath().toString(),
                            ConstraintViolation::getMessage,
                            (existing, replacement) -> existing
                    ));
            throw new SocketFieldValidationException(source, fieldErrors);
        }
    }

}
