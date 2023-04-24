package com.lottery.service.impl;

import com.lottery.controller.dto.CreateLotteryRequestDto;
import com.lottery.domain.BallotUnit;
import com.lottery.domain.LotteryState;
import com.lottery.domain.entity.Lottery;
import com.lottery.exception.NotFoundException;
import com.lottery.repository.LotteryRepository;
import com.lottery.service.DateTimeService;
import org.junit.jupiter.api.Assertions;
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
        // Arrange
        final var lottery = generateLottery(null);
        Long expectedId = 456L;
        final var insertedLottery = generateLottery(expectedId);
        when(lotteryRepository.save(lottery)).thenReturn(insertedLottery);
        when(dateTimeService.currentTimestamp()).thenReturn(TEST_TIMESTAMP);

        // Act
        Long actualId = lotteryServiceImpl().create(generateLotteryRequest());

        //Assert
        assertEquals(expectedId, actualId);
    }

    @Test
    public void testFinish() {
        // Arrange
        Long lotteryId = 456L;
        final var lottery = generateLottery(lotteryId);
        when(lotteryRepository.findById(lotteryId)).thenReturn(Optional.of(lottery));

        // Act
        lotteryServiceImpl().finish(lotteryId);

        // Assert
        Assertions.assertEquals(LotteryState.FINISHED, lottery.getState());
    }

    @Test
    public void testFinishWhenLotteryDoesNotExists() {
        // Arrange
        Long lotteryId = 456L;
        when(lotteryRepository.findById(lotteryId)).thenReturn(Optional.empty());

        // Act and Assert
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

    private CreateLotteryRequestDto generateLotteryRequest() {
        return new CreateLotteryRequestDto(
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
