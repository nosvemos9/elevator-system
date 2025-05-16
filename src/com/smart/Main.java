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
        for (int i = 0; i < 20; i++) {
            System.out.println("Step " + (i + 1));
            system.step();
            Thread.sleep(500);  // Pause for readability
        }
    }
}
