package com.hms.user.exception;

public class HMSException extends RuntimeException {
    public HMSException() {
    }
    public HMSException(String message) {
        super(message);
    }
}
