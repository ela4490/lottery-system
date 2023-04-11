package com.bynder.lottery.service;

import com.bynder.lottery.domain.BallotUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created on 25/01/2023.
 *
 * @author Armen Aslikyan
 */
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
