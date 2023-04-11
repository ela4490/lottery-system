package com.bynder.lottery.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessage {

    private String message;
    private String exception;

    public static ErrorMessage build(String message, String exception) {
        return new ErrorMessage(message, exception);
    }

}
