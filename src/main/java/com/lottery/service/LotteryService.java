package com.lottery.service;

import com.lottery.domain.entity.Lottery;

import java.util.List;

public interface LotteryService {

    /**
     * Creates a new lottery
     *
     * @param createLotteryRequest create lottery request
     * @return the id of the created lottery
     */
    Long create(CreateLotteryRequest createLotteryRequest);

    /**
     * Reads all lotteries
     *
     * @param page page number
     * @param size page size
     * @return list of lotteries
     */
    List<Lottery> readAll(Integer page, Integer size);

    /**
     * Finishes a lottery
     *
     * @param id lottery id
     */
    void finish(Long id);

    /**
     * Finds a lottery by id
     *
     * @param id lottery id
     * @return lottery
     */
    Lottery findById(Long id);
}
