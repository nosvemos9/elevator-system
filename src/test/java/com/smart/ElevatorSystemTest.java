package com.smart;

import com.smart.sim.ConfigLoader;
import com.smart.sim.SimulationConfig;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ElevatorSystemTest {

    private ElevatorSystem bootstrapSystem() {
        SimulationConfig cfg = ConfigLoader.load();

        List<Floor> floors = new ArrayList<>();
        for (int i = 0; i < cfg.getFloors(); i++) floors.add(new Floor(i));

        List<Elevator> elevators = new ArrayList<>();
        for (int i = 0; i < cfg.getElevators(); i++) {
            int start = cfg.getInitialPositions().get(i);
            Elevator e = new Elevator(i + 1, 12, 800);
            e.setCurrentFloor(start);
            e.attachFloors(floors);
            elevators.add(e);
        }
        return new ElevatorSystem(elevators, floors);
    }

    private void runSteps(ElevatorSystem system, int steps) {
        for (int i = 0; i < steps; i++) system.step();
    }

    @Test
    void basicDispatch_movesClosestElevator() {
        ElevatorSystem system = bootstrapSystem();

        // zemin → 4. kata çağrı (yukarı)
        system.requestElevator(0, Direction.UP);

        // bir miktar adım at
        runSteps(system, 10);

        // en az bir asansör 0'a ulaşıp DOOR_OPEN'a geçmiş olmalı ya da oraya ilerliyor olmalı
        boolean someoneAtOrMovingTo0 = system.getElevators().stream().anyMatch(e ->
                e.getCurrentFloor() == 0 || ( !e.getTargetFloors().isEmpty() && e.getTargetFloors().get(0) == 0 )
        );

        assertTrue(someoneAtOrMovingTo0, "En yakın asansör 0. kata atanmalı veya yolda olmalı.");
    }
}
