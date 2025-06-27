package com.api.Event_Management_API.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {
    
    private Key key;

    @PostConstruct
    public void init() {
        String secret = System.getProperty("JWT_SECRET");

        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("JWT_SECRET is not found or has length shorter than 32 characters");
        }

        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String maTaiKhoan, String tenDangNhap, String vaiTro) {
        long expiration = 1000 * 60 * 60 * 24 * 7; // 7 days

        Map<String, Object> claims = new HashMap<>();

        claims.put("maTaiKhoan", maTaiKhoan);
        claims.put("tenDangNhap", tenDangNhap);
        claims.put("vaiTro", vaiTro);

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(maTaiKhoan)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    public Claims validateToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(this.key)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}
