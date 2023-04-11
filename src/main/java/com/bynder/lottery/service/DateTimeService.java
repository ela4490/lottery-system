package com.bynder.lottery.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public interface DateTimeService {
    Timestamp currentTimestamp();

    LocalDateTime now();


    Date convert(LocalDateTime ldt);

    Date convert(LocalDate ld);
}
