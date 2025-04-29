package com.swprojects.swfinancialapi.resource;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.swprojects.swfinancialapi.event.RecursoCriadoEvent;
import com.swprojects.swfinancialapi.exceptionhandler.SwFinancialExceptionHandler.ErroResponse;
import com.swprojects.swfinancialapi.model.Lancamento;
import com.swprojects.swfinancialapi.repository.filter.LancamentoFilter;
import com.swprojects.swfinancialapi.repository.projection.ResumoLancamento;
import com.swprojects.swfinancialapi.service.LancamentoService;
import com.swprojects.swfinancialapi.service.exception.PessoaInexistenteOuInativaException;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoResource {

  @Autowired
  private LancamentoService lancamentoService;

  @Autowired
  private ApplicationEventPublisher publisher;

  @Autowired
  private MessageSource messageSource;

  @GetMapping
  @PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and hasAuthority('SCOPE_read')")
  public Page<Lancamento> pesquisar(LancamentoFilter lancamentoFilter, Pageable pageable) {
    return lancamentoService.filtrar(lancamentoFilter, pageable);
  }

  @GetMapping(params = "resumo")
  @PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and hasAuthority('SCOPE_read')")
  public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable) {
    return lancamentoService.resumir(lancamentoFilter, pageable);
  }

  @GetMapping("/{codigo}")
  @PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and hasAuthority('SCOPE_read')")
  public ResponseEntity<Lancamento> buscarPeloCodigo(@PathVariable Long codigo) {
    return ResponseEntity.ok(lancamentoService.buscarLancamentoPeloCodigo(codigo));
  }

  @PostMapping
  @PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and hasAuthority('SCOPE_write')")
  public ResponseEntity<Lancamento> criar(@Valid @RequestBody Lancamento lancamento, HttpServletResponse response) {
    Lancamento lancamentoSalva = lancamentoService.salvar(lancamento);
    publisher.publishEvent(new RecursoCriadoEvent(this, response, lancamentoSalva.getCodigo()));
    return ResponseEntity.status(HttpStatus.CREATED).body(lancamentoSalva);
  }

  @DeleteMapping("/{codigo}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasAuthority('ROLE_REMOVER_LANCAMENTO') and hasAuthority('SCOPE_write')")
  public void remover(@PathVariable Long codigo) {
    lancamentoService.remover(codigo);
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
