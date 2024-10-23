package com.Corporate.Event_Sync.exceptions;

public class PasswordMismatchException extends InternalServerException{
    private static final long serialVersionUID = -6870732210014274010L;

    public PasswordMismatchException(String message) {
        super(message);
    }
}
