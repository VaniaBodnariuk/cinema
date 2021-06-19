package com.cinema.exception;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import java.text.MessageFormat;
import java.util.Set;
import static java.util.stream.Collectors.joining;

public class CustomValidationException extends ValidationException {
    public CustomValidationException(
            Set<ConstraintViolation<Object>> violations, String className) {
        super(MessageFormat.format("{0} : {1}", className,
                                   getViolationsMessages(violations)));
    }

    private static String getViolationsMessages(
            Set<ConstraintViolation<Object>> violations) {
        return violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(joining(". "));
    }

    public CustomValidationException(String message) {
        super(message);
    }
}
