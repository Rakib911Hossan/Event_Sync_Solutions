package com.Corporate.Event_Sync.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorMessage> resourceNotFoundException(NotFoundException ex) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.NOT_FOUND.value(),
                new Date(),
                ex.getMessage());

        return new ResponseEntity<ErrorMessage>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorMessage> resourceConflictException(ConflictException ex) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.CONFLICT.value(),
                new Date(),
                ex.getMessage());

        return new ResponseEntity<ErrorMessage>(message, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UnprocessableContentException.class)
    public ResponseEntity<ErrorMessage> unprocessableContentException(UnprocessableContentException ex) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                new Date(),
                ex.getMessage());

        return new ResponseEntity<ErrorMessage>(message, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ErrorMessage> internalServerException(InternalServerException ex) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                new Date(),
                ex.getMessage());

        return new ResponseEntity<ErrorMessage>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorMessage> badRequestException(BadRequestException ex) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                ex.getMessage());

        return new ResponseEntity<ErrorMessage>(message, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ErrorMessage> dataNotFoundException(DataNotFoundException ex) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.OK.value(),
                new Date(),
                ex.getMessage());
        return new ResponseEntity<ErrorMessage>(message, HttpStatus.OK);
    }
    @ExceptionHandler(PasswordMismatchException.class)
    public ResponseEntity<ErrorMessage> PasswordMismatchException(PasswordMismatchException ex){
        ErrorMessage message = new ErrorMessage(
                HttpStatus.UNAUTHORIZED.value(),
                new Date(),
                ex.getMessage());
        return new ResponseEntity<ErrorMessage>(message, HttpStatus.UNAUTHORIZED);
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorMessage> globalExceptionHandler(Exception ex) {
//        ErrorMessage message = new ErrorMessage(
//                HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                new Date(),
//                ex.getMessage());
//
//        return new ResponseEntity<ErrorMessage>(message, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
}
