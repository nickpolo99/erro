package com.ifsc.tarefas.services;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ifsc.tarefas.model.Prioridade;
import com.ifsc.tarefas.model.Status;
import com.ifsc.tarefas.model.Tarefa;
import com.ifsc.tarefas.repository.CategoriaRepository;
import com.ifsc.tarefas.repository.TarefaRepository;

import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController // anotação que indica que essa classe é um service
@RequestMapping("/tarefas") // anotação que define padrão url exemplo: /tarefas/inserir
public class TarefaServices {
   
    // Injetando o repositorio de tarefa pra usar no service e buscar coisas do banco
    private final TarefaRepository tarefaRepository;
    private final CategoriaRepository categoriaRepository;

    
    public TarefaServices(TarefaRepository tarefaRepository, CategoriaRepository categoriaRepository) {
        this.tarefaRepository = tarefaRepository;
        this.categoriaRepository = categoriaRepository;
    }

    // anotação pra GET
    // pra chamar minha api de buscar todas eu uso --> /tarefas/buscar-todos <--
    @GetMapping("/buscar-todos")
    public ResponseEntity<?> buscarTodas(){
        // uso o repository pra buscar todas as tarefas
        return ResponseEntity.ok(tarefaRepository.findAll());
    }
    
    // api para criar uma nova tarefa
    // link da api para o post --> /tarefas/inserir: 
    // preciso informar a anotação @RequestBody pra informar que vou enviar um body
    @PostMapping("/inserir")
    public ResponseEntity<Tarefa> criarNovaTarefa(@RequestBody Tarefa tarefa){
        return ResponseEntity.ok(tarefaRepository.save(tarefa));
    }


    // api para editar uma tarefa
    // link do put
    // preciso informar a anotação @RequestBody pra informar que vou enviar um body
    // e informar o id para saber qual tarefa eu quero editar
    @PutMapping("editar/{id}")
    // Path variable para informar qual id eu quero editar
    public ResponseEntity<Tarefa> editarTarefa(@PathVariable Long id, @RequestBody Tarefa novaTarefa) {
        // recebi um id
        // quero procurar no banco esse id
        return tarefaRepository.findById(id).map(
            // procuro pelo id e o que eu achar eu altero os atributos
            tarefa -> {
                // pego o atributo antigo e coloco o que veio no novo
                tarefa.setTitulo(novaTarefa.getTitulo());
                // coloca todo o resto que veio novo 
                // nova descrição
                tarefa.setDescricao(novaTarefa.getDescricao());
                // novo responsavel e etc
                tarefa.setResponsavel(novaTarefa.getResponsavel());
                tarefa.setDataLimite(novaTarefa.getDataLimite());
                tarefa.setStatus(novaTarefa.getStatus());
                tarefa.setPrioridade(novaTarefa.getPrioridade());
                // gato 200
                return ResponseEntity.ok(tarefaRepository.save(tarefa));
            }
        // se não achar a tarefa retorna que não encontrou nada
        // erro gato 404
        ).orElse(ResponseEntity.notFound().build());
        
    }


    // api para deletar uma tarefa
    // recebe um id pra saber qual tarefa eu quero deletar
    @DeleteMapping("deletar/{id}")
    public ResponseEntity<Tarefa> deletarTarefa(@PathVariable Long id) {
        // verifico se esse id existe no meu banco
        // ! <--- negação 
        // se NÃO existe 
        if(!tarefaRepository.existsById(id)){
            // deu merda, gato 404 - nao encontrou
            return ResponseEntity.notFound().build();
        }

        // vou no banco e deleto só o cara que mandei na URL
        tarefaRepository.deleteById(id);
        // gato 200 - deu bom!!
        return ResponseEntity.ok().build();
    }


    @PostMapping("/{tarefaId}/associar-categoria/{categoriaId}")
    // uma transaction para evitar problemas, se explodir vai parar no meio e nada 
    // vai ser commitado 
    @Transactional
    public ResponseEntity<Void> associarTarefaParaUmaCategoria(
        // passando os id na url : 
        @PathVariable Long tarefaId,
        @PathVariable Long categoriaId
    ){
        // Pega a terefa e a categoria pelos seus ids
        var tarefa = tarefaRepository.findById(tarefaId);
        var categoria = categoriaRepository.findById(categoriaId);
        // se não achar a tarefa ou a categoria retorna que não encontrou nada
        if(tarefa.isEmpty() || categoria.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        // se achar a tarefa e a categoria, eu vou adicionar a categoria na tarefa
        tarefa.get().getCategorias().add(categoria.get());
        // salva no banco
        tarefaRepository.save(tarefa.get());
        // retorna tudo ok 200
        return ResponseEntity.ok().build();
    }

    @GetMapping("/por-titulo/{titulo}")
    public ResponseEntity<List<Tarefa>> buscarPorTitulo(@PathVariable String titulo) {
        return ResponseEntity.ok(tarefaRepository.findByTitulo(titulo));
    }

    @GetMapping("/por-status/{status}")
    public ResponseEntity<List<Tarefa>> buscarPorStatus(@PathVariable Status status) {
        return ResponseEntity.ok(tarefaRepository.findByStatus(status));
    }

    @GetMapping("/por-responsavel/{responsavel}")
    public ResponseEntity<List<Tarefa>> buscarPorResponsavel(@PathVariable String responsavel) {
        return ResponseEntity.ok(tarefaRepository.findByResponsavel(responsavel));
    }

    @GetMapping("/por-prioridade/{prioridade}")
    public ResponseEntity<List<Tarefa>> buscarPorPrioridade(@PathVariable Prioridade prioridade) {
        return ResponseEntity.ok(tarefaRepository.findByPrioridade(prioridade));
    }

    @GetMapping("/vencidas")
    public ResponseEntity<List<Tarefa>> buscarTarefasVencidas() {
        return ResponseEntity.ok(tarefaRepository.findByDataLimiteBefore(LocalDate.now()));
    }
}
