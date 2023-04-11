package com.bynder.lottery.mapper;

import com.bynder.lottery.controller.dto.WinnerInfoResponseDto;
import com.bynder.lottery.domain.model.WinnerInfo;

public class WinnerMapper {

    public static WinnerInfoResponseDto map(WinnerInfo model) {
        WinnerInfoResponseDto dto = new WinnerInfoResponseDto();
        dto.setAward(model.getAward());
        dto.setBallotCode(model.getBallotCode());
        dto.setLotteryName(model.getLotteryName());
        dto.setParticipantName(model.getParticipantName());
        dto.setParticipantSsn(model.getParticipantSsn());
        dto.setSubmissionDate(model.getSubmissionDate());
        return dto;
    }
}
