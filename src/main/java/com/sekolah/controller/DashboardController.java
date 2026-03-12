package com.sekolah.controller;

import com.sekolah.config.DatabaseConnection;
import org.springframework.web.bind.annotation.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:5173") // Izinkan React
public class DashboardController {

    @GetMapping("/stats")
    public Map<String, Integer> getStats() {
        Map<String, Integer> stats = new HashMap<>();

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {

            // 1. Hitung Total Aset
            ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) AS total FROM assets");
            if(rs1.next()) stats.put("totalAssets", rs1.getInt("total"));

            // 2. Hitung Aset yang sedang RUNNING
            ResultSet rs2 = stmt.executeQuery("SELECT COUNT(*) AS total FROM assets WHERE status = 'RUNNING'");
            if(rs2.next()) stats.put("runningAssets", rs2.getInt("total"));

            // 3. Hitung Tiket yang masih OPEN (Rusak/Belum diperbaiki)
            ResultSet rs3 = stmt.executeQuery("SELECT COUNT(*) AS total FROM work_orders WHERE status = 'OPEN'");
            if(rs3.next()) stats.put("openTickets", rs3.getInt("total"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stats;
    }
}