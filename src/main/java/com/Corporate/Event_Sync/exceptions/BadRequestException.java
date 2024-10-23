package com.Corporate.Event_Sync.exceptions;

import lombok.Data;

@Data
public class BadRequestException extends RuntimeException {

    private static final long serialVersionUID = -5330068136795055851L;

    public BadRequestException(final String message) {
        super(message);
    }

}
