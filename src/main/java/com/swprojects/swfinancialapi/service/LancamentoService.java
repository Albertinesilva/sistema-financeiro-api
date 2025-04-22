package com.swprojects.swfinancialapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.swprojects.swfinancialapi.model.Lancamento;
import com.swprojects.swfinancialapi.repositorie.LancamentoRepository;

@Service
public class LancamentoService {

  @Autowired
  private LancamentoRepository lancamentoRepository;

  public Lancamento salvar(Lancamento lancamento) {
    return lancamentoRepository.save(lancamento);
  }

  public List<Lancamento> listarTodos() {
    return lancamentoRepository.findAll();
  }

  public Optional<Lancamento> buscarLancamentoPeloCodigo(Long codigo) {
    return lancamentoRepository.findById(codigo);
  }
}
