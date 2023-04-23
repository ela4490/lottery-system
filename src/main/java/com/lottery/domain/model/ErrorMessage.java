package com.lottery.domain.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessage {

    private String message;
    private String exception;

    public static ErrorMessage build(String message, String exception) {
        return new ErrorMessage(message, exception);
    }

}
