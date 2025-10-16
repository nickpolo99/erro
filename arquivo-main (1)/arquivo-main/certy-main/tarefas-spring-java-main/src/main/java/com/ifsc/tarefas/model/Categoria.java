package com.ifsc.tarefas.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

// categoria tabela
@Entity
public class Categoria {

   @Column(name = "categoria_id")
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   private String nome;
   // Json ignore pra n mostrar essa coluna, evitando um erro de loop infinito 
   // tarefa -> categoria -> tarefa -> categoria e etc
   @JsonIgnore
   // To dizendo que quem manda nessa relação de muitos para muitos é o tarefa
   @ManyToMany(mappedBy = "categorias")
   // hash set são listas que não aceitam repetição
   private Set<Tarefa> tarefas = new HashSet<>();

   public Long getId() {
    return id;
   }
   public void setId(Long id) {
    this.id = id;
   }
   public String getNome() {
    return nome;
   }
   public void setNome(String nome) {
    this.nome = nome;
   }
   public Set<Tarefa> getTarefas() {
      return tarefas;
   }
   public void setTarefas(Set<Tarefa> tarefas) {
      this.tarefas = tarefas;
   }


   
}
