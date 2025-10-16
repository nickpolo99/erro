package com.ifsc.tarefas.model;

// Um enum torna uma variavel que só possa ter resultados delimitados
// exemplo, a variavel só pode receber a string "BAIXA" ou "ALTA" 
// OU "MEDIA" e nada mais
public enum Prioridade {
   BAIXA,
   MEDIA,
   ALTA;

   private Prioridade() {
   }

}




