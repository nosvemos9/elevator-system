package com.smart;

import java.time.LocalDateTime;

// Her asansör çağrısını zaman, yön ve kat bilgisiyle kaydeden sınıf
public class CallLog {
    public int floor;
    public boolean goingUp;
    public LocalDateTime timestamp;

    public CallLog(int floor, boolean goingUp, LocalDateTime timestamp) {
        this.floor = floor;
        this.goingUp = goingUp;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "CallLog{" +
                "floor=" + floor +
                ", goingUp=" + goingUp +
                ", timestamp=" + timestamp +
                '}';
    }
}
