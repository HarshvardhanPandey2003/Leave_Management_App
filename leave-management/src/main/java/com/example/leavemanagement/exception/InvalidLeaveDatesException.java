package com.example.leavemanagement.exception;

public class InvalidLeaveDatesException extends RuntimeException {
    public InvalidLeaveDatesException(String message) {
        super(message);
    }
}
