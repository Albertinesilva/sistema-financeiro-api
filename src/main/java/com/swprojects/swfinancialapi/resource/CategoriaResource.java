package com.swprojects.swfinancialapi.resource;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swprojects.swfinancialapi.event.RecursoCriadoEvent;
import com.swprojects.swfinancialapi.model.Categoria;
import com.swprojects.swfinancialapi.service.CategoriaService;

@RestController
@RequestMapping("/categorias")
public class CategoriaResource {

  @Autowired
  private CategoriaService categoriaService;

  @Autowired
  private ApplicationEventPublisher publisher;

  @GetMapping
  @PreAuthorize("hasAuthority('ROLE_PESQUISAR_CATEGORIA') and hasAuthority('SCOPE_read')")
  public List<Categoria> listar() {
    return categoriaService.listarTodos();
  }

  @PostMapping
  @PreAuthorize("hasAuthority('ROLE_CADASTRAR_CATEGORIA') and hasAuthority('SCOPE_write')")
  public ResponseEntity<Categoria> criar(@Valid @RequestBody Categoria categoria, HttpServletResponse response) {
    Categoria novaCategoria = categoriaService.salvar(categoria);
    publisher.publishEvent(new RecursoCriadoEvent(this, response, novaCategoria.getCodigo()));
    return ResponseEntity.status(HttpStatus.CREATED).body(novaCategoria);
  }

  @GetMapping("/{codigo}")
  @PreAuthorize("hasAuthority('ROLE_PESQUISAR_CATEGORIA') and hasAuthority('SCOPE_read')")
  public ResponseEntity<Categoria> buscarPeloCodigo(@PathVariable Long codigo) {
    return categoriaService.buscarCategoriaPeloCodigo(codigo)
        .map(categoria -> ResponseEntity.ok(categoria))
        .orElse(ResponseEntity.notFound().build());
  }
}
