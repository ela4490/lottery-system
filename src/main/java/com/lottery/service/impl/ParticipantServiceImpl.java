package com.lottery.service.impl;

import com.lottery.controller.dto.RegisterParticipantRequestDto;
import com.lottery.controller.dto.SubmissionRequestDto;
import com.lottery.domain.LotteryState;
import com.lottery.domain.entity.Ballot;
import com.lottery.domain.entity.Lottery;
import com.lottery.domain.entity.Participant;
import com.lottery.domain.entity.Submission;
import com.lottery.exception.FinishedLotteryException;
import com.lottery.exception.NotFoundException;
import com.lottery.repository.BallotRepository;
import com.lottery.repository.ParticipantRepository;
import com.lottery.repository.SubmissionRepository;
import com.lottery.service.*;
import com.lottery.util.BallotGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional
public class ParticipantServiceImpl implements ParticipantService {

    private static final Logger LOG = LoggerFactory.getLogger(ParticipantServiceImpl.class);
    private final ParticipantRepository participantRepository;
    private final SubmissionRepository submissionRepository;
    private final BallotRepository ballotRepository;
    private final LotteryService lotteryService;
    private final DateTimeService dateTimeService;

    public ParticipantServiceImpl(ParticipantRepository participantRepository,
                                  SubmissionRepository submissionRepository,
                                  BallotRepository ballotRepository,
                                  LotteryService lotteryService, DateTimeService dateTimeService) {
        this.participantRepository = participantRepository;
        this.submissionRepository = submissionRepository;
        this.ballotRepository = ballotRepository;
        this.lotteryService = lotteryService;
        this.dateTimeService = dateTimeService;
    }

    @Override
    @Transactional
    public Long register(final RegisterParticipantRequestDto registerParticipantRequest) {
        LOG.info("Registering participant {} to lottery {}", registerParticipantRequest.getSsn(), registerParticipantRequest.getLotteryId());
        final var lottery = lotteryService.findById(registerParticipantRequest.getLotteryId());
        if (lottery.getState() != LotteryState.ACTIVE) {
            throw new FinishedLotteryException("Lottery is not active anymore !!!");
        }
        final var participant = participantRepository.findBySsnAndLottery(
                registerParticipantRequest.getSsn(),
                lottery
        ).orElseGet(() -> {
            final var saved = new Participant();
            saved.setName(registerParticipantRequest.getName());
            saved.setSsn(registerParticipantRequest.getSsn());
            saved.setLottery(lottery);
            saved.setRegistrationDate(dateTimeService.currentTimestamp());
            return participantRepository.save(saved);
        });
        LOG.debug("Participant {} registered to lottery {}", participant.getId(), registerParticipantRequest.getLotteryId());
        return participant.getId();
    }

    @Override
    @Transactional
    public List<String> submit(final SubmissionRequestDto submissionRequest) {
        LOG.info("Submitting {} ballots to lottery {}", submissionRequest.getNumberOfBallots(), submissionRequest.getLotteryId());
        final var lottery = lotteryService.findById(submissionRequest.getLotteryId());
        final var optionalParticipant = participantRepository.findBySsnAndLottery(submissionRequest.getSsn(), lottery);
        if (optionalParticipant.isEmpty()) throw new NotFoundException("Participant is not registered for lottery!");

        final var participant = optionalParticipant.get();

        if (LotteryState.FINISHED == participant.getLottery().getState())
            throw new FinishedLotteryException("Lottery is not active anymore!");

        final var submission = new Submission();
        submission.setNumberOfBallots(submissionRequest.getNumberOfBallots());
        submission.setDate(dateTimeService.currentTimestamp());
        submission.setParticipant(participant);
        final var insertedSubmission = submissionRepository.save(submission);

        final var codes = new ArrayList<String>();
        IntStream.rangeClosed(1, insertedSubmission.getNumberOfBallots()).forEach(prefix -> {
            final var b = new Ballot();
            b.setCode(BallotGenerator.generate(prefix, insertedSubmission.getParticipant().getId().intValue()));
            b.setSubmission(insertedSubmission);
            ballotRepository.save(b);
            codes.add(b.getCode());
        });

        LOG.debug("Submitted {} ballots to lottery {}", submissionRequest.getNumberOfBallots(), submissionRequest.getLotteryId());
        return codes;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> readAllBallotsOfLottery(final String ssn, final Long lotteryId) {
        Assert.notNull(ssn, "ssn cannot be null");
        Assert.notNull(lotteryId, "lotteryId cannot be null");
        LOG.info("Reading all ballots of lottery {} for ssn {}", lotteryId, ssn);
        final var ballots = ballotRepository.findAllBySubmission_Participant_SsnAndSubmission_Participant_Lottery_Id(ssn, lotteryId);
        final var collected = ballots
                .stream()
                .map(Ballot::getCode)
                .collect(Collectors.toList());
        LOG.debug("Found {} ballots of lottery {} for ssn {}", collected.size(), lotteryId, ssn);
        return collected;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lottery> readActiveRegisteredLotteries(final String ssn) {
        Assert.notNull(ssn, "ssn cannot be null");
        LOG.info("Reading active registered lotteries for ssn {}", ssn);
        final var participants = participantRepository.findAllBySsnAndLottery_State(ssn, LotteryState.ACTIVE);
        final var lotteries = participants
                .stream()
                .map(Participant::getLottery)
                .collect(Collectors.toList());
        LOG.debug("Found {} active registered lotteries for ssn {}", lotteries.size(), ssn);
        return lotteries;
    }
}
