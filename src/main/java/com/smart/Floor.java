package com.smart;

import java.util.ArrayList;
import java.util.List;

public class Floor {
    private final int floorNumber;
    private final List<Passenger> upQueue;
    private final List<Passenger> downQueue;

    public Floor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.upQueue = new ArrayList<>();
        this.downQueue = new ArrayList<>();
    }

    public void addPassenger(Passenger passenger) {
        if (passenger.getDestination() > floorNumber) {
            upQueue.add(passenger);
        } else if (passenger.getDestination() < floorNumber) {
            downQueue.add(passenger);
        }
    }

    public List<Passenger> getUpQueue() {
        return upQueue;
    }

    public List<Passenger> getDownQueue() {
        return downQueue;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public int getTotalWaitingPassengers() {
        return upQueue.size() + downQueue.size();
    }
}