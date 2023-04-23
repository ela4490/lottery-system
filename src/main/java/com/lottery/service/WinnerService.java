package com.lottery.service;

import com.lottery.domain.model.WinnerInfo;

public interface WinnerService {

    /**
     * Reads winner info by date
     *
     * @param date date
     * @return winner info
     */
    WinnerInfo readWinnerByDate(String date);
}
