package com.lottery.repository;

import com.lottery.domain.LotteryState;
import com.lottery.domain.entity.Lottery;
import com.lottery.domain.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    Optional<Participant> findBySsnAndLottery(String ssn, Lottery lottery);

    List<Participant> findAllBySsnAndLottery_State(String ssn, LotteryState state);

}
