package com.ifsc.tarefas.auth;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.ifsc.tarefas.model.User;
import com.ifsc.tarefas.repository.UserRepository;

@Repository
public class AuthRepository {
    // guardar informaçãos do token, não muito inteligente usar isso em prod
    private final Map<String, SessionInfo> tokenStore = new ConcurrentHashMap<>();

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public AuthRepository(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    public Optional<String> login(String username, String password) {
        // verificar se o username e senha estao corretos e estão no banco
        return userRepository.findByUsername(username)
            .filter(u -> u.getPassword().equals(password))
            .map(u -> { 
                // se achou o user, gera o token e o seta na memoria
                String jwt = jwtUtil.generateToken(u.getUsername(), u.getRole());
                tokenStore.put(jwt, new SessionInfo(u.getUsername(), Instant.now()));
                return jwt;
            });
    }

    // validar o token, se ta tudo certo
    public Optional<String> validate(String token){
        if(token == null || token.isBlank()) return Optional.empty();
        try {
            SessionInfo sessionInfo = tokenStore.get(token);
            if(sessionInfo == null) return Optional.empty();
            return Optional.ofNullable(jwtUtil.getSubject(token));

        }catch (Exception e) {
            return Optional.empty();
        }
    }

    // saiu do sistema, mata o token
    public void logout(String token) { 
        if(token != null){
            tokenStore.remove(token);
        }
    }

    public boolean register(String username, String password){
        if(username == null || password == null) return false;

        if(userRepository.existsByUsername(username)) return false;

        User u = new User();
        u.setUsername(username);
        u.setPassword(password);
        u.setRole("USER");

        userRepository.save(u);
        return true;
    }


    public String getRoleByUsername(String username) {
        return userRepository.findByUsername(username)
            .map(User::getRole)
            .orElse(null);
    }


    // salva o token no "banco de dados", no caso na memoria por enquanto
    private record SessionInfo(String username, Instant authentificatedAt) {}
}
