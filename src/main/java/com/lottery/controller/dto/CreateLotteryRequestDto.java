package com.lottery.controller.dto;

import com.lottery.domain.BallotUnit;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
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
