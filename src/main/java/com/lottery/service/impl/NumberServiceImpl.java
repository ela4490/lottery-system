package com.lottery.service.impl;

import com.lottery.service.NumberService;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class NumberServiceImpl implements NumberService {

    @Override
    public long randomBetween(long minInclusive, long maxInclusive) {
        Random random = new Random();
        return random
            .longs(minInclusive, maxInclusive + 1)
            .findFirst()
            .getAsLong();
    }
}
