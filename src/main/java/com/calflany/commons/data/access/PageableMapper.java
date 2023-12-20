package com.calflany.commons.data.access;

import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;

 
public interface PageableMapper<D, E> extends EntityMapper<D, E> {

    public Page<D> toDtos(Pageable pageable, Page<E> entities, long total);

    public Page<E> toEntities(Pageable pageable, Page<D> dtos, long total);

}
