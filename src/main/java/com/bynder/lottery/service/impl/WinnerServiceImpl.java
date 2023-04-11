package com.bynder.lottery.service.impl;

import com.bynder.lottery.domain.entity.*;
import com.bynder.lottery.domain.model.WinnerInfo;
import com.bynder.lottery.exception.InvalidDateException;
import com.bynder.lottery.exception.NoBallotSubmittedException;
import com.bynder.lottery.repository.BallotRepository;
import com.bynder.lottery.repository.WinnerRepository;
import com.bynder.lottery.service.DateTimeService;
import com.bynder.lottery.service.NumberService;
import com.bynder.lottery.service.WinnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Optional;

@Service
@Transactional
public class WinnerServiceImpl implements WinnerService {

    private static final Logger logger = LoggerFactory.getLogger(WinnerServiceImpl.class);

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
        logger.info("Reading winner by date: {}", date);
        final Date d = dateTimeService.convert(LocalDate.parse(date));
        final Optional<Winner> optionalWinner = winnerRepository.findByDate(d);
        if (optionalWinner.isEmpty())
            throw new InvalidDateException("The entered date is not valid !!!");
        final Ballot ballot = optionalWinner.get().getBallot();
        final Submission submission = ballot.getSubmission();
        final Participant participant = submission.getParticipant();
        final Lottery lottery = participant.getLottery();

        final WinnerInfo winnerModel = new WinnerInfo();
        winnerModel.setAward(lottery.getAward());
        winnerModel.setLotteryName(lottery.getName());
        winnerModel.setParticipantName(participant.getName());
        winnerModel.setParticipantSsn(participant.getSsn());
        winnerModel.setBallotCode(ballot.getCode());
        winnerModel.setSubmissionDate(submission.getDate());
        logger.debug("Winner info: {}", winnerModel);
        return winnerModel;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void selectWinner() {
        LocalDateTime dt = dateTimeService.now();
        if (dt.getHour() == 0) dt = dt.minusDays(1);
        Date startDate = dateTimeService.convert(dt.toLocalDate().atStartOfDay());
        Date endDate = dateTimeService.convert(dt.toLocalDate().atTime(LocalTime.MAX));
        Object[][] range = ballotRepository.findRangeOfIds(startDate, endDate);
        if (range != null && range.length == 1) {
            long min = ((BigInteger) range[0][0]).longValue();
            long max = ((BigInteger) range[0][1]).longValue();
            long id = numberService.randomBetween(min, max);
            Optional<Ballot> optionalBallot = ballotRepository.findById(id);
            if (optionalBallot.isEmpty()) throw new RuntimeException("Something Went Wrong !!!");
            winnerRepository.save(Winner.build(optionalBallot.get(), startDate));
        } else throw new NoBallotSubmittedException("Not any ballot submitted at specified date !!!");
    }

}
