package com.lottery.controller;

import com.lottery.controller.dto.*;
import com.lottery.service.LotteryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lottery")
public class LotteryController {

    private static final Logger LOG = LoggerFactory.getLogger(LotteryController.class);
    private final LotteryService lotteryService;

    public LotteryController(LotteryService lotteryService) {
        this.lotteryService = lotteryService;
    }

    @ApiOperation(tags = {"lottery"}, value = "Creates lottery")
    @ApiResponses(value = @ApiResponse(code = 200, message = "Lottery created successfully"))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateLotteryResponseDto create(@Valid @RequestBody CreateLotteryRequestDto createLotteryRequest) {
        final var lotteryId = lotteryService.create(createLotteryRequest);
        final var createLotteryResponseDto = CreateLotteryResponseDto.build(lotteryId);
        LOG.debug("Created lottery: {}", createLotteryResponseDto);
        return createLotteryResponseDto;
    }

    @ApiOperation(tags = {"lottery"}, value = "Reads all lotteries")
    @ApiResponses(value = @ApiResponse(code = 200, message = "Lotteries read successfully"))
    @GetMapping
    public List<LotteryResponseDto> readAll(@RequestParam(defaultValue = "0") @Min(0) Integer page,
                                            @RequestParam(defaultValue = "10") @Max(200) @Min(1) Integer size) {
        LOG.info("Reading all lotteries with page: {} and size: {}", page, size);
        final var lotteries = lotteryService.readAll(page, size);
        final var lotteryResponseDtos = lotteries
                .stream()
                .map(lottery ->
                        LotteryResponseDto.build(
                                lottery.getId(),
                                lottery.getName(),
                                lottery.getCreateDate(),
                                lottery.getAward(),
                                lottery.getBallotPrice(),
                                lottery.getBallotUnit(),
                                lottery.getState()))
                .collect(Collectors.toList());
        LOG.debug("Read all lotteries with response: {}", lotteryResponseDtos);
        return lotteryResponseDtos;

    }

    @ApiOperation(tags = {"lottery"}, value = "Finishes lottery")
    @ApiResponses(value = @ApiResponse(code = 200, message = "Lottery finished successfully"))
    @PatchMapping("/{id}/finish")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponseDto finish(@PathVariable Long id) {
        LOG.info("Finishing lottery with id: {}", id);
        lotteryService.finish(id);
        LOG.debug("Finished lottery with id: {}", id);
        return new ApiResponseDto();
    }
}
