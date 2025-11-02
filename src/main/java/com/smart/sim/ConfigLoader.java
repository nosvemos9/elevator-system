package com.smart.sim;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * config.properties dosyasından simülasyon parametrelerini yükler.
 */
public final class ConfigLoader {
    private ConfigLoader() {}

    public static SimulationConfig load() {
        Properties p = new Properties();

        try (InputStream in = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("config.properties")) {

            if (in == null) {
                throw new IllegalStateException("config.properties bulunamadı (resources altında olmalı)");
            }
            p.load(in);

        } catch (IOException e) {
            throw new IllegalStateException("config.properties okunamadı", e);
        }

        int floors = parseInt(p, "floors");
        int elevators = parseInt(p, "elevators");

        List<Integer> initialPositions = parseCsvInts(p, "initialPositions");
        if (initialPositions.size() != elevators) {
            throw new IllegalArgumentException(
                    "initialPositions sayısı, elevators değeriyle aynı olmalı. Beklenen="
                            + elevators + " ama gelen=" + initialPositions.size());
        }
        int maxInit = initialPositions.stream().max(Integer::compareTo).orElse(0);
        if (floors <= maxInit) {
            throw new IllegalArgumentException(
                    "floors değeri, en büyük başlangıç katından büyük olmalı. floors="
                            + floors + ", maxInitial=" + maxInit);
        }

        double velocity = parseDouble(p, "velocity");
        double acceleration = parseDouble(p, "acceleration");
        int doorWaitSeconds = parseInt(p, "doorWaitSeconds");

        // Yeni parametreler (default değerlerle)
        int elevatorCapacity = parseInt(p, "elevatorCapacity", 12);
        int weightLimitKg = parseInt(p, "weightLimitKg", 800);

        return new SimulationConfig(
                floors, elevators, initialPositions, velocity, acceleration,
                doorWaitSeconds, elevatorCapacity, weightLimitKg);
    }

    private static int parseInt(Properties p, String key) {
        String v = p.getProperty(key);
        if (v == null) throw new IllegalArgumentException(key + " eksik");
        try { return Integer.parseInt(v.trim()); }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException(key + " sayısal olmalı");
        }
    }

    private static int parseInt(Properties p, String key, int defaultValue) {
        String v = p.getProperty(key);
        if (v == null) return defaultValue;
        try { return Integer.parseInt(v.trim()); }
        catch (NumberFormatException e) {
            System.err.println("[WARN] " + key + " geçersiz, default kullanılıyor: " + defaultValue);
            return defaultValue;
        }
    }

    private static double parseDouble(Properties p, String key) {
        String v = p.getProperty(key);
        if (v == null) throw new IllegalArgumentException(key + " eksik");
        try { return Double.parseDouble(v.trim()); }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException(key + " sayısal olmalı");
        }
    }

    private static List<Integer> parseCsvInts(Properties p, String key) {
        String v = p.getProperty(key);
        if (v == null) throw new IllegalArgumentException(key + " eksik");
        return Stream.of(v.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
}