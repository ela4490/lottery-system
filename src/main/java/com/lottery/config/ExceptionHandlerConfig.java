package com.lottery.config;

import com.lottery.domain.model.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerConfig {
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorMessage> exceptionHandler(Throwable exception) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorMessage errorMessage = ErrorMessage.build(exception.getMessage(), exception.getClass().getSimpleName());
        return new ResponseEntity<>(errorMessage, status);
    }
}
