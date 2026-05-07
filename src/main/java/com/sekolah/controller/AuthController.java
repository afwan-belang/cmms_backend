package com.sekolah.controller;

import com.sekolah.config.DatabaseConnection;
import com.sekolah.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    // Inject JwtUtil dan PasswordEncoder
    public AuthController(JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String plainPassword = payload.get("password");

        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String hashedPasswordFromDb = rs.getString("password");

                // Bandingkan password inputan dengan password BCrypt di Database
                if (passwordEncoder.matches(plainPassword, hashedPasswordFromDb)) {

                    int userId = rs.getInt("user_id");
                    String role = rs.getString("role");
                    String fullName = rs.getString("full_name");

                    // 1. Buat Token JWT
                    String token = jwtUtil.generateToken(userId, username, role, fullName);

                    // 2. Siapkan data user (tanpa password)
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("id", userId);
                    userData.put("username", username);
                    userData.put("fullName", fullName);
                    userData.put("role", role);

                    // 3. Gabungkan Token dan Data User
                    Map<String, Object> response = new HashMap<>();
                    response.put("token", token);
                    response.put("user", userData);

                    System.out.println(">> Login SUKSES untuk: " + username);
                    return response;
                }
            }
            System.out.println(">> Login GAGAL: Username/Password salah.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Return null jika gagal (Akan ditangkap oleh React)
    }
}