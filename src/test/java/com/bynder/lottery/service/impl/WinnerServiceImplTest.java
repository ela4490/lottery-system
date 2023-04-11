package com.bynder.lottery.service.impl;

import com.bynder.lottery.domain.BallotUnit;
import com.bynder.lottery.domain.LotteryState;
import com.bynder.lottery.domain.entity.*;
import com.bynder.lottery.domain.model.WinnerInfo;
import com.bynder.lottery.exception.InvalidDateException;
import com.bynder.lottery.exception.NoBallotSubmittedException;
import com.bynder.lottery.repository.BallotRepository;
import com.bynder.lottery.repository.WinnerRepository;
import com.bynder.lottery.service.DateTimeService;
import com.bynder.lottery.service.NumberService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

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
        String dateStr = TEST_TIMESTAMP.toLocalDateTime().toLocalDate().toString();
        Date date = Date.from(TEST_TIMESTAMP.toInstant());
        when(dateTimeService.convert(LocalDate.parse(dateStr))).thenReturn(date);

        assertThrows(InvalidDateException.class, () -> winnerServicdeImpl().readWinnerByDate(dateStr));
    }

    @Test
    public void testReadWinnerByDate() {
        String dateStr = TEST_TIMESTAMP.toLocalDateTime().toLocalDate().toString();
        Date date = Date.from(TEST_TIMESTAMP.toInstant());
        when(dateTimeService.convert(LocalDate.parse(dateStr))).thenReturn(date);
        Ballot ballot = new Ballot(123L, generateSubmission(123L), "code");
        Winner winner = new Winner(234L, ballot, date);
        when(winnerRepository.findByDate(date)).thenReturn(Optional.of(winner));

        WinnerInfo winnerInfo = winnerServicdeImpl().readWinnerByDate(dateStr);

        assertEquals(ballot.getSubmission().getParticipant().getLottery().getAward(), winnerInfo.getAward());
        assertEquals(ballot.getSubmission().getParticipant().getLottery().getName(), winnerInfo.getLotteryName());
        assertEquals(ballot.getSubmission().getParticipant().getName(), winnerInfo.getParticipantName());
        assertEquals(ballot.getSubmission().getParticipant().getSsn(), winnerInfo.getParticipantSsn());
        assertEquals(ballot.getSubmission().getDate(), winnerInfo.getSubmissionDate());
        assertEquals(ballot.getCode(), winnerInfo.getBallotCode());
    }

    @Test
    public void testSelectWinnerWhenNoBalletsFound() {
        when(dateTimeService.now()).thenReturn(TEST_TIMESTAMP.toLocalDateTime());
        Date startOfDay = mock(Date.class);
        Date endOfDay = mock(Date.class);
        when(dateTimeService.convert(TEST_TIMESTAMP.toLocalDateTime().toLocalDate().atStartOfDay()))
                .thenReturn(startOfDay);
        when(dateTimeService.convert(TEST_TIMESTAMP.toLocalDateTime().toLocalDate().atTime(LocalTime.MAX)))
                .thenReturn(endOfDay);
        when(ballotRepository.findRangeOfIds(startOfDay, endOfDay)).thenReturn(new Object[][]{});

        assertThrows(NoBallotSubmittedException.class, () -> winnerServicdeImpl().selectWinner());
    }

    @Test
    public void testSelectWinner() {
        when(dateTimeService.now()).thenReturn(TEST_TIMESTAMP.toLocalDateTime());
        Date startOfDay = mock(Date.class);
        Date endOfDay = mock(Date.class);
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
        Ballot ballot = new Ballot(4L, generateSubmission(234L), "code");
        when(ballotRepository.findById(4L)).thenReturn(Optional.of(ballot));
        ArgumentCaptor<Winner> winnerArgumentCaptor = ArgumentCaptor.forClass(Winner.class);

        winnerServicdeImpl().selectWinner();

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
