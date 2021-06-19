package com.cinema.exception;

import java.text.MessageFormat;
import java.util.UUID;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String className, UUID id) {
        super(MessageFormat.format("{0} with id={1} not found",
                                    className, id));
    }
}