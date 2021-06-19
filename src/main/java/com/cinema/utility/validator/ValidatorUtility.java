package com.cinema.utility.validator;

import com.cinema.exception.CustomValidationException;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import java.text.MessageFormat;
import java.util.Set;

public interface ValidatorUtility {
    static void validateModel(Object model){
        javax.validation.Validator validator = Validation.buildDefaultValidatorFactory()
                .getValidator();
        Set<ConstraintViolation<Object>> violations = validator.validate(model);
        if(!violations.isEmpty())
            throw new CustomValidationException(violations,
                                                model.getClass()
                                                        .getSimpleName());
    }

    static void validateFileFormat(String fileFormat,String fileName){
        String regex
                = MessageFormat.format(
                        "^([a-zA-Z0-9\\s_\\\\.\\-\\(\\):])+.{0}$",
                fileFormat);
        if(!fileName.matches(regex))
            throw new CustomValidationException(
                    MessageFormat.format("Required file format: {0}",
                                         fileFormat));
    }

    //TODO Add validateMethod()

    //TODO Add validateConstructor()
}
