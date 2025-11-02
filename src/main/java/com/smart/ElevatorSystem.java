package com.smart;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Asansör sisteminin merkezi kontrol sınıfı.
 * Çağrı atama, yolcu yönetimi ve sistem koordinasyonu.
 */
public class ElevatorSystem {
    private final List<Elevator> elevators;
    private final List<Floor> floors;
    public final List<CallLog> callLogs = Collections.synchronizedList(new ArrayList<>());
    private final Deque<CallRequest> pendingCalls = new ArrayDeque<>();

    public ElevatorSystem(List<Elevator> elevators, List<Floor> floors) {
        if (elevators == null || elevators.isEmpty()) {
            throw new IllegalArgumentException("Elevators list cannot be null or empty");
        }
        if (floors == null || floors.isEmpty()) {
            throw new IllegalArgumentException("Floors list cannot be null or empty");
        }

        this.elevators = elevators;
        this.floors = floors;

        // Asansörlere kat referanslarını enjekte et
        for (Elevator e : this.elevators) {
            try {
                e.attachFloors(this.floors);
            } catch (Exception ex) {
                System.err.println("[WARN] Could not attach floors to elevator " + e.getId() + ": " + ex.getMessage());
            }
        }
    }

    /**
     * Dış çağrı - belirli bir kattan belirli yöne asansör çağırma.
     */
    public void requestElevator(int floor, Direction direction) {
        if (floor < 0 || floor >= floors.size()) {
            System.err.println("[SYSTEM] Invalid floor: " + floor);
            return;
        }

        // CallLog kaydet
        boolean isUp = (direction == Direction.UP);
        callLogs.add(new CallLog(floor, isUp, LocalDateTime.now()));

        // Kuyruğa ekle
        CallRequest request = new CallRequest(floor, direction);
        pendingCalls.add(request);

        // En uygun asansörü bul
        Elevator best = findBestElevator(floor, direction);
        if (best != null && !best.getTargetFloors().contains(floor)) {
            best.addTargetFloor(floor);
            System.out.println("[SYSTEM] Assigned Elevator " + best.getId() + " to Floor " + floor + " (direction: " + direction + ")");
        }
    }

    /**
     * Yolcu bazlı çağrı - origin'den destination'a gidecek yolcu.
     */
    public void requestElevator(int origin, int destination, int weight) {
        if (origin < 0 || origin >= floors.size()) {
            System.err.println("[SYSTEM] Invalid origin floor: " + origin);
            return;
        }
        if (destination < 0 || destination >= floors.size()) {
            System.err.println("[SYSTEM] Invalid destination floor: " + destination);
            return;
        }
        if (weight <= 0) {
            System.err.println("[SYSTEM] Invalid weight: " + weight);
            return;
        }

        Direction direction = (destination > origin) ? Direction.UP : Direction.DOWN;

        // Yolcuyu ilgili katın kuyruğuna ekle
        Passenger passenger = new Passenger(origin, destination, weight);
        Floor originFloor = floors.get(origin);
        if (direction == Direction.UP) {
            originFloor.getUpQueue().add(passenger);
        } else {
            originFloor.getDownQueue().add(passenger);
        }

        System.out.println("[SYSTEM] Passenger added to Floor " + origin + " queue (direction: " + direction + ")");

        // Asansörü çağır
        requestElevator(origin, direction);
    }

    /**
     * IDLE bir asansörü belirli kata gönder (predictive logic için).
     */
    public void sendIdleElevatorToFloor(int floor) {
        if (floor < 0 || floor >= floors.size()) return;

        Elevator idleElevator = findClosestIdleElevator(floor);
        if (idleElevator != null && !idleElevator.getTargetFloors().contains(floor)) {
            idleElevator.addTargetFloor(floor);
            System.out.println("[SYSTEM] Sent IDLE Elevator " + idleElevator.getId() + " to Floor " + floor + " (predictive)");
        }
    }

    /**
     * Bir simülasyon adımı - bekleyen çağrıları işle ve tüm asansörleri güncelle.
     */
    public void step() {
        // Bekleyen çağrılardan birkaçını işle
        int processed = 0;
        while (!pendingCalls.isEmpty() && processed < 3) {
            CallRequest call = pendingCalls.poll();
            if (call != null) {
                Elevator best = findBestElevator(call.floor, call.direction);
                if (best != null && !best.getTargetFloors().contains(call.floor)) {
                    best.addTargetFloor(call.floor);
                }
            }
            processed++;
        }

        // Tüm asansörleri ilerlet
        for (Elevator e : elevators) {
            e.update();
        }
    }

    public boolean hasPendingCalls() {
        return !pendingCalls.isEmpty();
    }

    public List<Elevator> getElevators() {
        return new ArrayList<>(elevators);
    }

    public List<Floor> getFloors() {
        return new ArrayList<>(floors);
    }

    // ========== Atama Algoritmaları ==========

    /**
     * En iyi asansörü bul - önce IDLE olanları, sonra en yakını.
     */
    private Elevator findBestElevator(int floor, Direction direction) {
        Elevator idle = findClosestIdleElevator(floor);
        if (idle != null) {
            return idle;
        }
        return findClosestElevator(floor);
    }

    private Elevator findClosestIdleElevator(int floor) {
        Elevator best = null;
        int minDistance = Integer.MAX_VALUE;

        for (Elevator e : elevators) {
            if (e.getState() == ElevatorState.IDLE) {
                int distance = Math.abs(e.getCurrentFloor() - floor);
                if (distance < minDistance) {
                    minDistance = distance;
                    best = e;
                }
            }
        }
        return best;
    }

    private Elevator findClosestElevator(int floor) {
        Elevator best = null;
        int minDistance = Integer.MAX_VALUE;

        for (Elevator e : elevators) {
            if (e.getState() != ElevatorState.EMERGENCY) {
                int distance = Math.abs(e.getCurrentFloor() - floor);
                if (distance < minDistance) {
                    minDistance = distance;
                    best = e;
                }
            }
        }
        return best;
    }

    // ========== İç Sınıf: CallRequest ==========

    private static class CallRequest {
        final int floor;
        final Direction direction;

        CallRequest(int floor, Direction direction) {
            this.floor = floor;
            this.direction = direction;
        }
    }
}