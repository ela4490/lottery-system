package com.lottery.controller.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterParticipantRequestDto {

    @NotEmpty
    private String name;
    @NotEmpty
    private String ssn;
    @NotNull
    @Positive
    private Long lotteryId;
}
