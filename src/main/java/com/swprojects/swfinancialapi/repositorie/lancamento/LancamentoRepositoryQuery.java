package com.swprojects.swfinancialapi.repositorie.lancamento;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.swprojects.swfinancialapi.model.Lancamento;
import com.swprojects.swfinancialapi.repositorie.filter.LancamentoFilter;

public interface LancamentoRepositoryQuery {

  public Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable);
}
