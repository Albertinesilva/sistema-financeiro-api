package com.swprojects.swfinancialapi.repository.lancamento;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.swprojects.swfinancialapi.model.Lancamento;
import com.swprojects.swfinancialapi.repository.filter.LancamentoFilter;

public interface LancamentoRepositoryQuery {

  public Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable);
}
