package com.ifsc.tarefas.exceptions;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;

// classe de erro, quando eu tiver um erro 
//  eu vou criar essa classe de erro
public class ApiError {
    // informacoes do erro
    // data e hora do erro
    private LocalDateTime timestamp;
    // status do erro, 404, 500, 405 e etc
    private int status;
    // qual erro aconteceu
    private String error;
    // mensagem retornada ao usuario
    private String message;
    // caminho da api que deu erro
    private String path;
    // informacoes sobre o erro, ex: campo é obrigatorio e etc
    private Map<String, String> fields;

    // constructor padrao do objeto
    public ApiError(int status, String error, String message, String path) {
            this.timestamp = LocalDateTime.now();
            this.status = status;
            this.error = error;
            this.message = message;
            this.path = path;
        }

        public ApiError(int status, String error, String message, String path, Map<String, String> fields) {
            this(status, error, message, path);
            this.fields = fields;
        }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public Map<String, String> getFields() {
        return fields;
    }
    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }
    
    
}
