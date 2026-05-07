package com.sekolah.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // 1. Alamat Database (URL)
    // format: jdbc:mysql://host:port/nama_database
    private static final String URL = "jdbc:mysql://localhost:3306/db_cmms_sekolah";

    // 2. Username Database
    // Di Ubuntu/Linux, defaultnya sering 'root'
    private static final String USER = "root";

    // 3. Password Database
    // Kosongkan string ("") jika MySQL kamu tidak dipassword
    // Jika nanti error "Access Denied", kita akan atur user khusus.
    private static final String PASSWORD = "";

    public static Connection connect() {
        Connection conn = null;
        try {
            // Mendaftarkan Driver MySQL (Supaya Java kenal MySQL)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Mencoba login ke database
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println(">> BERHASIL: Koneksi ke Database terhubung!");

        } catch (ClassNotFoundException e) {
            System.err.println(">> ERROR: Driver JDBC tidak ditemukan.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println(">> ERROR: Gagal login ke MySQL. Cek username/password.");
            System.err.println("Pesan Error: " + e.getMessage());
        }
        return conn;
    }
}