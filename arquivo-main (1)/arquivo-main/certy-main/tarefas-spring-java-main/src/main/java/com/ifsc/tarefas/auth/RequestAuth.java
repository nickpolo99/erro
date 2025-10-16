package com.ifsc.tarefas.auth;

import jakarta.servlet.http.HttpServletRequest;
// classe para ajudar a pegar informações do token
public final class RequestAuth {
    
    // pegar o nome do usuario que está armazenado no token
    // HttpServletRequest = O que o usuário atual "mandou"
    public static String getUser(HttpServletRequest request){
        Object v = request.getAttribute("AUTH_USER");
        return v == null ? null : v.toString();
    }
    // pegar o papel do usuario que está usando o token
    // HttpServletRequest = O que o usuário atual "mandou"
    public static String getRole(HttpServletRequest request){
        Object v = request.getAttribute("AUTH_ROLE");
        return v == null ? null : v.toString();
    }
}
