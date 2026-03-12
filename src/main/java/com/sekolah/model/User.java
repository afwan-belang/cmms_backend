package com.sekolah.model;

public class User {
    private int id;
    private String username;
    private String password; // Nanti di industri asli ini harus di-enkripsi
    private String fullName;
    private String role; // 'ADMIN' atau 'TECHNICIAN'

    // Constructor kosong (Penting untuk beberapa library)
    public User() {}

    // Constructor lengkap
    public User(int id, String username, String password, String fullName, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
    }

    // Getter (Untuk mengambil data)
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }

    // Setter (Untuk mengubah data jika perlu)
    public void setRole(String role) { this.role = role; }
}