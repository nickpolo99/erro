package com.ifsc.tarefas.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ifsc.tarefas.model.Prioridade;
import com.ifsc.tarefas.model.Status;
import com.ifsc.tarefas.model.Tarefa;

// Fala direto com o nosso banco de dados
// Com o JpaRepository ja criamos o CRUD inicial 
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
    // exigente nomenclatura    
    // vai buscar todas as tarefas que são iguais a o titulo passado
    List<Tarefa> findByTitulo(String titulo);
    // pesquisar por status
    List<Tarefa> findByStatus(Status status);
    // procurar por responsavel
    List<Tarefa> findByResponsavel(String responsavel);
    // procurar por Data limite antes de tal data
    List<Tarefa> findByDataLimiteBefore(LocalDate dataLimite);
    // procurar por prioridade
    List<Tarefa> findByPrioridade(Prioridade prioridade);
    
    List<Tarefa> findByStatusAndPrioridade(Status status, Prioridade prioridade);

} 
// insert
// select
// select por id
// delete