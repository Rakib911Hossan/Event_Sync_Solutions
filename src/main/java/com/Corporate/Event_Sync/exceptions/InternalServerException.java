package com.Corporate.Event_Sync.exceptions;

import lombok.Data;

@Data
public class InternalServerException extends RuntimeException {

    private static final long serialVersionUID = -5330068136795055851L;

    public InternalServerException(final String message) {
        super(message);
    }

}
