package com.swprojects.swfinancialapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.swprojects.swfinancialapi.model.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
  
}
