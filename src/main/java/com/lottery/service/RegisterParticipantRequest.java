package com.lottery.service;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterParticipantRequest {
    private String name;
    private String ssn;
    private Long lotteryId;
}
