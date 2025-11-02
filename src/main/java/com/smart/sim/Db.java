package com.smart.sim; // mevcut paketini bozma; başka pakete taşıdıysan onu yaz

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public final class Db {

    private static final String DB_DIR  = "data";
    private static final String DB_FILE = "elevator.db";
    private static final String JDBC_URL = "jdbc:sqlite:" + DB_DIR + "/" + DB_FILE;

    static {
        try { Class.forName("org.sqlite.JDBC"); }
        catch (ClassNotFoundException e) { throw new RuntimeException("SQLite driver not found", e); }
    }


    private Db() {}

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(JDBC_URL);
        } catch (Exception e) {
            throw new RuntimeException("DB connection error: " + e.getMessage(), e);
        }
    }

    /** Proje başında 1 kez çağır: klasörü oluştur + tabloyu hazırla */
    public static void init() {
        try {
            ensureDbFolder();
            try (Connection c = getConnection(); Statement st = c.createStatement()) {
                st.execute("""
                    CREATE TABLE IF NOT EXISTS step_logs (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        step INTEGER NOT NULL,
                        elevator_id INTEGER NOT NULL,
                        current_floor INTEGER NOT NULL,
                        target TEXT,
                        state TEXT,
                        direction TEXT,
                        load_kg INTEGER,
                        time TEXT
                    )
                """);
            }
            System.out.println("[DB] Ready at " + JDBC_URL);
        } catch (Exception e) {
            throw new RuntimeException("DB init error: " + e.getMessage(), e);
        }
    }

    private static void ensureDbFolder() throws Exception {
        Path dir = Paths.get(DB_DIR);
        if (!Files.exists(dir)) Files.createDirectories(dir);
    }
}
