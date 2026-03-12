package com.sekolah.controller;

import com.sekolah.config.DatabaseConnection;
import com.sekolah.model.User;
import org.springframework.web.bind.annotation.*;
import java.sql.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
// PENTING: Baris ini mengizinkan React mengakses Java
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @PostMapping("/login")
    public User login(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");

        System.out.println(">> Menerima Login dari: " + username); // Debugging di Console Java

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println(">> Login SUKSES!");
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        "HIDDEN",
                        rs.getString("full_name"),
                        rs.getString("role")
                );
            } else {
                System.out.println(">> Login GAGAL: Password salah.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Balikkan null jika gagal
    }
}