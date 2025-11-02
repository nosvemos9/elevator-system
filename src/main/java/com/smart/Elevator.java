package com.smart;

import java.util.*;

/**
 * Asansör sınıfı - durum yönetimi, hareket, kapı kontrolü ve yolcu taşıma.
 */
public class Elevator {
    // Constants
    private static final int DOOR_OPEN_DURATION_TICKS = 3;

    private final int id;
    private int currentFloor;
    private ElevatorState state;
    private final int capacity;
    private final int weightLimit;
    private final Set<Integer> internalRequests;
    private final List<Integer> targetFloors;
    private int doorTimer;
    private boolean overloadDetected;

    private final List<Passenger> passengers = new ArrayList<>();
    private List<Floor> floorsRef;

    public Elevator(int id, int capacity, int weightLimit) {
        this.id = id;
        this.currentFloor = 0;
        this.state = ElevatorState.IDLE;
        this.capacity = capacity;
        this.weightLimit = weightLimit;
        this.internalRequests = new HashSet<>();
        this.targetFloors = new ArrayList<>();
        this.doorTimer = 0;
        this.overloadDetected = false;
    }

    // ========== Public Setters/Getters ==========

    public void setCurrentFloor(int floor) {
        this.currentFloor = floor;
    }

    public List<Passenger> getPassengers() {
        return new ArrayList<>(passengers);
    }

    public List<Integer> getTargetFloors() {
        return new ArrayList<>(targetFloors);
    }

    public int getCurrentLoad() {
        return passengers.stream()
                .mapToInt(Passenger::getWeight)
                .sum();
    }

    public Direction getDirection() {
        return switch (state) {
            case MOVING_UP -> Direction.UP;
            case MOVING_DOWN -> Direction.DOWN;
            default -> Direction.IDLE;
        };
    }

    public void removeTarget(int floor) {
        targetFloors.remove((Integer) floor);
    }

    public void unloadPassenger(Passenger p) {
        passengers.remove(p);
    }

    public void attachFloors(List<Floor> floors) {
        this.floorsRef = floors;
    }

    public void addInternalRequest(int floor) {
        if (floor >= 0 && internalRequests.add(floor)) {
            addTargetFloor(floor);
        }
    }

    public void addTargetFloor(int floor) {
        if (!targetFloors.contains(floor)) {
            targetFloors.add(floor);
            sortTargetFloors();
        }
    }

    public String getTravelStatus() {
        return switch (state) {
            case DOOR_OPEN -> "ARRIVED";
            case MOVING_UP, MOVING_DOWN -> "ON_ROUTE";
            case EMERGENCY -> "EMERGENCY";
            default -> "IDLE";
        };
    }

    // ========== State Machine ==========

    public void update() {
        switch (state) {
            case IDLE -> handleIdle();
            case MOVING_UP -> handleMovingUp();
            case MOVING_DOWN -> handleMovingDown();
            case DOOR_OPEN -> handleDoorOpen();
            case WAITING -> handleWaiting();
            case EMERGENCY -> handleEmergency();
        }
    }

    private void handleIdle() {
        if (!targetFloors.isEmpty()) {
            int nextFloor = targetFloors.get(0);
            if (nextFloor > currentFloor) {
                state = ElevatorState.MOVING_UP;
            } else if (nextFloor < currentFloor) {
                state = ElevatorState.MOVING_DOWN;
            } else {
                state = ElevatorState.DOOR_OPEN;
                doorTimer = DOOR_OPEN_DURATION_TICKS;
            }
        }
    }

    private void handleMovingUp() {
        checkEnRoutePickup();

        if (!targetFloors.isEmpty() && currentFloor < targetFloors.get(0)) {
            currentFloor++;
        }

        if (!targetFloors.isEmpty() && currentFloor == targetFloors.get(0)) {
            targetFloors.remove(0);
            internalRequests.remove(currentFloor);
            state = ElevatorState.DOOR_OPEN;
            doorTimer = DOOR_OPEN_DURATION_TICKS;
        } else if (targetFloors.isEmpty()) {
            state = ElevatorState.IDLE;
        }
    }

    private void handleMovingDown() {
        checkEnRoutePickup();

        if (!targetFloors.isEmpty() && currentFloor > targetFloors.get(0)) {
            currentFloor--;
        }

        if (!targetFloors.isEmpty() && currentFloor == targetFloors.get(0)) {
            targetFloors.remove(0);
            internalRequests.remove(currentFloor);
            state = ElevatorState.DOOR_OPEN;
            doorTimer = DOOR_OPEN_DURATION_TICKS;
        } else if (targetFloors.isEmpty()) {
            state = ElevatorState.IDLE;
        }
    }

