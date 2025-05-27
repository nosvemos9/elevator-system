package com.smart;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Çağrıları 20 dakikalık slotlara bölüp kat bazlı yoğunluk analizi yapan sınıf
public class CallLogAnalyzer {

    // Slot başına kat çağrı sayıları
    private Map<Integer, Map<Integer, Integer>> slotFloorCalls = new HashMap<>();

    // 20 dakikalık slot toplamı (24 saat * 3 slot/saat = 72 slot)
    private final int SLOT_COUNT = 72;
    private final int SLOT_DURATION_MINUTES = 20;

    public CallLogAnalyzer(List<CallLog> callLogs) {
        analyze(callLogs);
    }

    // Slot indeksini hesaplayan metod (0'dan 71'e kadar)
    private int getSlotIndex(LocalDateTime time) {
        int minutes = time.getHour() * 60 + time.getMinute();
        return minutes / SLOT_DURATION_MINUTES;
    }

    // Tüm çağrıları analiz eder ve slot bazında kat çağrı sayılarını hesaplar
    private void analyze(List<CallLog> callLogs) {
        for (CallLog log : callLogs) {
            int slotIndex = getSlotIndex(log.timestamp);
            slotFloorCalls.putIfAbsent(slotIndex, new HashMap<>());

            Map<Integer, Integer> floorCalls = slotFloorCalls.get(slotIndex);
            floorCalls.put(log.floor, floorCalls.getOrDefault(log.floor, 0) + 1);
        }
    }

    // Verilen slot indeksindeki tüm kat çağrı sayılarını döner
    public Map<Integer, Integer> getCallsForSlot(int slotIndex) {
        return slotFloorCalls.getOrDefault(slotIndex, new HashMap<>());
    }

    // Belirli slot ve kat için çağrı sayısını döner
    public int getCallCount(int slotIndex, int floor) {
        return slotFloorCalls.getOrDefault(slotIndex, new HashMap<>()).getOrDefault(floor, 0);
    }

    // Belirli bir eşik değerinin üzerinde çağrı sayısı olan katları döner
    public Map<Integer, Integer> getBusyFloors(int slotIndex, int threshold) {
        Map<Integer, Integer> busyFloors = new HashMap<>();
        Map<Integer, Integer> floorCalls = slotFloorCalls.getOrDefault(slotIndex, new HashMap<>());

        for (Map.Entry<Integer, Integer> entry : floorCalls.entrySet()) {
            if (entry.getValue() >= threshold) {
                busyFloors.put(entry.getKey(), entry.getValue());
            }
        }

        return busyFloors;
    }
}
