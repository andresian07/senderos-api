package com.andres.senderos.web.exception;

public class MissingCoordinatesException extends RuntimeException {
    public MissingCoordinatesException(String message) {
        super(message);
    }
}
