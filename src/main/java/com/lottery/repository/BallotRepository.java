package com.lottery.repository;

import com.lottery.domain.entity.Ballot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface BallotRepository extends JpaRepository<Ballot, Long> {

    @Query(nativeQuery = true,
            value = """
                    select min(b.id), max(b.id) from ballot b
                    inner join submission s on s.id = b.submission_id
                    where s.date >= :start and s.date <= :end""")
    Object[][] findRangeOfIds(@Param("start") Date start, @Param("end") Date end);

    List<Ballot> findAllBySubmission_Participant_SsnAndSubmission_Participant_Lottery_Id(String ssn, Long lotteryId);
}
