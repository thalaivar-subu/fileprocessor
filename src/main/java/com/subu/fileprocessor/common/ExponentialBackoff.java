package com.subu.fileprocessor.common;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExponentialBackoff {

    private final int initialDelay;
    private final int maxDelay;
    private final double backoffFactor;
    private int currentDelay;

    public ExponentialBackoff() {
        initialDelay = 100; // Initial delay in milliseconds
        maxDelay = 1600; // Maximum delay in milliseconds
        backoffFactor = 2.0; // Exponential backoff factor
        currentDelay = initialDelay;
    }

    public ExponentialBackoff(int initialDelay, int maxDelay, double backoffFactor) {
        this.initialDelay = initialDelay;
        this.maxDelay = maxDelay;
        this.backoffFactor = backoffFactor;
        this.currentDelay = initialDelay;
    }

    public void applyBackoff(boolean condition) throws InterruptedException {
        if (condition) {
            log.debug("Applying Backoff: {}", currentDelay);
            Thread.sleep(currentDelay);
            currentDelay = Math.min((int) (currentDelay * backoffFactor), maxDelay);
        } else {
            currentDelay = initialDelay; // Reset delay after a successful attempt
        }
    }
}
