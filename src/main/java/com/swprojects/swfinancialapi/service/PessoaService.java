package com.swprojects.swfinancialapi.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.swprojects.swfinancialapi.model.Pessoa;
import com.swprojects.swfinancialapi.repositorie.PessoaRepository;

@Service
public class PessoaService {

  @Autowired
  private PessoaRepository pessoaRepository;

  public Pessoa atualizar(Long codigo, Pessoa pessoa) {
    Pessoa pessoaSalva = pessoaRepository.findById(codigo)
        .orElseThrow(() -> new EmptyResultDataAccessException("Pessoa não encontrada", 1));
    BeanUtils.copyProperties(pessoa, pessoaSalva, "codigo");
    return pessoaRepository.save(pessoaSalva);
  }

  public void atualizarPropriedadeAtivo(Long codigo, Boolean ativo) {
    Pessoa pessoaSalva = buscarPessoaPeloCodigo(codigo);
    pessoaSalva.setAtivo(ativo);
    pessoaRepository.save(pessoaSalva);
  }

  private Pessoa buscarPessoaPeloCodigo(Long codigo) {
    return pessoaRepository.findById(codigo)
        .orElseThrow(() -> new EmptyResultDataAccessException("Pessoa não encontrada", 1));
  }
}
