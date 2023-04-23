package com.lottery.controller.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiResponseDto {
    private Long timeSpent;
    private String apiVersion;
}
