package com.Corporate.Event_Sync.exceptions;


public class ConflictException extends InternalServerException {

    private static final long serialVersionUID = -6870732210014274011L;

    public ConflictException(final String message) {
        super(message);
    }

}
