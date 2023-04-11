package com.bynder.lottery.repository;

import com.bynder.lottery.domain.entity.Winner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface WinnerRepository extends JpaRepository<Winner, Long> {

    Optional<Winner> findByDate(Date date);
}
