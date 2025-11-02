package com.smart.sim;

import java.util.List;

/**
 * Simülasyon konfigürasyonu.
 * Asansör sayısı, kat sayısı, fiziksel özellikler vb. parametreleri içerir.
 */
public class SimulationConfig {
    private final int floors;
    private final int elevators;
    private final List<Integer> initialPositions;
    private final double velocity;
    private final double acceleration;
    private final int doorWaitSeconds;

    // Yeni eklenenler
    private final int elevatorCapacity;
    private final int weightLimitKg;

    public SimulationConfig(
            int floors,
            int elevators,
            List<Integer> initialPositions,
            double velocity,
            double acceleration,
            int doorWaitSeconds,
            int elevatorCapacity,
            int weightLimitKg) {
        this.floors = floors;
        this.elevators = elevators;
        this.initialPositions = List.copyOf(initialPositions);
        this.velocity = velocity;
        this.acceleration = acceleration;
        this.doorWaitSeconds = doorWaitSeconds;
        this.elevatorCapacity = elevatorCapacity;
        this.weightLimitKg = weightLimitKg;
    }

    public int getFloors() { return floors; }
    public int getElevators() { return elevators; }
    public List<Integer> getInitialPositions() { return initialPositions; }
    public double getVelocity() { return velocity; }
    public double getAcceleration() { return acceleration; }
    public int getDoorWaitSeconds() { return doorWaitSeconds; }
    public int getElevatorCapacity() { return elevatorCapacity; }
    public int getWeightLimitKg() { return weightLimitKg; }

    @Override
    public String toString() {
        return "SimulationConfig{" +
                "floors=" + floors +
                ", elevators=" + elevators +
                ", initialPositions=" + initialPositions +
                ", velocity=" + velocity +
                ", acceleration=" + acceleration +
                ", doorWaitSeconds=" + doorWaitSeconds +
                ", elevatorCapacity=" + elevatorCapacity +
                ", weightLimitKg=" + weightLimitKg +
                '}';
    }
}