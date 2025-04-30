package com.swprojects.swfinancialapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swprojects.swfinancialapi.model.Lancamento;
import com.swprojects.swfinancialapi.model.Pessoa;
import com.swprojects.swfinancialapi.repository.LancamentoRepository;
import com.swprojects.swfinancialapi.repository.filter.LancamentoFilter;
import com.swprojects.swfinancialapi.repository.projection.ResumoLancamento;
import com.swprojects.swfinancialapi.service.exception.PessoaInexistenteOuInativaException;

@Service
public class LancamentoService {

  @Autowired
  private LancamentoRepository lancamentoRepository;

  @Autowired
  private PessoaService pessoaService;

  @Transactional(readOnly = false)
  public Lancamento salvar(Lancamento lancamento) {
    Pessoa pessoa = pessoaService.buscarPessoaPeloCodigo(lancamento.getPessoa().getCodigo());
    if (pessoa == null || pessoa.isInativo()) {
      throw new PessoaInexistenteOuInativaException();
    }
    return lancamentoRepository.save(lancamento);
  }

  @Transactional(readOnly = true)
  public List<Lancamento> listarTodos() {
    return lancamentoRepository.findAll();
  }

  @Transactional(readOnly = true)
  public Lancamento buscarLancamentoPeloCodigo(Long codigo) {
    return lancamentoRepository.findById(codigo)
        .orElseThrow(() -> new EmptyResultDataAccessException("Lancamento não encontrada", 1));
  }

  @Transactional(readOnly = true)
  public Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable) {
    return lancamentoRepository.filtrar(lancamentoFilter, pageable);
  }

  @Transactional(readOnly = true)
  public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable) {
    return lancamentoRepository.resumir(lancamentoFilter, pageable);
  }

  public void remover(Long codigo) {
    Lancamento lancamento = lancamentoRepository.findById(codigo)
        .orElseThrow(() -> new EmptyResultDataAccessException("Pessoa não encontrada", 1));
    lancamentoRepository.deleteById(lancamento.getCodigo());
  }

  @Transactional(readOnly = false)
  public Lancamento atualizar(Long codigo, Lancamento lancamento) {
    Lancamento lancamentoSalvo = buscarLancamentoExistente(codigo);
    if (!lancamento.getPessoa().equals(lancamentoSalvo.getPessoa())) {
      validarPessoa(lancamento);
    }

    BeanUtils.copyProperties(lancamento, lancamentoSalvo, "codigo");
    return lancamentoRepository.save(lancamentoSalvo);
  }

  private void validarPessoa(Lancamento lancamento) {
    Optional<Pessoa> pessoa = Optional.ofNullable(
        lancamento.getPessoa().getCodigo() != null
            ? pessoaService.buscarPessoaPeloCodigo(lancamento.getPessoa().getCodigo())
            : null);

    if (pessoa.isEmpty() || pessoa.get().isInativo()) {
      throw new PessoaInexistenteOuInativaException();
    }
  }

  private Lancamento buscarLancamentoExistente(Long codigo) {

    // Optional<Lancamento> lancamentoSalvo = lancamentoRepository.findById(codigo);
    // if (lancamentoSalvo.isEmpty()) {
    // throw new IllegalArgumentException();
    // }

    return lancamentoRepository.findById(codigo).orElseThrow(() -> new IllegalArgumentException());
  }

}
