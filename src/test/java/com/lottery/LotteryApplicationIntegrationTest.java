package com.lottery;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class LotteryApplicationIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void contextLoads() {
        assertTrue(postgresqlContainer.isRunning(), "PostgreSQL container should be running");
    }
}
