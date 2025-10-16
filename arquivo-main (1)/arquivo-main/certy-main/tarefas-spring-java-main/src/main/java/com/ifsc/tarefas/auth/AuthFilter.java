package com.ifsc.tarefas.auth;

import java.io.IOException;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthFilter extends OncePerRequestFilter {

    // final, não da de mudar ;)
    private final AuthRepository authRepository;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private final JwtUtil jwtUtil;

    public AuthFilter(AuthRepository authRepository, JwtUtil jwtUtil) {
        this.authRepository = authRepository;
        this.jwtUtil = jwtUtil;
    }

    // apis que podem acessar sem o token
    private static final Set<String> PUBLIC_PATTERNS = Set.of(
        "/login", 
        "/login/**", 
        "/register", 
        "/register/**",
        "/css/**",
        "/js/**",
        "/images/**",
        "/webjars/**",
        "/h2-console/**",
        "/"
    );

    // Filtra as requisições que precisam de autenticação, algumas não precisam exemplo o login
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        // procura nas urls publicas e se encontrar retorna true pode passar sem autenticação
        for(String url: PUBLIC_PATTERNS) {
            if(antPathMatcher.match(url, path)) {
                return true;
            }
        }

        // permitir o OPTIONS, exemplo o filtro de cors
        if("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        // se nao encontrar retorna false e precisa autenticação
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // pega o token
        String token = extractTokenFromCookie(request, "AUTH_TOKEN");

        // Se não achar no cookie, verificar se existe no header
        if(token == null || token.isBlank()) {
            String authHeader = request.getHeader("Authorization");
            // se achou o token e ele começa com o "Bearer "
            if(authHeader != null && authHeader.startsWith("Bearer ")) {
                // Removemos o bearer da string
                token = authHeader.substring(7);
            }
     
        }

        // vlaida o token e pega o username  
        var user = authRepository.validate(token);

        if(user.isEmpty()){
            // n tem usuario
            // vamos redirecionar para tela de login
            String accept = request.getHeader("Accept");
            // se o que foi pedido foi um html (uma pagina) redireciona para a pagina login
            if(accept != null && accept.contains("text/html")) {
                String redirectTo = request.getRequestURI();
                response.sendRedirect("/login?redirect=" + redirectTo);
                return; 
            }
            // se não envia uma mensagem de erro 401 (Não autorizado)
            // montando a resposta
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Unauthorized\"}");
            return;
        }
        // pega o username do user
        String username = user.get();
        // seta o username no request
        request.setAttribute("AUTH_USER", username);
        // realiza um try catch para pegar o role
        try {
            String role = jwtUtil.getRole(token);
            // se achou a role no token ok, se n pesquisa pelo o username
            request.setAttribute("AUTH_ROLE", role != null ? role : authRepository.getRoleByUsername(username));
        } catch (Exception e) {
            request.setAttribute("AUTH_ROLE",  authRepository.getRoleByUsername(username));
        }

        filterChain.doFilter(request, response);
    }

    // função para extrair os cookies
    private String extractTokenFromCookie(HttpServletRequest request, String cookieAutentificacao) {
        // pega oq o usuario enviou e especificamente pega os cookies
        Cookie[] cookies = request.getCookies();
        if(cookies == null) return null;

        for(Cookie cookie : cookies) {
            // se encontrar o cookie de autenticação retorna ele 
            if(cookieAutentificacao.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        

        // se nao encontrar o cookie de autentificação, retorna null
        return null;
        
    }


}
