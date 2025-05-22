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

        System.out.println("===");
        system.requestElevator(10, true);

        system.requestElevator(5, true);
        system.requestElevator(7, true);

        runSimulation(system, elevators);

        elevators = Arrays.asList(
                new Elevator(1, 2.5, 4.5, 0),
                new Elevator(2, 2.5, 4.5, 0)
        );

        floors = new ArrayList<>();
        for (int i = 0; i <= 33; i++) {
            floors.add(new Floor(i));
        }

        system = new ElevatorSystem(elevators, floors);

        System.out.println("\n===");
        system.requestElevator(15, true);

        system.requestElevator(7, false);
        system.requestElevator(10, false);

        runSimulation(system, elevators);

        elevators = Arrays.asList(
                new Elevator(1, 2.5, 4.5, 20),
                new Elevator(2, 2.5, 4.5, 0)
        );

        floors = new ArrayList<>();
        for (int i = 0; i <= 33; i++) {
            floors.add(new Floor(i));
        }

        system = new ElevatorSystem(elevators, floors);

        System.out.println("\n=== ");
        // Asansör 1 aşağı yönlü hedef alıyor
        system.requestElevator(5, false);


        system.requestElevator(15, false);
        system.requestElevator(10, false);

        runSimulation(system, elevators);

        elevators = Arrays.asList(
                new Elevator(1, 2.5, 4.5, 20),
                new Elevator(2, 2.5, 4.5, 0)
        );

        floors = new ArrayList<>();
        for (int i = 0; i <= 33; i++) {
            floors.add(new Floor(i));
        }

        system = new ElevatorSystem(elevators, floors);

        System.out.println("\n===");
        system.requestElevator(5, false);

        system.requestElevator(15, true);
        system.requestElevator(10, true);

        runSimulation(system, elevators);
    }

    private static void runSimulation(ElevatorSystem system, List<Elevator> elevators) throws InterruptedException {
        int step = 0;
        int maxSteps = 50;

        while (step < maxSteps) {
            System.out.println("\nStep " + (++step));
            system.step();

            boolean allIdle = elevators.stream().allMatch(e -> !e.busy);
            if (allIdle) {
                System.out.println("All elevators are idle. Simulation complete.");
                break;
            }

            Thread.sleep(500); // readability delay
        }

        if (step >= maxSteps) {
            System.out.println("Maximum step count reached. Stopping simulation.");
        }
    }
}