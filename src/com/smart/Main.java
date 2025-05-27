package com.smart;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        List<Elevator> elevators = Arrays.asList(
                new Elevator(1, 2.5, 4.5, 0),
                new Elevator(2, 2.5, 4.5, 8)
        );

        List<Floor> floors = new ArrayList<>();
        for (int i = 0; i <= 10; i++) {
            floors.add(new Floor(i));
        }

        ElevatorSystem system = new ElevatorSystem(elevators, floors);

        // Örnek: 1 haftalık veri – her 20 dakikalık slot başında 3 çağrı (0. kat, yukarı yönlü)
        for (int hour = 0; hour < 24; hour++) {
            for (int slot = 0; slot < 3; slot++) {
                LocalDateTime baseTime = LocalDateTime.of(2025, 5, 27, hour, slot * 20);
                for (int j = 0; j < 3; j++) {
                    system.callLogs.add(new CallLog(0, true, baseTime.plusMinutes(j)));
                }
            }
        }

        // Başlangıç çağrıları ve iç kat seçimleri
        system.requestElevator(2, true);
        system.requestElevator(7, false);
        elevators.get(0).selectDestinationFloor(9);
        elevators.get(1).selectDestinationFloor(1);

        int step = 0;
        int slotStepInterval = 20;
        int stepLimit = 200;

        while (true) {
            System.out.println("\n=== STEP " + step + " ===");

            // SLOT başında yoğunluk analizi
            if (step % slotStepInterval == 0) {
                int simulatedHour = (step / slotStepInterval) / 3;
                int simulatedSlot = (step / slotStepInterval) % 3;
                int slotIndex = (simulatedHour * 3) + simulatedSlot;

                CallLogAnalyzer analyzer = new CallLogAnalyzer(system.callLogs);
                Map<Integer, Integer> busyFloors = analyzer.getBusyFloors(slotIndex, 2); // threshold = 2

                for (Integer floor : busyFloors.keySet()) {
                    boolean alreadyAssigned = elevators.stream()
                            .anyMatch(e -> e.targetFloors.contains(floor) || e.currentFloor == floor);
                    if (alreadyAssigned) continue;

                    system.sendIdleElevatorToFloor(floor);
                    System.out.println("[SYSTEM] Predicted traffic → sending elevator to Floor " + floor);
                }
            }

            for (Elevator e : elevators) {
                String target = e.targetFloors.isEmpty() ? "-" : String.valueOf(e.targetFloors.get(0));
                String door = (e.state == ElevatorState.DOOR_OPEN) ? "OPEN" : "CLOSED";
                String mode = (e.state == ElevatorState.IDLE) ? "IDLE" : "BUSY";

                System.out.println("Elevator " + e.id
                        + " | Floor: " + e.currentFloor
                        + " | State: " + e.state
                        + " | Target: " + target
                        + " | Weight: " + e.currentWeight + " kg"
                        + " | Mode: " + mode
                        + " | TravelStatues: " + e.travelStatus
                        + " | Time: " + LocalTime.now().withNano(0));
            }

            system.step();

            boolean allIdle = elevators.stream().allMatch(e -> e.state == ElevatorState.IDLE);
            boolean noPending = system.pendingCalls == null || system.pendingCalls.isEmpty();

            if ((allIdle && noPending && step > 80) || step > stepLimit) {
                System.out.println("\nSimulation complete.");
                break;
            }

            step++;
            Thread.sleep(100); // hızlandırılmış simülasyon
        }
    }
}
