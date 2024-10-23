package com.Corporate.Event_Sync.exceptions;


public class UnprocessableContentException extends InternalServerException {

    private static final long serialVersionUID = -6870732210014274019L;

    public UnprocessableContentException(final String message) {
        super(message);
    }

}
