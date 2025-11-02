// ===== File: src/main/java/com/smart/sim/StepLogRepository.java =====
package com.smart.sim;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalTime;
import java.util.List;

// FIX: Elevator, com.smart paketinde. Import eklenmeli.
import com.smart.Elevator;

public class StepLogRepository {

    // step, elevator_id, current_floor, target(TEXT), state(TEXT), direction(TEXT), load_kg(INT), time(TEXT)
    // Java 15+ text block:
    private static final String INSERT_SQL = """
        INSERT INTO step_logs
        (step, elevator_id, current_floor, target, state, direction, load_kg, time)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

    /* Eğer projen Java 11/14 ise, yukarıdaki text-block'u yorumlayıp bunu kullan:
    private static final String INSERT_SQL =
        "INSERT INTO step_logs " +
        "(step, elevator_id, current_floor, target, state, direction, load_kg, time) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    */

    public void insertStep(int step, List<Elevator> elevators) {
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement(INSERT_SQL)) {

            String now = LocalTime.now().withNano(0).toString();

            for (Elevator e : elevators) {
                String target = e.getTargetFloors().isEmpty()
                        ? "-"
                        : e.getTargetFloors().toString().replaceAll("[\\[\\]\\s]", ""); // "2,3,7"

                String stateTxt = (e.getState() == null) ? "UNKNOWN" : e.getState().name();
                String dirTxt   = (e.getDirection() == null) ? "IDLE"    : e.getDirection().name();

                ps.setInt(1, step);
                ps.setInt(2, e.getId());
                ps.setInt(3, e.getCurrentFloor());
                ps.setString(4, target);
                ps.setString(5, stateTxt);
                ps.setString(6, dirTxt);
                ps.setInt(7, e.getCurrentLoad());
                ps.setString(8, now);

                ps.addBatch();
            }
            ps.executeBatch();

        } catch (Exception ex) {
            System.err.println("[DB] insertStep error: " + ex.getMessage());
        }
    }
}
// ===== end of file =====
