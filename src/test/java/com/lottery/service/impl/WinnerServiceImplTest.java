package com.lottery.service.impl;

import com.lottery.domain.BallotUnit;
import com.lottery.domain.LotteryState;
import com.lottery.domain.model.WinnerInfo;
import com.lottery.exception.InvalidDateException;
import com.lottery.exception.NoBallotSubmittedException;
import com.lottery.repository.BallotRepository;
import com.lottery.repository.WinnerRepository;
import com.lottery.service.DateTimeService;
import com.lottery.service.NumberService;
import com.lottery.domain.entity.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WinnerServiceImplTest {
    private final WinnerRepository winnerRepository = mock(WinnerRepository.class);
    private final BallotRepository ballotRepository = mock(BallotRepository.class);
    private final DateTimeService dateTimeService = mock(DateTimeService.class);
    private final NumberService numberService = mock(NumberService.class);
    private final Timestamp TEST_TIMESTAMP = Timestamp.from(Instant.now());

    @Test
    public void testReadWinnerByDateWhenNoResultsForDate() {
        // Arrange
        final var dateStr = TEST_TIMESTAMP.toLocalDateTime().toLocalDate().toString();
        final var date = Date.from(TEST_TIMESTAMP.toInstant());
        when(dateTimeService.convert(LocalDate.parse(dateStr))).thenReturn(date);

        // Act and Assert
        assertThrows(InvalidDateException.class, () -> winnerServicdeImpl().readWinnerByDate(dateStr));
    }

    @Test
    public void testReadWinnerByDate() {
        // Arrange
        final var dateStr = TEST_TIMESTAMP.toLocalDateTime().toLocalDate().toString();
        final var date = Date.from(TEST_TIMESTAMP.toInstant());
        when(dateTimeService.convert(LocalDate.parse(dateStr))).thenReturn(date);
        final var ballot = new Ballot(123L, generateSubmission(123L), "code");
        final var winner = new Winner(234L, ballot, date);
        when(winnerRepository.findByDate(date)).thenReturn(Optional.of(winner));

        // Act
        WinnerInfo winnerInfo = winnerServicdeImpl().readWinnerByDate(dateStr);

        // Assert
        assertEquals(ballot.getSubmission().getParticipant().getLottery().getAward(), winnerInfo.getAward());
        assertEquals(ballot.getSubmission().getParticipant().getLottery().getName(), winnerInfo.getLotteryName());
        assertEquals(ballot.getSubmission().getParticipant().getName(), winnerInfo.getParticipantName());
        assertEquals(ballot.getSubmission().getParticipant().getSsn(), winnerInfo.getParticipantSsn());
        assertEquals(ballot.getSubmission().getDate(), winnerInfo.getSubmissionDate());
        assertEquals(ballot.getCode(), winnerInfo.getBallotCode());
    }

    @Test
    public void testSelectWinnerWhenNoBalletsFound() {
        // Arrange
        when(dateTimeService.now()).thenReturn(TEST_TIMESTAMP.toLocalDateTime());
        final var startOfDay = mock(Date.class);
        final var endOfDay = mock(Date.class);
        when(dateTimeService.convert(TEST_TIMESTAMP.toLocalDateTime().toLocalDate().atStartOfDay()))
                .thenReturn(startOfDay);
        when(dateTimeService.convert(TEST_TIMESTAMP.toLocalDateTime().toLocalDate().atTime(LocalTime.MAX)))
                .thenReturn(endOfDay);
        when(ballotRepository.findRangeOfIds(startOfDay, endOfDay)).thenReturn(new Object[][]{});

        // Act and Assert
        assertThrows(NoBallotSubmittedException.class, () -> winnerServicdeImpl().selectWinner());
    }

    @Test
    public void testSelectWinner() {
        // Arrange
        when(dateTimeService.now()).thenReturn(TEST_TIMESTAMP.toLocalDateTime());
        final var startOfDay = mock(Date.class);
        final var endOfDay = mock(Date.class);
        when(dateTimeService.convert(TEST_TIMESTAMP.toLocalDateTime().toLocalDate().atStartOfDay()))
                .thenReturn(startOfDay);
        when(dateTimeService.convert(TEST_TIMESTAMP.toLocalDateTime().toLocalDate().atTime(LocalTime.MAX)))
                .thenReturn(endOfDay);
        when(ballotRepository.findRangeOfIds(startOfDay, endOfDay))
                .thenReturn(new Object[][]{
                        new Object[]{
                                BigInteger.valueOf(3L),
                                BigInteger.valueOf(5L)
                        }
                });
        when(numberService.randomBetween(3L, 5l)).thenReturn(4L);
        final var ballot = new Ballot(4L, generateSubmission(234L), "code");
        when(ballotRepository.findById(4L)).thenReturn(Optional.of(ballot));
        final var winnerArgumentCaptor = ArgumentCaptor.forClass(Winner.class);

        // Act
        winnerServicdeImpl().selectWinner();

        // Assert
        verify(winnerRepository).save(winnerArgumentCaptor.capture());
        assertEquals(ballot, winnerArgumentCaptor.getValue().getBallot());
    }

    private WinnerServiceImpl winnerServicdeImpl() {
        return new WinnerServiceImpl(winnerRepository, ballotRepository, dateTimeService, numberService);
    }

    private Lottery generateLottery() {
        Long LOTTERY_ID = 234L;
        return new Lottery(
                LOTTERY_ID,
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

    private Submission generateSubmission(Long id) {
        return new Submission(
                id,
                generateParticipant(123L),
                5,
                TEST_TIMESTAMP
        );
    }
}
