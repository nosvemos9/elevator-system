package com.smart;

import com.smart.sim.ConfigLoader;
import com.smart.sim.SimulationConfig;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Kabin kapasite/limit testleri.
 */
class WeightLimitTest {

    private ElevatorSystem bootstrapSystemWithSmallLimit() {
        SimulationConfig cfg = ConfigLoader.load();

        List<Floor> floors = new ArrayList<>();
        for (int i = 0; i < cfg.getFloors(); i++) {
            floors.add(new Floor(i));
        }

        List<Elevator> elevators = new ArrayList<>();
        Elevator e = new Elevator(1, 4, 100); // 100 kg limit
        e.setCurrentFloor(0);
        e.attachFloors(floors);
        elevators.add(e);

        return new ElevatorSystem(elevators, floors);
    }

    private void runSteps(ElevatorSystem system, int steps) {
        for (int i = 0; i < steps; i++) {
            system.step();
        }
    }

    @Test
    void doesNotBoardWhenExceedsWeightLimit() {
        ElevatorSystem system = bootstrapSystemWithSmallLimit();

        // 0->5 iki yolcu: 70kg ve 50kg => toplam 120kg (limit 100; ikincisi binmemeli)
        system.requestElevator(0, 5, 70);
        system.requestElevator(0, 5, 50);

        // Asansörü 0'a çağır
        system.requestElevator(0, Direction.UP);

        // Kapının açılıp bindirme yapması için yeterince adım
        runSteps(system, 20);

        Elevator e = system.getElevators().get(0);

        // DÜZELTİLMİŞ: getCurrentLoad() kullanıyoruz
        int currentLoad = e.getCurrentLoad();
        assertTrue(currentLoad <= 100,
                "Ağırlık limiti aşılmamalı, ikincisi bindirilmemeli. Mevcut yük: " + currentLoad + " kg");

        // Sadece bir yolcu binmeli (70 kg)
        assertEquals(1, e.getPassengers().size(),
                "Limit nedeniyle sadece 1 yolcu binmeli");

        // İlk yolcu binmeli
        assertEquals(70, currentLoad,
                "İlk yolcu (70kg) binmeli");
    }

    @Test
    void allowsBoardingWhenWithinLimit() {
        ElevatorSystem system = bootstrapSystemWithSmallLimit();

        // 0->5 iki yolcu: 40kg ve 50kg => toplam 90kg (limit 100; ikisi de binmeli)
        system.requestElevator(0, 5, 40);
        system.requestElevator(0, 5, 50);

        system.requestElevator(0, Direction.UP);

        runSteps(system, 20);

        Elevator e = system.getElevators().get(0);
        int currentLoad = e.getCurrentLoad();

        assertTrue(currentLoad <= 100, "Toplam yük limit içinde olmalı");
        assertEquals(2, e.getPassengers().size(), "İki yolcu da binmeli");
        assertEquals(90, currentLoad, "Toplam yük 90kg olmalı");
    }
}