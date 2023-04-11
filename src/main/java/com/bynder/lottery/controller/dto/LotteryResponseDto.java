package com.bynder.lottery.controller.dto;

import com.bynder.lottery.domain.BallotUnit;
import com.bynder.lottery.domain.LotteryState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LotteryResponseDto extends ApiResponseDto {

    private Long id;
    private String name;
    private Timestamp createDate;
    private String award;
    private Long ballotPrice;
    private BallotUnit ballotUnit;
    private LotteryState state;

    public static LotteryResponseDto build(Long id,
                                           String name,
                                           Timestamp createDate,
                                           String award,
                                           Long ballotPrice,
                                           BallotUnit ballotUnit,
                                           LotteryState state) {

        return new LotteryResponseDto(
            id,
            name,
            createDate,
            award,
            ballotPrice,
            ballotUnit,
            state);
    }

}
