package com.bynder.lottery.service.impl;

import com.bynder.lottery.domain.LotteryState;
import com.bynder.lottery.domain.entity.Ballot;
import com.bynder.lottery.domain.entity.Lottery;
import com.bynder.lottery.domain.entity.Participant;
import com.bynder.lottery.domain.entity.Submission;
import com.bynder.lottery.exception.FinishedLotteryException;
import com.bynder.lottery.exception.NotFoundException;
import com.bynder.lottery.repository.BallotRepository;
import com.bynder.lottery.repository.ParticipantRepository;
import com.bynder.lottery.repository.SubmissionRepository;
import com.bynder.lottery.service.*;
import com.bynder.lottery.util.BallotGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional
public class ParticipantServiceImpl implements ParticipantService {

    private static final Logger logger = LoggerFactory.getLogger(ParticipantServiceImpl.class);

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

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Long register(final RegisterParticipantRequest registerParticipantRequest) {
        Assert.notNull(registerParticipantRequest.getName(), "registerParticipantRequest.name cannot be null");
        Assert.notNull(registerParticipantRequest.getSsn(), "registerParticipantRequest.ssn cannot be null");
        Assert.notNull(registerParticipantRequest.getLotteryId(), "registerParticipantRequest.lotteryId cannot be null");
        logger.info("Registering participant {} to lottery {}", registerParticipantRequest.getSsn(), registerParticipantRequest.getLotteryId());
        final Lottery lottery = lotteryService.findById(registerParticipantRequest.getLotteryId());
        if (!lottery.getState().equals(LotteryState.ACTIVE)) {
            throw new FinishedLotteryException("Lottery is not active anymore !!!");
        }
        final Participant participant = participantRepository.findBySsnAndLottery(
                registerParticipantRequest.getSsn(),
                lottery
        ).orElseGet(() -> {
            final Participant saved = new Participant();
            saved.setName(registerParticipantRequest.getName());
            saved.setSsn(registerParticipantRequest.getSsn());
            saved.setLottery(lottery);
            saved.setRegistrationDate(dateTimeService.currentTimestamp());
            return participantRepository.save(saved);
        });
        logger.debug("Participant {} registered to lottery {}", participant.getId(), registerParticipantRequest.getLotteryId());
        return participant.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public List<String> submit(final SubmissionRequest submissionRequest) {
        Assert.notNull(submissionRequest.getLotteryId(), "submissionRequest.lotteryId cannot be null");
        Assert.notNull(submissionRequest.getNumberOfBallots(), "submissionRequest.numberOfBallots cannot be null");
        logger.info("Submitting {} ballots to lottery {}", submissionRequest.getNumberOfBallots(), submissionRequest.getLotteryId());
        final Lottery lottery = lotteryService.findById(submissionRequest.getLotteryId());
        final Optional<Participant> optionalParticipant = participantRepository.findBySsnAndLottery(submissionRequest.getSsn(), lottery);
        if (optionalParticipant.isEmpty()) throw new NotFoundException("Participant is not registered for lottery !!!");

        final Participant participant = optionalParticipant.get();

        if (LotteryState.FINISHED.equals(participant.getLottery().getState()))
            throw new FinishedLotteryException("Lottery is not active anymore !!!");

        final Submission submission = new Submission();
        submission.setNumberOfBallots(submissionRequest.getNumberOfBallots());
        submission.setDate(dateTimeService.currentTimestamp());
        submission.setParticipant(participant);
        final Submission insertedSubmission = submissionRepository.save(submission);

        final List<String> codes = new ArrayList<>();
        IntStream.rangeClosed(1, insertedSubmission.getNumberOfBallots()).forEach(prefix -> {
            final Ballot b = new Ballot();
            b.setCode(BallotGenerator.generate(prefix, insertedSubmission.getParticipant().getId().intValue()));
            b.setSubmission(insertedSubmission);
            ballotRepository.save(b);
            codes.add(b.getCode());
        });

        logger.debug("Submitted {} ballots to lottery {}", submissionRequest.getNumberOfBallots(), submissionRequest.getLotteryId());
        return codes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> readAllBallotsOfLottery(final String ssn, final Long lotteryId) {
        Assert.notNull(ssn, "ssn cannot be null");
        Assert.notNull(lotteryId, "lotteryId cannot be null");
        logger.info("Reading all ballots of lottery {} for ssn {}", lotteryId, ssn);
        final List<Ballot> ballots = ballotRepository.findAllBySubmission_Participant_SsnAndSubmission_Participant_Lottery_Id(ssn, lotteryId);
        final List<String> collected = ballots
                .stream()
                .map(Ballot::getCode)
                .collect(Collectors.toList());
        logger.debug("Found {} ballots of lottery {} for ssn {}", collected.size(), lotteryId, ssn);
        return collected;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Lottery> readActiveRegisteredLotteries(final String ssn) {
        Assert.notNull(ssn, "ssn cannot be null");
        logger.info("Reading active registered lotteries for ssn {}", ssn);
        final List<Participant> participants = participantRepository.findAllBySsnAndLottery_State(ssn, LotteryState.ACTIVE);
        final List<Lottery> lotteries = participants
                .stream()
                .map(Participant::getLottery)
                .collect(Collectors.toList());
        logger.debug("Found {} active registered lotteries for ssn {}", lotteries.size(), ssn);
        return lotteries;

    }
}
