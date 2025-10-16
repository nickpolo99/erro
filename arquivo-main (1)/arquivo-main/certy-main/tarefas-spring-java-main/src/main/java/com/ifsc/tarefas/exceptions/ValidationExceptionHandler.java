package com.ifsc.tarefas.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

// vai ficar escutando as chamadas das apis
@RestControllerAdvice
public class ValidationExceptionHandler {

    // erro 400 - bad request
    // se um erro acontecer nas anotações que colocamos no model
    // vai capturar esse erro
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        // preparando uma lista vazia para armazenar os erros que vai aparecer
        Map<String, String> errors = new HashMap<>();

        // percorrendo a lista de erros que veio
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            // adiciono na lista o campo que deu erro e sua mensagem 
            errors.put(error.getField(), error.getDefaultMessage());
        }

        // montando um objeto padrão com o erro
        ApiError apiError = new ApiError(
            HttpStatus.BAD_REQUEST.value(), 
            "Bad Request", 
            "Erro de validação dos campos",
             request.getDescription(false), 
             errors);

        // retornando o erro de bad request
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
        // response entity monta os erros
        // ResponseEntity.notFound().build(ApiError);
        // ResponseEntity.ok().build();
        // ResponseEntity.badRequest().build();

    }
    

    // erro 500 
    // err oque explode quando "da merda" no codigo
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleInternalServerError(Exception ex, WebRequest request) {
        // montando o objeto com qual erro que veio
        ApiError apiError = new ApiError(
            HttpStatus.INTERNAL_SERVER_ERROR.value(), 
            "Internal Server Error", 
            "Erro no servidor",
             request.getDescription(false));

        // retornando o erro
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);

    }
}
