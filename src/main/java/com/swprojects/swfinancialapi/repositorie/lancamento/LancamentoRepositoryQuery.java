package com.swprojects.swfinancialapi.repositorie.lancamento;

import java.util.List;

import com.swprojects.swfinancialapi.model.Lancamento;
import com.swprojects.swfinancialapi.repositorie.filter.LancamentoFilter;

public interface LancamentoRepositoryQuery {

  public List<Lancamento> filtrar(LancamentoFilter lancamentoFilter);
}
