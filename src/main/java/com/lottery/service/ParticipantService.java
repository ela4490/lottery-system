package com.lottery.service;

import com.lottery.domain.entity.Lottery;

import java.util.List;

public interface ParticipantService {

    /**
     * Registers a participant to a lottery
     *
     * @param registerParticipantRequest register participant request
     * @return the id of the registered participant
     */
    Long register(RegisterParticipantRequest registerParticipantRequest);

    /**
     * Submits a ballot to a lottery
     *
     * @param submissionRequest submission request
     * @return list of submitted ballots
     */
    List<String> submit(SubmissionRequest submissionRequest);

    /**
     * Reads all ballots of a lottery
     *
     * @param ssn participant ssn
     * @param lotteryId lottery id
     * @return list of ballots
     */
    List<String> readAllBallotsOfLottery(String ssn, Long lotteryId);

    /**
     * Reads all lotteries that a participant is registered to
     *
     * @param ssn participant ssn
     * @return list of lotteries
     */
    List<Lottery> readActiveRegisteredLotteries(String ssn);
}
