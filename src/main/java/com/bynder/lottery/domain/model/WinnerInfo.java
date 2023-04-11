package com.bynder.lottery.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WinnerInfo {

    private String ballotCode;
    private String participantName;
    private String participantSsn;
    private String lotteryName;
    private String award;
    private Date submissionDate;

}
