package com.ifsc.tarefas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ifsc.tarefas.model.Arquivo;

public interface ArquivoRepository extends JpaRepository<Arquivo, Long> {

}