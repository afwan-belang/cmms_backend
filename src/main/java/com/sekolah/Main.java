package com.sekolah;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        // Ini akan menjalankan server di port 8080
        SpringApplication.run(Main.class, args);
        System.out.println(">> SERVER RUNNING DI: http://localhost:8080");
    }
}