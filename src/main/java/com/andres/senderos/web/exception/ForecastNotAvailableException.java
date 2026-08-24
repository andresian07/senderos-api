package com.andres.senderos.web.exception;

public class ForecastNotAvailableException extends RuntimeException {
    public ForecastNotAvailableException(String message) {
        super(message);
    }
}
