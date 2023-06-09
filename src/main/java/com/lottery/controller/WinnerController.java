package com.lottery.controller;

import com.lottery.controller.dto.WinnerInfoResponseDto;
import com.lottery.mapper.WinnerMapper;
import com.lottery.service.WinnerService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/winner")
public class WinnerController {

    private static final Logger LOG = LoggerFactory.getLogger(WinnerController.class);
    private final WinnerService winnerService;

    public WinnerController(WinnerService winnerService) {
        this.winnerService = winnerService;
    }

    @ApiOperation(tags = {"winner"}, value = "Read winner info for date")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Read winner info for date successfully"),
    })
    @GetMapping("/of/{date}")
    public WinnerInfoResponseDto readWinnerByDate(@PathVariable String date) {
        LOG.info("Reading winner info for date: {}", date);
        final var winnerInfo = winnerService.readWinnerByDate(date);
        final var winnerInfoResponseDto = WinnerMapper.map(winnerInfo);
        LOG.debug("Read winner info for date: {}", date);
        return winnerInfoResponseDto;
    }
}
