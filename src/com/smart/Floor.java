package com.smart;

public class Floor {
    int floorNumber;
    boolean callUp;
    boolean callDown;

    public Floor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.callUp = false;
        this.callDown = false;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public boolean isCallUp() {
        return callUp;
    }

    public void setCallUp(boolean callUp) {
        this.callUp = callUp;
    }

    public boolean isCallDown() {
        return callDown;
    }

    public void setCallDown(boolean callDown) {
        this.callDown = callDown;
    }
}
