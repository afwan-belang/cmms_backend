package com.sekolah.controller;

import com.sekolah.config.DatabaseConnection;
import com.sekolah.model.WorkOrder;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/work-orders")
@CrossOrigin(origins = "http://localhost:5173")
public class WorkOrderApiController {

    @GetMapping
    public List<WorkOrder> getAll() {
        List<WorkOrder> list = new ArrayList<>();
        String sql = "SELECT wo.wo_id, a.asset_name, u.full_name, wo.issue_description, wo.priority, wo.status, wo.created_at " +
                "FROM work_orders wo " +
                "JOIN assets a ON wo.asset_id = a.asset_id " +
                "LEFT JOIN users u ON wo.technician_id = u.user_id " +
                "ORDER BY wo.created_at DESC";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while(rs.next()) {
                String tech = rs.getString("full_name");
                if(tech == null) tech = "-";
                list.add(new WorkOrder(
                        rs.getInt("wo_id"), rs.getString("asset_name"), tech,
                        rs.getString("issue_description"), rs.getString("priority"),
                        rs.getString("status"), rs.getTimestamp("created_at")
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // --- FIX BUG STATISTIK DISINI ---
    @PostMapping
    public boolean create(@RequestBody Map<String, Object> payload) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.connect();
            conn.setAutoCommit(false); // Mulai Transaksi

            // 1. Simpan Laporan (Work Order)
            String sqlWO = "INSERT INTO work_orders (asset_id, issue_description, priority, status) VALUES (?, ?, ?, 'OPEN')";
            try (PreparedStatement psWO = conn.prepareStatement(sqlWO)) {
                psWO.setInt(1, Integer.parseInt(payload.get("assetId").toString()));
                psWO.setString(2, payload.get("issue").toString());
                psWO.setString(3, payload.get("priority").toString());
                psWO.executeUpdate();
            }

            // 2. UPDATE STATUS MESIN JADI 'DOWN' (Agar statistik berkurang)
            String sqlAsset = "UPDATE assets SET status = 'DOWN' WHERE asset_id = ?";
            try (PreparedStatement psAsset = conn.prepareStatement(sqlAsset)) {
                psAsset.setInt(1, Integer.parseInt(payload.get("assetId").toString()));
                psAsset.executeUpdate();
            }

            conn.commit(); // Simpan perubahan
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            return false;
        } finally {
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (SQLException ex) {}
        }
    }

    @PutMapping("/{id}/complete")
    public boolean complete(@PathVariable int id, @RequestBody Map<String, String> payload) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.connect();
            conn.setAutoCommit(false);

            // 1. Update WO jadi COMPLETED
            String sqlWO = "UPDATE work_orders SET status = 'COMPLETED', action_taken = ? WHERE wo_id = ?";
            try (PreparedStatement psWO = conn.prepareStatement(sqlWO)) {
                psWO.setString(1, payload.get("action"));
                psWO.setInt(2, id);
                psWO.executeUpdate();
            }

            // 2. Ambil Asset ID
            int assetId = 0;
            String sqlGetId = "SELECT asset_id FROM work_orders WHERE wo_id = ?";
            try (PreparedStatement psGet = conn.prepareStatement(sqlGetId)) {
                psGet.setInt(1, id);
                ResultSet rs = psGet.executeQuery();
                if (rs.next()) assetId = rs.getInt("asset_id");
            }

            // 3. UPDATE STATUS MESIN JADI 'RUNNING' (Statistik bertambah lagi)
            if (assetId > 0) {
                String sqlAsset = "UPDATE assets SET status = 'RUNNING' WHERE asset_id = ?";
                try (PreparedStatement psAsset = conn.prepareStatement(sqlAsset)) {
                    psAsset.setInt(1, assetId);
                    psAsset.executeUpdate();
                }
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            return false;
        } finally {
            try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (SQLException ex) {}
        }
    }
}