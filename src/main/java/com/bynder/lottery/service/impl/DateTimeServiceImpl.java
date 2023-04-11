package com.bynder.lottery.service.impl;

import com.bynder.lottery.service.DateTimeService;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class DateTimeServiceImpl implements DateTimeService {
    @Override
    public Timestamp currentTimestamp() {
        LocalDateTime ldt = LocalDateTime.now();
        return Timestamp.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }

    @Override
    public Date convert(LocalDateTime ldt) {
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }

    @Override
    public  Date convert(LocalDate ld) {
        return Date.from(ld.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }
}
