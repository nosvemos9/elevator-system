package com.smart;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ElevatorSystem {
    List<Elevator> elevators;
    List<Floor> floors;

    // Bekleyen çağrılar (hedefte uygun asansör yoksa burada tutulur)
    public List<CallRequest> pendingCalls = new ArrayList<>();

    // Yeni: geçmiş çağrılar veri havuzu
    public List<CallLog> callLogs = new ArrayList<>();

    public ElevatorSystem(List<Elevator> elevators, List<Floor> floors) {
        this.elevators = elevators;
        this.floors = floors;
    }

    public void requestElevator(int floorNumber, boolean goingUp) {
        Floor floor = floors.get(floorNumber);
        if (goingUp) floor.setCallUp(true);
        else floor.setCallDown(true);

        // 🔴 Çağrı kaydı (veri havuzuna eklendi)
        callLogs.add(new CallLog(floorNumber, goingUp, LocalDateTime.now()));

        Elevator nearestElevator = findNearestAvailableElevator(floorNumber, goingUp);

        if (nearestElevator != null) {
            nearestElevator.addTargetFloor(floorNumber);
        } else {
            pendingCalls.add(new CallRequest(floorNumber, goingUp));
        }
    }

    private Elevator findNearestAvailableElevator(int floorNumber, boolean goingUp) {
        Elevator bestElevator = null;
        int bestDistance = Integer.MAX_VALUE;

        for (Elevator elevator : elevators) {
            int distance = Math.abs(elevator.currentFloor - floorNumber);

            if (elevator.state == ElevatorState.IDLE) {
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestElevator = elevator;
                }
            } else {
                boolean sameDirection = (goingUp && elevator.state == ElevatorState.MOVING_UP)
                        || (!goingUp && elevator.state == ElevatorState.MOVING_DOWN);
                boolean onTheWay = (goingUp && elevator.currentFloor < floorNumber)
                        || (!goingUp && elevator.currentFloor > floorNumber);

                if (sameDirection && onTheWay && distance < bestDistance) {
                    bestDistance = distance;
                    bestElevator = elevator;
                }
            }
        }

        return bestElevator;
    }

    public void step() {
        for (Elevator elevator : elevators) {
            elevator.moveOneStep();
        }

        List<CallRequest> served = new ArrayList<>();
        for (CallRequest req : pendingCalls) {
            Elevator e = findNearestAvailableElevator(req.floorNumber, req.goingUp);
            if (e != null) {
                e.addTargetFloor(req.floorNumber);
                served.add(req);
            }
        }
        pendingCalls.removeAll(served);
    }
}
