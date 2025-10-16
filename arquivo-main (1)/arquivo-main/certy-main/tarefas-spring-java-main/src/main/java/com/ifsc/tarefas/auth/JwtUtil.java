package com.ifsc.tarefas.auth;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;


@Component
public class JwtUtil {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXP_SECONDS = 8 * 60 * 60;

    public String generateToken(String username, String role){
        Instant now = Instant.now();
        return Jwts.builder()
            // quem é o "dono"
            .setSubject(username)
            // adicionar no token a role
            .addClaims(Map.of("role", role))
            // quando foi criado
            .setIssuedAt(Date.from(now))
            // quando vai expirar
            .setExpiration(Date.from(now.plusSeconds(EXP_SECONDS)))
            // use a key pra assinar
            .signWith(key)
            .compact();
    }

    // pegar o username do token
    public String getSubject(String token) throws Exception {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    // pegar a permissão do token
    public String getRole(String token) throws Exception {
        Object role = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .get("role");
            
        return role == null ? null : role.toString();
    }
}
