package com.sekolah.controller;

import com.sekolah.config.DatabaseConnection;
import com.sekolah.model.Asset;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity; // <--- Jangan lupa tambah ini
import org.springframework.http.HttpStatus;     // <--- Dan ini

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/assets")
@CrossOrigin(origins = "http://localhost:5173") // Wajib agar React bisa akses
public class AssetApiController {

    // 1. GET: Ambil Semua Data
    @GetMapping
    public List<Asset> getAllAssets() {
        List<Asset> listAset = new ArrayList<>();
        String sql = "SELECT * FROM assets ORDER BY asset_id DESC"; // Urutkan dari yang terbaru

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                listAset.add(new Asset(
                        rs.getInt("asset_id"),
                        rs.getString("asset_name"),
                        rs.getString("serial_number"),
                        rs.getString("location"),
                        rs.getString("status")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listAset;
    }

    // 2. POST: Tambah Mesin Baru (INI YANG HILANG SEBELUMNYA)
    @PostMapping
    public boolean addAsset(@RequestBody Asset asset) {
        String sql = "INSERT INTO assets (asset_name, serial_number, location, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, asset.getName());
            ps.setString(2, asset.getSerialNumber());
            ps.setString(3, asset.getLocation());
            ps.setString(4, asset.getStatus()); // Biasanya 'RUNNING' saat baru beli

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAsset(@PathVariable int id) {
        // Cek 1: Apakah aset ini punya riwayat di Work Orders?
        String sqlCheck = "SELECT COUNT(*) AS total FROM work_orders WHERE asset_id = ?";
        String sqlDelete = "DELETE FROM assets WHERE asset_id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement psCheck = conn.prepareStatement(sqlCheck)) {

            // Jalankan Cek
            psCheck.setInt(1, id);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next() && rs.getInt("total") > 0) {
                // GAGAL: Ada riwayat servis
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("GAGAL: Mesin tidak bisa dihapus karena memiliki riwayat perbaikan. Arsipkan saja statusnya.");
            }

            // Jika aman, Lakukan Hapus
            try (PreparedStatement psDelete = conn.prepareStatement(sqlDelete)) {
                psDelete.setInt(1, id);
                int rows = psDelete.executeUpdate();

                if (rows > 0) {
                    return ResponseEntity.ok("BERHASIL: Data aset dihapus.");
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aset tidak ditemukan.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error Database: " + e.getMessage());
        }
    }
}