package com.ifsc.tarefas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ifsc.tarefas.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    // Optional = Pode ser nulo o nome
    // findBy = procurar alguem por nome
    Optional<Categoria> findByNome(String nome);
    // existe alguem com esse nome?
    boolean existsByNome(String nome);

} 
    

