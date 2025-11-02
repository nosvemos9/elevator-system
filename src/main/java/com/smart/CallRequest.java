package com.smart;

import java.time.Duration;
import java.time.LocalDateTime;

public class CallRequest {
    public int floorNumber;
    public boolean goingUp;
    private final LocalDateTime requestTime;
    private LocalDateTime servedTime;

    public CallRequest(int floorNumber, boolean goingUp) {
        this.floorNumber = floorNumber;
        this.goingUp = goingUp;
        this.requestTime = LocalDateTime.now();
        this.servedTime = null;
    }

    public void markServed() {
        this.servedTime = LocalDateTime.now();
    }

    public long getWaitSeconds() {
        if (servedTime == null) return -1;
        return Duration.between(requestTime, servedTime).getSeconds();
    }
    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    public LocalDateTime getServedTime() {
        return servedTime;
    }

}
