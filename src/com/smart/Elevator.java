package com.smart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Elevator {
    int id;
    double velocity;
    double acceleration;
    int currentFloor;
    int doorTimer = 0;
    final int DOOR_WAIT = 2;

    int weightLimit = 800;
    int currentWeight = 0;

    List<Integer> targetFloors;
    List<Integer> internalRequests;

    ElevatorState state = ElevatorState.IDLE;

    // Yolda mı, hedefe ulaştı mı bilgisi (Main'de loglama için kullanılır)
    public String travelStatus = "IDLE";

    public Elevator(int id, double velocity, double acceleration, int currentFloor) {
        this.id = id;
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.currentFloor = currentFloor;
        this.targetFloors = new ArrayList<>();
        this.internalRequests = new ArrayList<>();
    }

    public void addTargetFloor(int floor) {
        if (!targetFloors.contains(floor)) {
            targetFloors.add(floor);
            sortTargetFloors();
            if (state == ElevatorState.IDLE) updateState();
        }
    }

    public void selectDestinationFloor(int floor) {
        if (!internalRequests.contains(floor)) {
            internalRequests.add(floor);
            sortInternalRequests();
            if (state == ElevatorState.IDLE) updateState();
        }
    }

    public void addPassenger(int weight) {
        if (state == ElevatorState.DOOR_OPEN) {
            currentWeight += weight;
        }
    }

    public void moveOneStep() {
        switch (state) {
            case IDLE:
                travelStatus = "IDLE";
                if (!targetFloors.isEmpty()) updateState();
                break;

            case MOVING_UP:
                currentFloor++;
                travelStatus = "ON_ROUTE";
                checkArrival();
                break;

            case MOVING_DOWN:
                currentFloor--;
                travelStatus = "ON_ROUTE";
                checkArrival();
                break;

            case DOOR_OPEN:
                travelStatus = "ARRIVED";
                doorTimer--;
                if (doorTimer <= 0) {
                    if (currentWeight > weightLimit) {
                        doorTimer = 1;
                    } else {
                        state = ElevatorState.WAITING;
                    }
                }
                break;

            case WAITING:
                updateState();
                break;

            case EMERGENCY:
                travelStatus = "EMERGENCY";
                break;
        }
    }

    private void checkArrival() {
        if (!targetFloors.isEmpty() && targetFloors.get(0) == currentFloor) {
            targetFloors.remove(0);
            state = ElevatorState.DOOR_OPEN;
            doorTimer = DOOR_WAIT;
        }
    }

    private void updateState() {
        if (!targetFloors.isEmpty()) {
            int target = targetFloors.get(0);
            if (target > currentFloor) state = ElevatorState.MOVING_UP;
            else if (target < currentFloor) state = ElevatorState.MOVING_DOWN;
            else {
                state = ElevatorState.DOOR_OPEN;
                doorTimer = DOOR_WAIT;
            }
        } else if (!internalRequests.isEmpty()) {
            targetFloors.addAll(internalRequests);
            internalRequests.clear();
            sortTargetFloors();
            updateState();
        } else {
            state = ElevatorState.IDLE;
        }
    }

    private void sortTargetFloors() {
        Collections.sort(targetFloors);
    }

    private void sortInternalRequests() {
        Collections.sort(internalRequests);
    }
}
