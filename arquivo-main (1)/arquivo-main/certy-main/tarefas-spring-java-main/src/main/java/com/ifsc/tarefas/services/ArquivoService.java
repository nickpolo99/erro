package com.ifsc.tarefas.services;

import java.io.File;
import java.io.IOException;
import java.util.Optional;


import org.springframework.core.io.Resource; // Adicionar import
import org.springframework.core.io.UrlResource; // Adicionar import
import org.springframework.http.HttpHeaders; // Adicionar import
import org.springframework.http.ResponseEntity; // Adicionar import
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import comsifscitarefas.model.Arquivoamework.web.bind.annotation.PostMapping;
import comsifscitarefas.model.Tarefa.web.bind.annotation.RequestParam;
import comsifscgtarefas.repository.ArquivoRepositoryk.web.multipart.MultipartFile;
import com.ifsc.tarefas.repository.TarefaRepository;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException; // Adicionar import
import java.nio.file.Path; // Adicionar import
import java.nio.file.Paths; // Adicionar import
import java.util.Optional;

@Controller
public class ArquivoService {

    private final ArquivoRepository arquivoRepository;
    private final TarefaRepository tarefaRepository;
    private final String UPLOAD_DIR = "./uploads/";

    public ArquivoService(ArquivoRepository arquivoRepository, TarefaRepository tarefaRepository) {
        this.arquivoRepository = arquivoRepository;
        this.tarefaRepository = tarefaRepository;
    }

    @GetMapping("/templates/{tarefaId}/anexar-arquivo")
    public String exibirPaginaAnexo(@PathVariable Long tarefaId, Model model) {
        Optional<Tarefa> tarefa = tarefaRepository.findById(tarefaId);
        if (tarefa.isEmpty()) {
            return "redirect:/templates/listar";
        }
        model.addAttribute("tarefa", tarefa.get());
        return "anexar-arquivo";
    }

    @PostMapping("/templates/{tarefaId}/upload")
    public String fazerUpload(@PathVariable Long tarefaId, @RequestParam("file") MultipartFile file, RedirectAttributes attributes) {
        Optional<Tarefa> tarefaOpt = tarefaRepository.findById(tarefaId);
        if (tarefaOpt.isEmpty()) {
            attributes.addFlashAttribute("mensagem", "Erro: Tarefa não encontrada.");
            return "redirect:/templates/listar";
        }
        
        if (file.isEmpty()) {
            attributes.addFlashAttribute("mensagemErro", "Por favor, selecione um arquivo.");
            return "redirect:/templates/" + tarefaId + "/anexar-arquivo";
        }

        try {
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String caminhoDestino = UPLOAD_DIR + file.getOriginalFilename();
            File dest = new File(caminhoDestino);
            file.transferTo(dest);

            Arquivo arquivoInfo = new Arquivo();
            arquivoInfo.setNomeOriginal(file.getOriginalFilename());
            arquivoInfo.setTipoConteudo(file.getContentType());
            arquivoInfo.setCaminhoArquivo(caminhoDestino);
            arquivoInfo.setTarefa(tarefaOpt.get());
            arquivoRepository.save(arquivoInfo);

            attributes.addFlashAttribute("sucesso", "Arquivo '" + file.getOriginalFilename() + "' anexado com sucesso!");

        } catch (IOException e) {
            e.printStackTrace();
            attributes.addFlashAttribute("mensagemErro", "Falha no upload => " + e.getMessage());
        }

        return "redirect:/templates/listar";
    }

    // NOVO MÉTODO PARA REALIZAR O DOWNLOAD
    @GetMapping("/download/arquivo/{arquivoId}")
    public ResponseEntity<Resource> baixarArquivo(@PathVariable Long arquivoId) {
        Optional<Arquivo> arquivoOpt = arquivoRepository.findById(arquivoId);
        if (arquivoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Arquivo arquivo = arquivoOpt.get();
        Path caminhoDoArquivo = Paths.get(arquivo.getCaminhoArquivo());
        
        try {
            Resource resource = new UrlResource(caminhoDoArquivo.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + arquivo.getNomeOriginal() + "\"")
                        .body(resource);
            } else {
                // Lança uma exceção se o arquivo não for encontrado no sistema de arquivos
                throw new RuntimeException("Não foi possível ler o arquivo!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Erro: " + e.getMessage());
        }
    }
}