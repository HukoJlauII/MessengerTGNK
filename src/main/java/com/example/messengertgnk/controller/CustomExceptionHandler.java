package com.example.messengertgnk.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
    public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

        @ExceptionHandler(AccessDeniedException.class)
        public final ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
//            ErrorMessage errorDetails = new ErrorMessage(new Date(), ex.getMessage(), request.getDescription(false));
            return new ResponseEntity<>("There are no authorized user", HttpStatus.UNAUTHORIZED);
        }

    }

