package com.smart;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Çağrı loglarını CSV dosyasına kaydeden utility sınıfı.
 */
public class LoggerUtil {
    private static final String FILE_PATH = "target/call_logs.csv";
    private static boolean headerWritten = false;

    /**
     * CallRequest nesnesini CSV'ye kaydeder.
     */
    public static void logToCSV(CallRequest request) {
        try {
            // target klasörünü oluştur
            Path targetDir = Path.of("target");
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            try (FileWriter writer = new FileWriter(FILE_PATH, true)) {
                if (!headerWritten) {
                    writer.write("Floor,Direction,RequestTime,ServedTime,WaitSeconds\n");
                    headerWritten = true;
                }

                String servedTime = (request.getServedTime() != null)
                        ? request.getServedTime().toString()
                        : "NOT_SERVED";

                long waitSeconds = request.getWaitSeconds();
                String waitStr = (waitSeconds >= 0) ? String.valueOf(waitSeconds) : "-1";

                String line = String.format(
                        "%d,%s,%s,%s,%s%n",
                        request.floorNumber,
                        request.goingUp ? "UP" : "DOWN",
                        request.getRequestTime(),
                        servedTime,
                        waitStr
                );

                writer.write(line);
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to write CSV log: " + e.getMessage());
        }
    }

    /**
     * Header'ı sıfırla (test amaçlı).
     */
    public static void resetHeader() {
        headerWritten = false;
    }
}