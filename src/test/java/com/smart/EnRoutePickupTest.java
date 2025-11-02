package com.smart;

import com.smart.sim.ConfigLoader;
import com.smart.sim.SimulationConfig;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EnRoutePickupTest {

    private ElevatorSystem bootstrapSystem() {
        SimulationConfig cfg = ConfigLoader.load();

        List<Floor> floors = new ArrayList<>();
        for (int i = 0; i < cfg.getFloors(); i++) floors.add(new Floor(i));

        List<Elevator> elevators = new ArrayList<>();
        // Tek asansörle de yeter ama config'i kullanıyoruz
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
    void elevatorAddsIntermediateStopWhenQueueOnTheWay() {
        ElevatorSystem system = bootstrapSystem();

        // Asansör 1'i 0'dan başlatıyoruz varsayım (config'te [0,8] idi)
        // Hedefi 9 yapalım:
        system.getElevators().get(0).addTargetFloor(9);

        // Yolda 3. katta yukarı çağrı oluştur (en-route pickup'a aday)
        system.requestElevator(3, Direction.UP);

        // birkaç adım ilerlet
        runSteps(system, 6);

        // Asansörün hedeflerine 3 eklenmiş olmalı (yukarı yönde ara durak)
        List<Integer> targets = system.getElevators().get(0).getTargetFloors();
        assertTrue(targets.contains(3), "Yoldaki uygun kat (3) hedeflere eklenmeli.");
    }
}
