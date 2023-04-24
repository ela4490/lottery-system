package com.lottery.service.impl;

import com.lottery.controller.dto.CreateLotteryRequestDto;
import com.lottery.domain.LotteryState;
import com.lottery.domain.entity.Lottery;
import com.lottery.exception.NotFoundException;
import com.lottery.repository.LotteryRepository;
import com.lottery.service.DateTimeService;
import com.lottery.service.LotteryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LotteryServiceImpl implements LotteryService {

    private static final Logger LOG = LoggerFactory.getLogger(LotteryServiceImpl.class);
    private final LotteryRepository lotteryRepository;
    private final DateTimeService dateTimeService;

    public LotteryServiceImpl(LotteryRepository lotteryRepository, DateTimeService dateTimeService) {
        this.lotteryRepository = lotteryRepository;
        this.dateTimeService = dateTimeService;
    }

    @Override
    @Transactional
    public Long create(final CreateLotteryRequestDto createLotteryRequest) {
        LOG.info("Creating lottery with request: {}", createLotteryRequest);
        final var lottery = new Lottery();
        lottery.setName(createLotteryRequest.getName());
        lottery.setAward(createLotteryRequest.getAward());
        lottery.setBallotPrice(createLotteryRequest.getBallotPrice());
        lottery.setBallotUnit(createLotteryRequest.getBallotUnit());
        lottery.setState(LotteryState.ACTIVE);
        lottery.setCreateDate(dateTimeService.currentTimestamp());
        final var insertedLottery = lotteryRepository.save(lottery);
        LOG.debug("Lottery created with id: {}", insertedLottery.getId());
        return insertedLottery.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lottery> readAll(Integer page, Integer size) {
        LOG.info("Reading all lotteries with page: {} and size: {}", page, size);
        final var lotteries = lotteryRepository.findAllByOrderByCreateDateDesc(PageRequest.of(page, size));
        LOG.debug("Lotteries read: {}", lotteries);
        return lotteries;
    }

    @Override
    @Transactional
    public void finish(final Long id) {
        LOG.info("Finishing lottery with id: {}", id);
        final var optionalLottery = lotteryRepository.findById(id);
        if (optionalLottery.isEmpty()) {
            throw new NotFoundException(String.format("Lottery with id: %s not found", id));
        }
        final var lottery = optionalLottery.get();
        lottery.setState(LotteryState.FINISHED);
        lotteryRepository.save(lottery);
        LOG.debug("Lottery finished with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Lottery findById(final Long id) {
        LOG.info("Finding lottery with id: {}", id);
        final var lottery = lotteryRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Lottery with id: %s not found", id)));
        LOG.debug("Lottery found: {}", lottery);
        return lottery;
    }
}
