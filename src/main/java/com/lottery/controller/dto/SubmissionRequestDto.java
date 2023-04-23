package com.lottery.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionRequestDto {

    @NotNull
    @Positive
    private Long lotteryId;
    @NotNull
    @Positive
    private Integer numberOfBallots;

}
