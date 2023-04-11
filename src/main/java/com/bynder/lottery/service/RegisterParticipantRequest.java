package com.bynder.lottery.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * Created on 25/01/2023.
 *
 * @author Armen Aslikyan
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterParticipantRequest {

    private String name;
    private String ssn;
    private Long lotteryId;
}