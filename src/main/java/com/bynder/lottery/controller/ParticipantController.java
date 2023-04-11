package com.bynder.lottery.controller;

import com.bynder.lottery.controller.dto.*;
import com.bynder.lottery.domain.entity.Lottery;
import com.bynder.lottery.service.ParticipantService;
import com.bynder.lottery.service.RegisterParticipantRequest;
import com.bynder.lottery.service.SubmissionRequest;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/participant")
public class ParticipantController {

    private static final Logger logger = LoggerFactory.getLogger(ParticipantController.class);

    private final ParticipantService participantService;
    private final BeanMapper beanMapper;

    public ParticipantController(ParticipantService participantService, BeanMapper beanMapper) {
        this.participantService = participantService;
        this.beanMapper = beanMapper;
    }

    @ApiOperation(tags = {"participant"}, value = "Registers participant")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Participant registered successfully"),
    })
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public RegisterParticipantResponseDto register(@Valid @RequestBody RegisterParticipantRequestDto registerParticipantRequestDto) {
        logger.info("Registering participant with request: {}", registerParticipantRequestDto);
        final RegisterParticipantRequest registerParticipantRequest = beanMapper.map(registerParticipantRequestDto, RegisterParticipantRequest.class);
        Long participantId = participantService.register(registerParticipantRequest);
        logger.debug("Registered participant with id: {}", participantId);
        return RegisterParticipantResponseDto.build(participantId);
    }

    @ApiOperation(tags = {"participant"}, value = "Read all active registered lotteries")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Read all active registered lotteries successfully"),
    })
    @GetMapping("/{ssn}/lotteries")
    public List<LotteryResponseDto> readActiveRegisteredLotteries(@PathVariable String ssn) {
        logger.info("Reading active registered lotteries for participant with ssn: {}", ssn);
        final List<Lottery> lotteries = participantService.readActiveRegisteredLotteries(ssn);
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
                                lottery.getState()
                        ))
                .collect(Collectors.toList());
        logger.debug("Read active registered lotteries for participant with ssn: {}", ssn);
        return lotteryResponseDtos;
    }

    @ApiOperation(tags = {"participant"}, value = "Submits ballot for participant")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Submitted ballot for participant successfully"),
    })
    @PostMapping("/{ssn}/submit")
    public ParticipantBallotsResponseDto submit(@Valid @RequestBody SubmissionRequestDto requestDto, @PathVariable String ssn) {
        logger.info("Submitting ballot for participant with ssn: {}", ssn);
        final SubmissionRequest submissionRequest = beanMapper.map(requestDto, SubmissionRequest.class);
        submissionRequest.setSsn(ssn);
        final List<String> ballots = participantService.submit(submissionRequest);
        logger.debug("Submitted ballot for participant with ssn: {}", ssn);
        return ParticipantBallotsResponseDto.build(ballots);
    }

    @ApiOperation(tags = {"participant"}, value = "Read all ballots of lottery")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Read all ballots of lottery successfully"),
    })
    @GetMapping("/{ssn}/lottery/{lotteryId}/ballots")
    public ParticipantBallotsResponseDto readAllBallotsOfLottery(@PathVariable String ssn, @PathVariable Long lotteryId) {
        logger.info("Reading all ballots of lottery with id: {} for participant with ssn: {}", lotteryId, ssn);
        final List<String> ballots = participantService.readAllBallotsOfLottery(ssn, lotteryId);
        logger.debug("Read all ballots of lottery with id: {} for participant with ssn: {}", lotteryId, ssn);
        return ParticipantBallotsResponseDto.build(ballots);
    }
}
