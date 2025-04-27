package com.swprojects.swfinancialapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swprojects.swfinancialapi.model.Categoria;
import com.swprojects.swfinancialapi.repository.CategoriaRepository;

@Service
public class CategoriaService {
  
  @Autowired
  private CategoriaRepository categoriaRepository;

  public Categoria salvar(Categoria categoria) {
    return categoriaRepository.save(categoria);
  }

  @Transactional(readOnly = true)
  public List<Categoria> listarTodos() {
    return categoriaRepository.findAll();
  }

  public Optional<Categoria> buscarCategoriaPeloCodigo(Long codigo) {
    return categoriaRepository.findById(codigo);
  }
}
