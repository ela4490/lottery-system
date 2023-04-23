package com.lottery.service.impl;

import com.lottery.domain.BallotUnit;
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
import com.lottery.service.DateTimeService;
import com.lottery.service.LotteryService;
import com.lottery.service.RegisterParticipantRequest;
import com.lottery.service.SubmissionRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ParticipantServiceImplTest {
    private final ParticipantRepository participantRepository = mock(ParticipantRepository.class);
    private final SubmissionRepository submissionRepository = mock(SubmissionRepository.class);
    private final BallotRepository ballotRepository = mock(BallotRepository.class);
    private final LotteryService lotteryService = mock(LotteryService.class);
    private final DateTimeService dateTimeService = mock(DateTimeService.class);
    private final Timestamp TEST_TIMESTAMP = Timestamp.from(Instant.now());

    @Test
    public void testRegisterWhenLotteryIsFinished() {
        // Arrange
        final var registerParticipantRequest = generateRegisterParticipantRequest();
        final var lottery = generateLottery();
        lottery.setState(LotteryState.FINISHED);
        when(lotteryService.findById(registerParticipantRequest.getLotteryId())).thenReturn(lottery);

        // Act and Assert
        assertThrows(
                FinishedLotteryException.class,
                () -> participantServiceImpl().register(registerParticipantRequest)
        );
    }

    @Test
    public void testRegisterWhenParticipantAlreadyExists() {
        // Arrange
        final var registerParticipantRequest = generateRegisterParticipantRequest();
        final var participant = generateParticipant(123L);
        when(lotteryService.findById(registerParticipantRequest.getLotteryId())).thenReturn(participant.getLottery());
        when(participantRepository.findBySsnAndLottery(registerParticipantRequest.getSsn(), participant.getLottery()))
                .thenReturn(Optional.of(participant));

        // Act
        final var participantId = participantServiceImpl().register(registerParticipantRequest);

        // Assert
        assertEquals(participant.getId(), participantId);
    }

    @Test
    public void testRegisterWhenParticipantDoesNotExists() {
        // Arrange
        final var registerParticipantRequest = generateRegisterParticipantRequest();
        final var participant = generateParticipant(123L);
        when(lotteryService.findById(registerParticipantRequest.getLotteryId())).thenReturn(participant.getLottery());
        when(participantRepository.findBySsnAndLottery(registerParticipantRequest.getSsn(), participant.getLottery()))
                .thenReturn(Optional.empty());
        Long participantId = 132L;
        final var participantResp = generateParticipant(participantId);
        when(dateTimeService.currentTimestamp()).thenReturn(TEST_TIMESTAMP);
        when(participantRepository.save(generateParticipant(null))).thenReturn(participantResp);

        // Act
        final var actualParticipantId = participantServiceImpl().register(registerParticipantRequest);

        // Assert
        assertEquals(participantResp.getId(), actualParticipantId);
    }

    @Test
    public void testSubmitWhenParticipantDoesNotExists() {
        // Arrange
        final var submissionRequest = generateSubmissionRequest();
        final var submission = generateSubmission(null);
        when(lotteryService.findById(submissionRequest.getLotteryId()))
                .thenReturn(submission.getParticipant().getLottery());
        when(participantRepository.findBySsnAndLottery(
                submissionRequest.getSsn(),
                submission.getParticipant().getLottery())
        ).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(
                NotFoundException.class,
                () -> participantServiceImpl().submit(submissionRequest)
        );
    }

    @Test
    public void testSubmitWhenLotteryIsFinished() {
        // Arrange
        final var submissionRequest = generateSubmissionRequest();
        final var submission = generateSubmission(null);
        submission.getParticipant().getLottery().setState(LotteryState.FINISHED);
        when(lotteryService.findById(submissionRequest.getLotteryId()))
                .thenReturn(submission.getParticipant().getLottery());
        when(participantRepository.findBySsnAndLottery(
                submissionRequest.getSsn(),
                submission.getParticipant().getLottery())
        ).thenReturn(Optional.of(submission.getParticipant()));

        // Act and Assert
        assertThrows(
                FinishedLotteryException.class,
                () -> participantServiceImpl().submit(generateSubmissionRequest())
        );
    }

    @Test
    public void testSubmit() {
        // Arrange
        final var submissionRequest = generateSubmissionRequest();
        final var submission = generateSubmission(null);
        when(lotteryService.findById(submissionRequest.getLotteryId()))
                .thenReturn(submission.getParticipant().getLottery());
        when(participantRepository.findBySsnAndLottery(
                submissionRequest.getSsn(),
                submission.getParticipant().getLottery())
        ).thenReturn(Optional.of(submission.getParticipant()));
        final var insertedSubmission = generateSubmission(234L);
        when(dateTimeService.currentTimestamp()).thenReturn(TEST_TIMESTAMP);
        when(submissionRepository.save(submission)).thenReturn(insertedSubmission);
        final var ballotArgumentCaptor = ArgumentCaptor.forClass(Ballot.class);

        // Act
        List<String> actualCodes = participantServiceImpl().submit(generateSubmissionRequest());

        // Assert
        assertEquals(submission.getNumberOfBallots(), actualCodes.size());
        verify(submissionRepository).save(submission);
        verify(ballotRepository, times(insertedSubmission.getNumberOfBallots())).save(ballotArgumentCaptor.capture());
        ballotArgumentCaptor.getAllValues()
                .forEach(
                        savedBallot -> {
                            assertTrue(actualCodes.contains(savedBallot.getCode()));
                            assertEquals(insertedSubmission, savedBallot.getSubmission());
                        });
    }

    @Test
    public void testReadAllBallotsOfLottery() {
        // Arrange
        var ballots = List.of(
                new Ballot(1L, generateSubmission(11L), "code1"),
                new Ballot(2L, generateSubmission(22L), "code2"),
                new Ballot(3L, generateSubmission(33L), "code3")
        );
        var ssn = "SSN";
        var lotteryId = 123L;
        when(ballotRepository.findAllBySubmission_Participant_SsnAndSubmission_Participant_Lottery_Id(ssn, lotteryId))
                .thenReturn(ballots);

        // Act
        final var codes = participantServiceImpl().readAllBallotsOfLottery(ssn, lotteryId);

        // Assert
        assertEquals(List.of("code1", "code2", "code3"), codes);
    }

    private ParticipantServiceImpl participantServiceImpl() {
        return new ParticipantServiceImpl(
                participantRepository,
                submissionRepository,
                ballotRepository,
                lotteryService,
                dateTimeService);
    }

    private Lottery generateLottery() {
        return new Lottery(
                234L,
                "lotteryName",
                TEST_TIMESTAMP,
                "lotteryAward",
                123L,
                BallotUnit.DOLLAR,
                LotteryState.ACTIVE
        );
    }

    private Participant generateParticipant(Long id) {
        return new Participant(
                id,
                "participantName",
                "participantSsn",
                generateLottery(),
                TEST_TIMESTAMP
        );
    }

    private RegisterParticipantRequest generateRegisterParticipantRequest() {
        return new RegisterParticipantRequest(
                "participantName",
                "participantSsn",
                generateLottery().getId()
        );
    }

    private Submission generateSubmission(Long id) {
        return new Submission(
                id,
                generateParticipant(123L),
                5,
                TEST_TIMESTAMP
        );
    }

    private SubmissionRequest generateSubmissionRequest() {
        Participant participant = generateParticipant(null);
        return new SubmissionRequest(
                participant.getLottery().getId(),
                5,
                participant.getSsn()

        );
    }
}
