package com.lottery.domain.model;

import lombok.*;

import java.util.Date;

@Data
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
