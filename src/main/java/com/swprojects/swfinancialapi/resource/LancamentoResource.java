package com.swprojects.swfinancialapi.resource;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swprojects.swfinancialapi.event.RecursoCriadoEvent;
import com.swprojects.swfinancialapi.exceptionhandler.SwFinancialExceptionHandler.ErroResponse;
import com.swprojects.swfinancialapi.model.Lancamento;
import com.swprojects.swfinancialapi.repositorie.filter.LancamentoFilter;
import com.swprojects.swfinancialapi.service.LancamentoService;
import com.swprojects.swfinancialapi.service.exception.PessoaInexistenteOuInativaException;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoResource {

  @Autowired
  private LancamentoService lancamentoService;

  @Autowired
  private ApplicationEventPublisher publisher;

  @Autowired
  private MessageSource messageSource;

  @PostMapping
  public ResponseEntity<Lancamento> criar(@Valid @RequestBody Lancamento lancamento, HttpServletResponse response) {
    Lancamento lancamentoSalva = lancamentoService.salvar(lancamento);
    publisher.publishEvent(new RecursoCriadoEvent(this, response, lancamentoSalva.getCodigo()));
    return ResponseEntity.status(HttpStatus.CREATED).body(lancamentoSalva);
  }

  @GetMapping
  public List<Lancamento> pesquisar(LancamentoFilter lancamentoFilter) {
    return lancamentoService.filtrar(lancamentoFilter);
  }

  @GetMapping("/{codigo}")
  public ResponseEntity<Lancamento> buscarPeloCodigo(@PathVariable Long codigo) {
    return ResponseEntity.ok(lancamentoService.buscarLancamentoPeloCodigo(codigo));
  }

  @ExceptionHandler({ PessoaInexistenteOuInativaException.class })
  public ResponseEntity<Object> handlePessoaInexistenteOuInativaException(PessoaInexistenteOuInativaException ex) {
    String mensagemUsuario = messageSource.getMessage("pessoa.inexistente-ou-inativa", null,
        LocaleContextHolder.getLocale());
    String mensagemDesenvolvedor = Optional.ofNullable(ex.getCause()).orElse(ex).toString();
    List<ErroResponse> erros = Arrays.asList(new ErroResponse(mensagemUsuario, mensagemDesenvolvedor));
    return ResponseEntity.badRequest().body(erros);
  }

}
