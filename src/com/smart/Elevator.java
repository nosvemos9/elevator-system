package com.smart;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ArrayList;
import java.util.List;

public class Elevator {
    int id;
    double velocity;
    double acceleration;
    int currentFloor;
    boolean movingUp;
    boolean movingDown;
    boolean busy;

    Queue<Integer> targetFloors;

    List<Integer> assignedFloorCalls;

    public Elevator(int id, double velocity, double acceleration, int currentFloor) {
        this.id = id;
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.currentFloor = currentFloor;
        this.movingUp = false;
        this.movingDown = false;
        this.busy = false;
        this.targetFloors = new LinkedList<>();
        this.assignedFloorCalls = new ArrayList<>();
    }

    public void addTargetFloor(int floor) {
        if (!targetFloors.contains(floor) && !assignedFloorCalls.contains(floor)) {
            boolean added = false;

            if (movingUp) {
                Queue<Integer> newQueue = new LinkedList<>();
                Queue<Integer> downCallQueue = new LinkedList<>();

                for (Integer existingFloor : targetFloors) {
                    if (!added && floor > currentFloor && floor < existingFloor) {
                        newQueue.offer(floor);
                        added = true;
                    }
                    newQueue.offer(existingFloor);
                }

                if (!added) {
                    newQueue.offer(floor);
                }

                targetFloors = newQueue;
            } else if (movingDown) {
                Queue<Integer> newQueue = new LinkedList<>();

                for (Integer existingFloor : targetFloors) {
                    if (!added && floor < currentFloor && floor > existingFloor) {
                        newQueue.offer(floor);
                        added = true;
                    }
                    newQueue.offer(existingFloor);
                }

                if (!added) {
                    newQueue.offer(floor);
                }

                targetFloors = newQueue;
            } else {
                targetFloors.offer(floor);
            }

            assignedFloorCalls.add(floor);
            busy = true;
            updateDirection();
        }
    }

    public void moveOneStep() {
        if (targetFloors.isEmpty()) {
            System.out.println("Elevator " + id + " is idle at floor " + currentFloor);
            busy = false;
            movingUp = false;
            movingDown = false;
            return;
        }

        int target = targetFloors.peek();
        if (currentFloor == target) {
            System.out.println("Elevator " + id + " arrived at floor " + currentFloor);
            targetFloors.poll();
            assignedFloorCalls.remove(Integer.valueOf(currentFloor));

            if (targetFloors.isEmpty()) {
                busy = false;
                movingUp = false;
                movingDown = false;
            } else {
                updateDirection();
            }
        } else if (currentFloor < target) {
            currentFloor++;
            System.out.println("Elevator " + id + " moving up to floor " + currentFloor);
            movingUp = true;
            movingDown = false;
        } else {
            currentFloor--;
            System.out.println("Elevator " + id + " moving down to floor " + currentFloor);
            movingUp = false;
            movingDown = true;
        }
    }

    private void updateDirection() {
        if (!targetFloors.isEmpty()) {
            int target = targetFloors.peek();
            if (target > currentFloor) {
                movingUp = true;
                movingDown = false;
            } else if (target < currentFloor) {
                movingDown = true;
                movingUp = false;
            } else {
                movingDown = false;
                movingUp = false;
            }
        }
    }

    public boolean canPickup(int floorNumber, boolean goingUp) {
        if (!busy) return true;

        if (movingUp) {
            if (goingUp && floorNumber > currentFloor &&
                    (targetFloors.isEmpty() || floorNumber < targetFloors.peek())) {
                return true;
            }
        }
        if (movingDown) {
            if (!goingUp && floorNumber < currentFloor &&
                    (targetFloors.isEmpty() || floorNumber > targetFloors.peek())) {
                return true;
            }
        }

        return false;
    }
}