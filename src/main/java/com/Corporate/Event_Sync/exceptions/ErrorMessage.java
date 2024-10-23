package com.Corporate.Event_Sync.exceptions;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ErrorMessage {
    private int statusCode;
    private Date timestamp;
    private String message;
}
