package com.smart;
import java.util.List;
public class ElevatorSystem {
    List<Elevator> elevators;
    List<Floor> floors;

    public ElevatorSystem(List<Elevator> elevators, List<Floor> floors) {
        this.elevators = elevators;
        this.floors = floors;
    }

    public void requestElevator(int floorNumber, boolean goingUp) {
        Floor floor = floors.get(floorNumber);

        if (goingUp) floor.setCallUp(true);
        else floor.setCallDown(true);

        if ((goingUp && floor.isCallUpAssigned()) || (!goingUp && floor.isCallDownAssigned())) {
            System.out.println("Floor " + floorNumber + " call is already assigned to an elevator.");
            return;
        }

        Elevator bestElevator = findBestElevator(floorNumber, goingUp);
        if (bestElevator != null) {
            bestElevator.addTargetFloor(floorNumber);

            if (goingUp) floor.setCallUpAssigned(true);
            else floor.setCallDownAssigned(true);

            System.out.println("Elevator " + bestElevator.id + " assigned to floor " + floorNumber);
        } else {
            System.out.println("No suitable elevator found for floor " + floorNumber + " at the moment.");
        }
    }

    private Elevator findBestElevator(int floorNumber, boolean goingUp) {
        Elevator bestElevator = null;
        int bestScore = Integer.MAX_VALUE;

        for (Elevator elevator : elevators) {
            if (elevator.canPickup(floorNumber, goingUp)) {
                int score = calculateScore(elevator, floorNumber, goingUp);

                if (score < bestScore) {
                    bestScore = score;
                    bestElevator = elevator;
                }
            }
        }

        if (bestElevator == null) {
            for (Elevator elevator : elevators) {
                if (!elevator.busy) {
                    int distance = Math.abs(elevator.currentFloor - floorNumber);
                    if (distance < bestScore) {
                        bestScore = distance;
                        bestElevator = elevator;
                    }
                }
            }
        }

        return bestElevator;
    }

    private int calculateScore(Elevator elevator, int floorNumber, boolean goingUp) {
        int score = Math.abs(elevator.currentFloor - floorNumber); // Mesafe puanı

        if ((elevator.movingUp && goingUp) || (elevator.movingDown && !goingUp)) {
            score -= 10;
        } else {
            score += 10;
        }
        if (!elevator.targetFloors.isEmpty()) {
            score += elevator.targetFloors.size() * 5;
        }

        return score;
    }

    public void step() {
        for (Elevator elevator : elevators) {
            elevator.moveOneStep();

            for (Floor floor : floors) {
                if (floor.getFloorNumber() == elevator.currentFloor) {
                    if (elevator.movingUp || (!elevator.movingUp && !elevator.movingDown)) {
                        floor.setCallUp(false);
                        floor.setCallUpAssigned(false);
                    }
                    if (elevator.movingDown || (!elevator.movingUp && !elevator.movingDown)) {
                        floor.setCallDown(false);
                        floor.setCallDownAssigned(false);
                    }
                }
            }
        }
    }
}
