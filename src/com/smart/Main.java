package com.smart;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
public class Main {
    public static void main(String[] args) throws InterruptedException {
        List<Elevator> elevators = Arrays.asList(
                new Elevator(1, 2.5, 4.5, 0),
                new Elevator(2, 2.5, 4.5, 0)
        );

        List<Floor> floors = new ArrayList<>();
        for (int i = 0; i <= 33; i++) {
            floors.add(new Floor(i));
        }

        ElevatorSystem system = new ElevatorSystem(elevators, floors);

        // Sample requests
        system.requestElevator(3, true);
        system.requestElevator(10, false);

        // Simulate system steps (moving elevators)
        int step = 0;
        while (true) {
            System.out.println("\nStep " + (++step));
            system.step();

            boolean allIdle = elevators.stream().allMatch(e -> !e.busy);
            if (allIdle) {
                System.out.println("All elevators are idle. Simulation complete.");
                break;
            }

            Thread.sleep(500); // readability delay
        }
    }
}
