package com.bynder.lottery.controller.dto;

import com.bynder.lottery.domain.BallotUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateLotteryRequestDto {

    @NotEmpty
    private String name;
    @NotEmpty
    private String award;
    @NotNull
    @PositiveOrZero
    private Long ballotPrice;
    @NotNull
    private BallotUnit ballotUnit;
}
