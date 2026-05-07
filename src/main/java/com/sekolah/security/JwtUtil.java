package com.sekolah.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    // Kunci Rahasia. Di dunia nyata, ini disimpan di Environment Variables (.env)
    private static final String SECRET = "IniAdalahKunciRahasiaYangSangatPanjangUntukJWTTokenCMMSApp2026";
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    // Masa aktif token: 24 Jam
    private final long EXPIRATION_TIME = 86400000;

    public String generateToken(int userId, String username, String role, String fullName) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("role", role)
                .claim("fullName", fullName)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}