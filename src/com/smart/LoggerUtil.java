package com.smart;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class LoggerUtil {
    private static final String FILE_PATH = "call_logs.csv";
    private static boolean headerWritten = false;

    public static void logToCSV(CallRequest request) {
        try (FileWriter writer = new FileWriter(FILE_PATH, true)) {
            if (!headerWritten) {
                writer.write("Floor,Direction,RequestTime,ServedTime,WaitSeconds\n");
                headerWritten = true;
            }

            String line = String.format(
                    "%d,%s,%s,%s,%d\n",
                    request.floorNumber,
                    request.goingUp ? "UP" : "DOWN",
                    request.getRequestTime(),
                    request.getServedTime(),
                    request.getWaitSeconds()
            );

            writer.write(line);
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to write CSV log: " + e.getMessage());
        }
    }
}
