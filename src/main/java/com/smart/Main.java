package com.smart;

import com.smart.sim.ConfigLoader;
import com.smart.sim.Db;
import com.smart.sim.SimulationConfig;
import com.smart.sim.StepLogRepository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * Asansör simülasyonu ana sınıfı.
 * Konfigürasyon yükleme, sistem başlatma ve simülasyon döngüsü.
 */
public class Main {
    private static final int SLOT_STEP_INTERVAL = 20;
    private static final int STEP_LIMIT = 200;
    private static final int SIMULATION_DELAY_MS = 100;

    public static void main(String[] args) throws InterruptedException {
        Db.init();
        System.out.println("=== Smart Elevator System Simulation ===\n");

        // 1) Konfigürasyonu yükle
        SimulationConfig cfg = ConfigLoader.load();
        System.out.println("[CONFIG] " + cfg + "\n");

        // 2) Sistemi başlat
        ElevatorSystem system = initializeSystem(cfg);
        StepLogRepository repo = initializeDatabase();

        // 3) Test verisi oluştur
        generateHistoricalCallLogs(system, cfg);
        generateInitialCalls(system);
        generatePassengerRequests(system);

        // 4) Simülasyon döngüsü
        runSimulation(system, repo, cfg);

        System.out.println("\n=== Simulation Complete ===");
    }

    /**
     * Sistem başlatma - katlar ve asansörler.
     */
    private static ElevatorSystem initializeSystem(SimulationConfig cfg) {
        List<Floor> floors = new ArrayList<>();
        for (int i = 0; i < cfg.getFloors(); i++) {
            floors.add(new Floor(i));
        }

        List<Elevator> elevators = new ArrayList<>();
        for (int i = 0; i < cfg.getElevators(); i++) {
            int startFloor = cfg.getInitialPositions().get(i);
            Elevator e = new Elevator(
                    i + 1,
                    cfg.getElevatorCapacity(),
                    cfg.getWeightLimitKg()
            );
            e.setCurrentFloor(startFloor);
            elevators.add(e);
        }

        return new ElevatorSystem(elevators, floors);
    }

    /**
     * Veritabanı başlatma.
     */
    private static StepLogRepository initializeDatabase() {
        Db.init();
        return new StepLogRepository();
    }

    /**
     * Geçmiş çağrı logları oluştur (1 günlük örnek veri).
     */
    private static void generateHistoricalCallLogs(ElevatorSystem system, SimulationConfig cfg) {
        Random random = new Random(42); // Sabit seed - tekrarlanabilir sonuçlar

        for (int hour = 0; hour < 24; hour++) {
            for (int slot = 0; slot < 3; slot++) {
                LocalDateTime baseTime = LocalDateTime.of(2025, 10, 30, hour, slot * 20);

                // Her slot'ta 3-5 rastgele çağrı
                int callCount = 3 + random.nextInt(3);
                for (int j = 0; j < callCount; j++) {
                    int floor = random.nextInt(cfg.getFloors());
                    boolean goingUp = random.nextBoolean();
                    system.callLogs.add(new CallLog(floor, goingUp, baseTime.plusMinutes(j)));
                }
            }
        }

        System.out.println("[DATA] Generated " + system.callLogs.size() + " historical call logs\n");
    }

    /**
     * Başlangıç çağrıları - sistem testi için.
     */
    private static void generateInitialCalls(ElevatorSystem system) {
        System.out.println("=== Initial Calls ===");
        system.requestElevator(2, Direction.UP);
        system.requestElevator(7, Direction.DOWN);

        // İç çağrılar (kabin butonları)
        system.getElevators().get(0).addInternalRequest(9);
        if (system.getElevators().size() > 1) {
            system.getElevators().get(1).addInternalRequest(1);
        }
        System.out.println();
    }

    /**
     * Yolcu çağrıları oluştur.
     */
    private static void generatePassengerRequests(ElevatorSystem system) {
        System.out.println("=== Passenger Requests ===");

        system.requestElevator(0, 5, 70);
        system.requestElevator(9, 2, 80);
        system.requestElevator(3, 7, 65);
        system.requestElevator(1, 8, 75);

        System.out.println();
    }

    /**
     * Ana simülasyon döngüsü.
     */
    private static void runSimulation(ElevatorSystem system, StepLogRepository repo, SimulationConfig cfg)
            throws InterruptedException {

        int step = 0;

        while (true) {
            System.out.println("\n=== STEP " + step + " ===");

            // Her 20 adımda bir yoğunluk analizi yap
            if (step % SLOT_STEP_INTERVAL == 0) {
                performTrafficPrediction(system, step);
            }

            // Durum çıktısı
            printElevatorStatus(system);

            // Veritabanına kaydet
            repo.insertStep(step, system.getElevators());

            // Sistemi ilerlet
            system.step();

            // Bitiş koşulları
            if (shouldStopSimulation(system, step)) {
                break;
            }

            step++;
            Thread.sleep(SIMULATION_DELAY_MS);
        }
    }

    /**
     * Yoğunluk tahmini ve proaktif asansör atama.
     */
    private static void performTrafficPrediction(ElevatorSystem system, int step) {
        int simulatedHour = (step / SLOT_STEP_INTERVAL) / 3;
        int simulatedSlot = (step / SLOT_STEP_INTERVAL) % 3;
        int slotIndex = (simulatedHour * 3) + simulatedSlot;

        CallLogAnalyzer analyzer = new CallLogAnalyzer(system.callLogs);
        Map<Integer, Integer> busyFloors = analyzer.getBusyFloors(slotIndex, 2);

        for (Integer floor : busyFloors.keySet()) {
            boolean alreadyAssigned = system.getElevators().stream()
                    .anyMatch(e ->
                            (!e.getTargetFloors().isEmpty() && e.getTargetFloors().get(0) == floor)
                                    || e.getCurrentFloor() == floor
                    );

            if (!alreadyAssigned) {
                system.sendIdleElevatorToFloor(floor);
                System.out.println("[PREDICTION] Traffic detected → Floor " + floor);
            }
        }
    }

    /**
     * Asansör durumlarını ekrana yazdır.
     */
    private static void printElevatorStatus(ElevatorSystem system) {
        for (Elevator e : system.getElevators()) {
            String target = e.getTargetFloors().isEmpty()
                    ? "-"
                    : e.getTargetFloors().toString();

            String mode = (e.getState() == ElevatorState.IDLE) ? "IDLE" : "BUSY";
            String direction = e.getDirection().toString();

            System.out.printf(
                    "Elevator %d | Floor: %2d | State: %-11s | Dir: %-4s | Target: %-15s | Load: %3d kg | Mode: %s | Time: %s%n",
                    e.getId(),
                    e.getCurrentFloor(),
                    e.getState(),
                    direction,
                    target,
                    e.getCurrentLoad(),
                    mode,
                    LocalTime.now().withNano(0)
            );
        }
    }

    /**
     * Simülasyon bitiş koşulları.
     */
    private static boolean shouldStopSimulation(ElevatorSystem system, int step) {
        boolean allIdle = system.getElevators().stream()
                .allMatch(e -> e.getState() == ElevatorState.IDLE);
        boolean noPending = !system.hasPendingCalls();

        return (allIdle && noPending && step > 50) || step > STEP_LIMIT;
    }
}