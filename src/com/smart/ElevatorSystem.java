package com.smart;
import java.util.List;
public class ElevatorSystem {
    List<Elevator> elevators;
    List<Floor> floors;

    public ElevatorSystem(List<Elevator> elevators, List<Floor> floors) {
        this.elevators = elevators;
        this.floors = floors;
    }

    // Request elevator for a floor, specifying direction: true=up, false=down
    public void requestElevator(int floorNumber, boolean goingUp) {
        Floor floor = floors.get(floorNumber);
        if (goingUp) floor.setCallUp(true);
        else floor.setCallDown(true);

        Elevator nearestElevator = findNearestAvailableElevator(floorNumber, goingUp);
        if (nearestElevator != null) {
            nearestElevator.addTargetFloor(floorNumber);
            System.out.println("Elevator " + nearestElevator.id + " assigned to floor " + floorNumber);
        } else {
            System.out.println("No available elevators for floor " + floorNumber + " at the moment.");
        }
    }

    private Elevator findNearestAvailableElevator(int floorNumber, boolean goingUp) {
        Elevator bestElevator = null;
        int bestDistance = Integer.MAX_VALUE;

        for (Elevator elevator : elevators) {
            if (elevator.busy) continue; // Skip busy elevators for now

            int distance = Math.abs(elevator.currentFloor - floorNumber);
            if (distance < bestDistance) {
                bestDistance = distance;
                bestElevator = elevator;
            }
        }
        return bestElevator;
    }

    public void step() {
        for (Elevator elevator : elevators) {
            elevator.moveOneStep();
        }
    }
}
