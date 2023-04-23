package com.lottery.service;

import com.lottery.domain.BallotUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateLotteryRequest {
    private String name;
    private String award;
    private Long ballotPrice;
    private BallotUnit ballotUnit;
}
