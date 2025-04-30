package com.swprojects.swfinancialapi.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swprojects.swfinancialapi.model.Pessoa;
import com.swprojects.swfinancialapi.repository.PessoaRepository;

@Service
public class PessoaService {

  @Autowired
  private PessoaRepository pessoaRepository;

  @Transactional(readOnly = false)
  public Pessoa salvar(Pessoa pessoa) {
    return pessoaRepository.save(pessoa);
  }

  @Transactional(readOnly = false)
  public Pessoa atualizar(Long codigo, Pessoa pessoa) {
    Pessoa pessoaSalva = buscarPessoaPeloCodigo(codigo);
    BeanUtils.copyProperties(pessoa, pessoaSalva, "codigo");
    return pessoaRepository.save(pessoaSalva);
  }

  @Transactional(readOnly = false)
  public void atualizarPropriedadeAtivo(Long codigo, Boolean ativo) {
    Pessoa pessoa = buscarPessoaPeloCodigo(codigo);
    pessoa.setAtivo(ativo);
    pessoaRepository.save(pessoa);
  }

  @Transactional(readOnly = true)
  public Pessoa buscarPessoaPeloCodigo(Long codigo) {
    return pessoaRepository.findById(codigo)
        .orElseThrow(() -> new EmptyResultDataAccessException("Pessoa não encontrada", 1));
  }

  @Transactional(readOnly = false)
  public void remover(Long codigo) {
    Pessoa p = pessoaRepository.findById(codigo)
        .orElseThrow(() -> new EmptyResultDataAccessException("Pessoa não encontrada", 1));
    pessoaRepository.delete(p);
  }

  @Transactional(readOnly = true)
  public Page<Pessoa> findByNomeContaining(String nome, Pageable pageable) {
    return pessoaRepository.findByNomeContaining(nome, pageable);
  }
}
