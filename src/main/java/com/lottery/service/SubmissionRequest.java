package com.lottery.service;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionRequest {
    private Long lotteryId;
    private Integer numberOfBallots;
    private String ssn;
}
