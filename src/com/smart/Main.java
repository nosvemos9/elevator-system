package com.smart;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalTime;


public class Main {
    public static void main(String[] args) throws InterruptedException {
        // Asansörleri oluştur
        List<Elevator> elevators = Arrays.asList(
                new Elevator(1, 2.5, 4.5, 0),
                new Elevator(2, 2.5, 4.5, 8)
        );

        // Katları oluştur
        List<Floor> floors = new ArrayList<>();
        for (int i = 0; i <= 10; i++) {
            floors.add(new Floor(i));
        }

        // Sistem başlat
        ElevatorSystem system = new ElevatorSystem(elevators, floors);

        // Başlangıç çağrıları
        system.requestElevator(2, true);       // 2. kattan yukarı çağrı
        system.requestElevator(7, false);      // 7. kattan aşağı çağrı
        elevators.get(0).selectDestinationFloor(9);  // Asansör 1 iç çağrısı
        elevators.get(1).selectDestinationFloor(1);  // Asansör 2 iç çağrısı

        // Simülasyon başlat
        int step = 0;
        while (true) {
            System.out.println("\n=== STEP " + step + " ===");

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

            if (allIdle && noPending && step > 15) {
                System.out.println("\nSimulation complete.");
                break;
            }

            step++;
            Thread.sleep(1000); // Adımlar arasında gecikme
        }
    }
}
