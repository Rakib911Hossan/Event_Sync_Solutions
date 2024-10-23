package com.Corporate.Event_Sync.exceptions;


public class NotFoundException extends InternalServerException {

    private static final long serialVersionUID = -6870732210014274010L;

    public NotFoundException(final String message) {
        super(message);
    }

}
