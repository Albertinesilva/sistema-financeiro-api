package com.swprojects.swfinancialapi.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swprojects.swfinancialapi.event.RecursoCriadoEvent;
import com.swprojects.swfinancialapi.model.Lancamento;
import com.swprojects.swfinancialapi.service.LancamentoService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoResource {

  @Autowired
  private LancamentoService lancamentoService;

  @Autowired
  private ApplicationEventPublisher publisher;

  @PostMapping
  public ResponseEntity<Lancamento> criar(@Valid @RequestBody Lancamento lancamento, HttpServletResponse response) {
    Lancamento lancamentoSalva = lancamentoService.salvar(lancamento);
    publisher.publishEvent(new RecursoCriadoEvent(this, response, lancamentoSalva.getCodigo()));
    return ResponseEntity.status(HttpStatus.CREATED).body(lancamentoSalva);
  }

  @GetMapping
  public List<Lancamento> listar() {
    return lancamentoService.listarTodos();
  }

  @GetMapping("/{codigo}")
  public ResponseEntity<Lancamento> buscarPeloCodigo(@PathVariable Long codigo) {
    return ResponseEntity.ok(lancamentoService.buscarLancamentoPeloCodigo(codigo));
  }

}
