package com.swprojects.swfinancialapi.repository;

import java.util.Optional;

import com.swprojects.swfinancialapi.model.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
  public Optional<Usuario> findByEmail(String email);
}
