package com.bynder.lottery.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
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
