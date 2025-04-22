package com.swprojects.swfinancialapi.repositorie.lancamento;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.ObjectUtils;

import com.swprojects.swfinancialapi.model.Lancamento;
import com.swprojects.swfinancialapi.model.Lancamento_;
import com.swprojects.swfinancialapi.repositorie.filter.LancamentoFilter;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class LancamentoRepositoryImpl implements LancamentoRepositoryQuery {

  @PersistenceContext
  private EntityManager manager;

  @Override
  public List<Lancamento> filtrar(LancamentoFilter lancamentoFilter) {
    CriteriaBuilder builder = manager.getCriteriaBuilder();
    CriteriaQuery<Lancamento> criteria = builder.createQuery(Lancamento.class);
    Root<Lancamento> root = criteria.from(Lancamento.class);

    Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);
    criteria.where(predicates);

    TypedQuery<Lancamento> query = manager.createQuery(criteria);
    return query.getResultList();
  }

  private Predicate[] criarRestricoes(LancamentoFilter lancamentoFilter, CriteriaBuilder builder,
      Root<Lancamento> root) {
    List<Predicate> predicates = new ArrayList<>();

    if (!ObjectUtils.isEmpty(lancamentoFilter.getDescricao())) {
      predicates.add(builder.like(
          builder.lower(root.get(Lancamento_.DESCRICAO)), "%" + lancamentoFilter.getDescricao().toLowerCase() + "%"));
    }

    if (lancamentoFilter.getDataVencimentoDe() != null) {
      predicates.add(
          builder.greaterThanOrEqualTo(root.get(Lancamento_.DATA_VENCIMENTO), lancamentoFilter.getDataVencimentoDe()));
    }

    if (lancamentoFilter.getDataVencimentoAte() != null) {
      predicates.add(
          builder.lessThanOrEqualTo(root.get(Lancamento_.DATA_VENCIMENTO), lancamentoFilter.getDataVencimentoAte()));
    }

    return predicates.toArray(new Predicate[predicates.size()]);
  }

}
