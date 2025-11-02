package com.smart;

public class Passenger {
    private int origin;
    private int destination;
    private int weight;

    public Passenger(int origin, int destination, int weight) {
        this.origin = origin;
        this.destination = destination;
        this.weight = weight;
    }

    public int getOrigin() {
        return origin;
    }

    public int getDestination() {
        return destination;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "Passenger{" +
                "origin=" + origin +
                ", destination=" + destination +
                ", weight=" + weight +
                '}';
    }
}