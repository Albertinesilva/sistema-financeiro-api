package com.swprojects.swfinancialapi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swprojects.swfinancialapi.model.Lancamento;
import com.swprojects.swfinancialapi.model.Pessoa;
import com.swprojects.swfinancialapi.repositorie.LancamentoRepository;
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
}
