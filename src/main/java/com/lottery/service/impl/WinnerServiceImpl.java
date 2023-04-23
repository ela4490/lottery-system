package com.lottery.service.impl;

import com.lottery.domain.entity.*;
import com.lottery.domain.model.WinnerInfo;
import com.lottery.exception.InvalidDateException;
import com.lottery.exception.NoBallotSubmittedException;
import com.lottery.repository.BallotRepository;
import com.lottery.repository.WinnerRepository;
import com.lottery.service.DateTimeService;
import com.lottery.service.NumberService;
import com.lottery.service.WinnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;

@Service
@Transactional
public class WinnerServiceImpl implements WinnerService {

    private static final Logger LOG = LoggerFactory.getLogger(WinnerServiceImpl.class);
    private final WinnerRepository winnerRepository;
    private final BallotRepository ballotRepository;
    private final DateTimeService dateTimeService;
    private final NumberService numberService;

    public WinnerServiceImpl(WinnerRepository winnerRepository,
                             BallotRepository ballotRepository,
                             DateTimeService dateTimeService,
                             NumberService numberService) {
        this.winnerRepository = winnerRepository;
        this.ballotRepository = ballotRepository;
        this.dateTimeService = dateTimeService;
        this.numberService = numberService;
    }

    @Override
    public WinnerInfo readWinnerByDate(final String date) {
        Assert.notNull(date, "date cannot be null");
        LOG.info("Reading winner by date: {}", date);
        final var d = dateTimeService.convert(LocalDate.parse(date));
        final var optionalWinner = winnerRepository.findByDate(d);
        if (optionalWinner.isEmpty())
            throw new InvalidDateException("The entered date is not valid !!!");
        final var ballot = optionalWinner.get().getBallot();
        final var submission = ballot.getSubmission();
        final var participant = submission.getParticipant();
        final var lottery = participant.getLottery();

        final var winnerModel = new WinnerInfo();
        winnerModel.setAward(lottery.getAward());
        winnerModel.setLotteryName(lottery.getName());
        winnerModel.setParticipantName(participant.getName());
        winnerModel.setParticipantSsn(participant.getSsn());
        winnerModel.setBallotCode(ballot.getCode());
        winnerModel.setSubmissionDate(submission.getDate());
        LOG.debug("Winner info: {}", winnerModel);
        return winnerModel;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void selectWinner() {
        var dt = dateTimeService.now();
        if (dt.getHour() == 0) dt = dt.minusDays(1);
        final var startDate = dateTimeService.convert(dt.toLocalDate().atStartOfDay());
        final var endDate = dateTimeService.convert(dt.toLocalDate().atTime(LocalTime.MAX));
        final var range = ballotRepository.findRangeOfIds(startDate, endDate);
        if (range != null && range.length == 1) {
            final var min = ((BigInteger) range[0][0]).longValue();
            final var max = ((BigInteger) range[0][1]).longValue();
            final var id = numberService.randomBetween(min, max);
            final var optionalBallot = ballotRepository.findById(id);
            if (optionalBallot.isEmpty()) throw new RuntimeException("Something Went Wrong !!!");
            winnerRepository.save(Winner.build(optionalBallot.get(), startDate));
        } else throw new NoBallotSubmittedException("Not any ballot submitted at specified date !!!");
    }

}
