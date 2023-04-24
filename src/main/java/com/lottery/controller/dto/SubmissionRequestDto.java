package com.lottery.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties
public class SubmissionRequestDto {

    @NotNull
    @Positive
    private Long lotteryId;
    @NotNull
    @Positive
    private Integer numberOfBallots;
    private String ssn;
}
