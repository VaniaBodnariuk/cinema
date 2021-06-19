package com.cinema.exception;

import java.text.MessageFormat;
import java.util.UUID;

public class UniqueFieldException extends RuntimeException{
    public UniqueFieldException(String className,UUID id, String fieldName) {
        super(MessageFormat.format("{0} with id={1}: such {2} already"
                                   + " exist(s)", className, id, fieldName));
    }
}
