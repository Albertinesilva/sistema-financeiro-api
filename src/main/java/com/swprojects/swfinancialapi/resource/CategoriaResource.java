package com.swprojects.swfinancialapi.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swprojects.swfinancialapi.event.RecursoCriadoEvent;
import com.swprojects.swfinancialapi.model.Categoria;
import com.swprojects.swfinancialapi.repositories.CategoriaRepository;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/categorias")
public class CategoriaResource {

  @Autowired
  private CategoriaRepository categoriaRepository;

  @Autowired
  private ApplicationEventPublisher publisher;

  @GetMapping
  public List<Categoria> listar() {
    return categoriaRepository.findAll();
  }

  @PostMapping
  public ResponseEntity<Categoria> adicionar(@Valid @RequestBody Categoria categoria, HttpServletResponse response) {
    Categoria novaCategoria = categoriaRepository.save(categoria);
    publisher.publishEvent(new RecursoCriadoEvent(this, response, novaCategoria.getCodigo()));
    return ResponseEntity.status(HttpStatus.CREATED).body(novaCategoria);
  }

  @GetMapping("/{codigo}")
  public ResponseEntity<Categoria> buscarPeloCodigo(@PathVariable Long codigo) {
    return categoriaRepository.findById(codigo)
        .map(categoria -> ResponseEntity.ok(categoria))
        .orElse(ResponseEntity.notFound().build());
  }
}
