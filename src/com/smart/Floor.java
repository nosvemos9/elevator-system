package com.smart;
public class Floor {
    int floorNumber;
    boolean callUp;
    boolean callDown;
    boolean callUpAssigned;
    boolean callDownAssigned;
    public Floor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.callUp = false;
        this.callDown = false;
        this.callUpAssigned = false;
        this.callDownAssigned = false;
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

    public boolean isCallUpAssigned() {
        return callUpAssigned;
    }

    public void setCallUpAssigned(boolean assigned) {
        this.callUpAssigned = assigned;
    }

    public boolean isCallDownAssigned() {
        return callDownAssigned;
    }

    public void setCallDownAssigned(boolean assigned) {
        this.callDownAssigned = assigned;
    }
}
