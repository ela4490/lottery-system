package com.lottery.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateLotteryResponseDto extends ApiResponseDto {

    private Long id;

    public static CreateLotteryResponseDto build(Long id) {
        return new CreateLotteryResponseDto(id);
    }
}