    private void handleDoorOpen() {
        if (targetFloors.contains(currentFloor)) {
            targetFloors.remove((Integer) currentFloor);
        }

        // Overload kontrolü
        if (getCurrentLoad() > weightLimit) {
            overloadDetected = true;
            doorTimer = DOOR_OPEN_DURATION_TICKS;
            System.out.println("[ELEVATOR " + id + "] OVERLOAD DETECTED! Weight: " + getCurrentLoad() + " kg");
            return;
        }

        overloadDetected = false;

        // 1) ÖNCE İNDİR
        List<Passenger> toUnload = new ArrayList<>();
        for (Passenger p : passengers) {
            if (p.getDestination() == currentFloor) {
                toUnload.add(p);
            }
        }
        for (Passenger p : toUnload) {
            passengers.remove(p);
            internalRequests.remove(currentFloor);
        }

        // 2) SONRA BİNDİR (iyileştirilmiş mantık)
        Floor f = (floorsRef != null && currentFloor >= 0 && currentFloor < floorsRef.size())
                ? floorsRef.get(currentFloor)
                : null;

        if (f != null) {
            List<Passenger> queue = selectQueue(f);
            if (queue != null && !queue.isEmpty()) {
                Iterator<Passenger> it = queue.iterator();
                while (it.hasNext() && passengers.size() < capacity) {
                    Passenger p = it.next();
                    if (getCurrentLoad() + p.getWeight() <= weightLimit) {
                        passengers.add(p);
                        it.remove();
                        int dest = p.getDestination();
                        if (internalRequests.add(dest)) {
                            addTargetFloor(dest);
                        }
                    } else {
                        break; // Ağırlık limiti aşıldı
                    }
                }
            }
        }

        // 3) Kapı zamanlayıcı
        if (doorTimer > 0) {
            doorTimer--;
            if (doorTimer == 0) {
                Integer nextFloor = firstTargetAfterCurrent();
                if (nextFloor != null) {
                    if (nextFloor > currentFloor) {
                        state = ElevatorState.MOVING_UP;
                    } else {
                        state = ElevatorState.MOVING_DOWN;
                    }
                } else {
                    state = ElevatorState.IDLE;
                }
            }
        }
    }

    private Integer firstTargetAfterCurrent() {
        for (Integer t : targetFloors) {
            if (t != currentFloor) {
                return t;
            }
        }
        return null;
    }

    private List<Passenger> selectQueue(Floor f) {
        if (state == ElevatorState.MOVING_UP) {
            return f.getUpQueue();
        } else if (state == ElevatorState.MOVING_DOWN) {
            return f.getDownQueue();
        } else {
            if (!targetFloors.isEmpty()) {
                int nextTarget = targetFloors.get(0);
                if (nextTarget > currentFloor) {
                    return f.getUpQueue();
                } else if (nextTarget < currentFloor) {
                    return f.getDownQueue();
                }
            }
            return f.getUpQueue();
        }
    }

    private void handleWaiting() {
        state = ElevatorState.IDLE;
    }

    private void handleEmergency() {
        // Acil durum - tüm hedefler iptal
    }

    private void checkEnRoutePickup() {
        if (floorsRef == null || targetFloors.isEmpty()) return;

        int firstTarget = targetFloors.get(0);

        if (state == ElevatorState.MOVING_UP) {
            for (int someFloor = currentFloor + 1; someFloor <= firstTarget; someFloor++) {
                if (someFloor >= 0 && someFloor < floorsRef.size()) {
                    Floor f = floorsRef.get(someFloor);
                    if (f != null && !f.getUpQueue().isEmpty()) {
                        if (!targetFloors.contains(someFloor)) {
                            targetFloors.add(someFloor);
                            sortTargetFloors();
                        }
                    }
                }
            }
        } else if (state == ElevatorState.MOVING_DOWN) {
            for (int someFloor = currentFloor - 1; someFloor >= firstTarget; someFloor--) {
                if (someFloor >= 0 && someFloor < floorsRef.size()) {
                    Floor f = floorsRef.get(someFloor);
                    if (f != null && !f.getDownQueue().isEmpty()) {
                        if (!targetFloors.contains(someFloor)) {
                            targetFloors.add(someFloor);
                            sortTargetFloors();
                        }
                    }
                }
            }
        }
    }

    private void sortTargetFloors() {
        if (state == ElevatorState.MOVING_UP) {
            Collections.sort(targetFloors);
        } else if (state == ElevatorState.MOVING_DOWN) {
            targetFloors.sort(Collections.reverseOrder());
        }
    }

    // ========== Emergency ==========

    public void triggerEmergency() {
        state = ElevatorState.EMERGENCY;
        targetFloors.clear();
        internalRequests.clear();
    }

    public void resolveEmergency() {
        if (state == ElevatorState.EMERGENCY) {
            state = ElevatorState.IDLE;
        }
    }

    // ========== Getters ==========

    public int getId() {
        return id;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public ElevatorState getState() {
        return state;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getWeightLimit() {
        return weightLimit;
    }

    public Set<Integer> getInternalRequests() {
        return new HashSet<>(internalRequests);
    }

    public boolean isOverloaded() {
        return overloadDetected;
    }
}