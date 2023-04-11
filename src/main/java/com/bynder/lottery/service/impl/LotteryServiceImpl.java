package com.bynder.lottery.service.impl;

import com.bynder.lottery.domain.LotteryState;
import com.bynder.lottery.domain.entity.Lottery;
import com.bynder.lottery.exception.NotFoundException;
import com.bynder.lottery.repository.LotteryRepository;
import com.bynder.lottery.service.CreateLotteryRequest;
import com.bynder.lottery.service.DateTimeService;
import com.bynder.lottery.service.LotteryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LotteryServiceImpl implements LotteryService {

    private static final Logger logger = LoggerFactory.getLogger(LotteryServiceImpl.class);

    private final LotteryRepository lotteryRepository;

    private final DateTimeService dateTimeService;

    public LotteryServiceImpl(LotteryRepository lotteryRepository, DateTimeService dateTimeService) {
        this.lotteryRepository = lotteryRepository;
        this.dateTimeService = dateTimeService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Long create(final CreateLotteryRequest createLotteryRequest) {
        Assert.notNull(createLotteryRequest.getName(), "createLotteryRequest.name cannot be null");
        Assert.notNull(createLotteryRequest.getAward(), "createLotteryRequest.award cannot be null");
        Assert.notNull(createLotteryRequest.getBallotUnit(), "createLotteryRequest.ballotUnit cannot be null");
        Assert.notNull(createLotteryRequest.getBallotUnit(), "createLotteryRequest.ballotUnit cannot be null");
        logger.info("Creating lottery with request: {}", createLotteryRequest);
        final Lottery lottery = new Lottery();
        lottery.setName(createLotteryRequest.getName());
        lottery.setAward(createLotteryRequest.getAward());
        lottery.setBallotPrice(createLotteryRequest.getBallotPrice());
        lottery.setBallotUnit(createLotteryRequest.getBallotUnit());
        lottery.setState(LotteryState.ACTIVE);
        lottery.setCreateDate(dateTimeService.currentTimestamp());
        Lottery insertedLottery = lotteryRepository.save(lottery);
        logger.debug("Lottery created with id: {}", insertedLottery.getId());
        return insertedLottery.getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Lottery> readAll(Integer page, Integer size) {
        Assert.notNull(page, "page cannot be null");
        Assert.notNull(size, "size cannot be null");
        logger.info("Reading all lotteries with page: {} and size: {}", page, size);
        final List<Lottery> lotteries = lotteryRepository.findAllByOrderByCreateDateDesc(PageRequest.of(page, size));
        logger.debug("Lotteries read: {}", lotteries);
        return lotteries;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void finish(final Long id) {
        logger.info("Finishing lottery with id: {}", id);
        final Optional<Lottery> optionalLottery = lotteryRepository.findById(id);
        if (optionalLottery.isEmpty()) throw new NotFoundException(String.format("Lottery with id: %s not found", id));
        final Lottery lottery = optionalLottery.get();
        lottery.setState(LotteryState.FINISHED);
        lotteryRepository.save(lottery);
        logger.debug("Lottery finished with id: {}", id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Lottery findById(final Long id) {
        logger.info("Finding lottery with id: {}", id);
        Lottery lottery = lotteryRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Lottery with id: %s not found", id)));
        logger.debug("Lottery found: {}", lottery);
        return lottery;
    }
}
