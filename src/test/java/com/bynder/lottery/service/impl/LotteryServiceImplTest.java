package com.bynder.lottery.service.impl;

import com.bynder.lottery.domain.BallotUnit;
import com.bynder.lottery.domain.LotteryState;
import com.bynder.lottery.domain.entity.Lottery;
import com.bynder.lottery.exception.NotFoundException;
import com.bynder.lottery.repository.LotteryRepository;
import com.bynder.lottery.service.CreateLotteryRequest;
import com.bynder.lottery.service.DateTimeService;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LotteryServiceImplTest {
    private final LotteryRepository lotteryRepository = mock(LotteryRepository.class);
    private final DateTimeService dateTimeService = mock(DateTimeService.class);

    private final Timestamp TEST_TIMESTAMP = Timestamp.from(Instant.now());

    @Test
    public void testCreate() {
        Lottery lottery = generateLottery(null);
        Long expectedId = 456L;
        Lottery insertedLottery = generateLottery(expectedId);
        when(lotteryRepository.save(lottery)).thenReturn(insertedLottery);
        when(dateTimeService.currentTimestamp()).thenReturn(TEST_TIMESTAMP);

        Long actualId = lotteryServiceImpl().create(generateLotteryRequest());

        assertEquals(expectedId, actualId);
    }

    @Test
    public void testFinish() {
        Long lotteryId = 456L;
        Lottery lottery = generateLottery(lotteryId);
        when(lotteryRepository.findById(lotteryId)).thenReturn(Optional.of(lottery));

        lotteryServiceImpl().finish(lotteryId);

        assertEquals(LotteryState.FINISHED, lottery.getState());
    }

    @Test
    public void testFinishWhenLotteryDoesNotExists() {
        Long lotteryId = 456L;
        when(lotteryRepository.findById(lotteryId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> lotteryServiceImpl().finish(lotteryId));
    }

    private Lottery generateLottery(Long id) {
        return new Lottery(
                id,
                "lotteryName",
                TEST_TIMESTAMP,
                "lotteryAward",
                123L,
                BallotUnit.DOLLAR,
                LotteryState.ACTIVE
        );
    }

    private CreateLotteryRequest generateLotteryRequest() {
        return new CreateLotteryRequest(
                "lotteryName",
                "lotteryAward",
                123L,
                BallotUnit.DOLLAR
        );
    }

    private LotteryServiceImpl lotteryServiceImpl() {
        return new LotteryServiceImpl(lotteryRepository, dateTimeService);
    }
}
