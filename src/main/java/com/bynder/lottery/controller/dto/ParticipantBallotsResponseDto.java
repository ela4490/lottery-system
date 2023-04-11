package com.bynder.lottery.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantBallotsResponseDto extends ApiResponseDto {

    List<String> ballots;

    public static ParticipantBallotsResponseDto build(List<String> ballots) {
        return new ParticipantBallotsResponseDto(ballots);
    }
}
