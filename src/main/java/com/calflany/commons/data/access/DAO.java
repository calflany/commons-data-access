package com.calflany.commons.data.access;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.calflany.commons.data.access.specification.Filter;


public interface DAO<T, R> {

    public T findById(long id);

    public List<T> findAll();

    public Page<T> findAll(Pageable pageable);

    public Long count();

    public List<T> getQueryResult(Filter... filters);

    public Page<T> getQueryResult(Pageable pageable, Filter... filters);

    public boolean existsById(Long id);

    public T save(T entity);

    public R getRepository();

}