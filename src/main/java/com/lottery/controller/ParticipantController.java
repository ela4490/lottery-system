package com.lottery.controller;

import com.lottery.controller.dto.*;
import com.lottery.service.ParticipantService;
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

    private static final Logger LOG = LoggerFactory.getLogger(ParticipantController.class);
    private final ParticipantService participantService;

    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    @ApiOperation(tags = {"participant"}, value = "Registers participant")
    @ApiResponses(value = @ApiResponse(code = 200, message = "Participant registered successfully"))
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public RegisterParticipantResponseDto register(@Valid @RequestBody RegisterParticipantRequestDto registerParticipantRequestDto) {
        LOG.info("Registering participant with request: {}", registerParticipantRequestDto);
        final var participantId = participantService.register(registerParticipantRequestDto);
        LOG.debug("Registered participant with id: {}", participantId);
        return RegisterParticipantResponseDto.build(participantId);
    }

    @ApiOperation(tags = {"participant"}, value = "Read all active registered lotteries")
    @ApiResponses(value = @ApiResponse(code = 200, message = "Read all active registered lotteries successfully"))
    @GetMapping("/{ssn}/lotteries")
    public List<LotteryResponseDto> readActiveRegisteredLotteries(@PathVariable String ssn) {
        LOG.info("Reading active registered lotteries for participant with ssn: {}", ssn);
        final var lotteries = participantService.readActiveRegisteredLotteries(ssn);
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
                                lottery.getState()
                        ))
                .collect(Collectors.toList());
        LOG.debug("Read active registered lotteries for participant with ssn: {}", ssn);
        return lotteryResponseDtos;
    }

    @ApiOperation(tags = {"participant"}, value = "Submits ballot for participant")
    @ApiResponses(value = @ApiResponse(code = 200, message = "Submitted ballot for participant successfully"))
    @PostMapping("/{ssn}/submit")
    public ParticipantBallotsResponseDto submit(@Valid @RequestBody SubmissionRequestDto submissionRequest, @PathVariable String ssn) {
        LOG.info("Submitting ballot for participant with ssn: {}", ssn);
        submissionRequest.setSsn(ssn);
        final var ballots = participantService.submit(submissionRequest);
        LOG.debug("Submitted ballot for participant with ssn: {}", ssn);
        return ParticipantBallotsResponseDto.build(ballots);
    }

    @ApiOperation(tags = {"participant"}, value = "Read all ballots of lottery")
    @ApiResponses(value = @ApiResponse(code = 200, message = "Read all ballots of lottery successfully"))
    @GetMapping("/{ssn}/lottery/{lotteryId}/ballots")
    public ParticipantBallotsResponseDto readAllBallotsOfLottery(@PathVariable String ssn, @PathVariable Long lotteryId) {
        LOG.info("Reading all ballots of lottery with id: {} for participant with ssn: {}", lotteryId, ssn);
        final var ballots = participantService.readAllBallotsOfLottery(ssn, lotteryId);
        LOG.debug("Read all ballots of lottery with id: {} for participant with ssn: {}", lotteryId, ssn);
        return ParticipantBallotsResponseDto.build(ballots);
    }
}
