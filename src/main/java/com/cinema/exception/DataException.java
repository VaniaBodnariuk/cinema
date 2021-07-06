package com.cinema.exception;

public class DataException extends RuntimeException{
    public DataException(String message, Throwable cause) {
        super(message, cause);
    }
}
