package com.bynder.lottery.controller;

import com.bynder.lottery.controller.dto.ApiResponseDto;
import com.bynder.lottery.controller.dto.CreateLotteryRequestDto;
import com.bynder.lottery.controller.dto.CreateLotteryResponseDto;
import com.bynder.lottery.controller.dto.LotteryResponseDto;
import com.bynder.lottery.domain.entity.Lottery;
import com.bynder.lottery.service.CreateLotteryRequest;
import com.bynder.lottery.service.LotteryService;
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

    private static final Logger logger = LoggerFactory.getLogger(LotteryController.class);

    private final LotteryService lotteryService;
    private BeanMapper beanMapper;

    public LotteryController(LotteryService lotteryService, BeanMapper beanMapper) {
        this.lotteryService = lotteryService;
        this.beanMapper = beanMapper;
    }

    @ApiOperation(tags = {"lottery"}, value = "Creates lottery")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Lottery created successfully"),
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateLotteryResponseDto create(@Valid @RequestBody CreateLotteryRequestDto createLotteryRequestDto) {
        logger.info("Creating lottery with request: {}", createLotteryRequestDto);
        final CreateLotteryRequest createLotteryRequest = beanMapper.map(createLotteryRequestDto, CreateLotteryRequest.class);
        Long lotteryId = lotteryService.create(createLotteryRequest);
        final CreateLotteryResponseDto createLotteryResponseDto = CreateLotteryResponseDto.build(lotteryId);
        logger.debug("Created lottery with response: {}", createLotteryResponseDto);
        return createLotteryResponseDto;
    }

    @ApiOperation(tags = {"lottery"}, value = "Reads all lotteries")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Lotteries read successfully"),
    })
    @GetMapping
    public List<LotteryResponseDto> readAll(@RequestParam(required = false, defaultValue = "0") @Min(0) Integer page,
                                            @RequestParam(required = false, defaultValue = "10") @Max(200) @Min(1) Integer size) {
        logger.info("Reading all lotteries with page: {} and size: {}", page, size);
        List<Lottery> lotteries = lotteryService.readAll(page, size);
        final List<LotteryResponseDto> lotteryResponseDtos = lotteries
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
        logger.debug("Read all lotteries with response: {}", lotteryResponseDtos);
        return lotteryResponseDtos;

    }

    @ApiOperation(tags = {"lottery"}, value = "Finishes lottery")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Lottery finished successfully"),
    })
    @PatchMapping("/{id}/finish")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponseDto finish(@PathVariable Long id) {
        logger.info("Finishing lottery with id: {}", id);
        lotteryService.finish(id);
        logger.debug("Finished lottery with id: {}", id);
        return new ApiResponseDto();
    }
}
