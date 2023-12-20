package com.calflany.commons.data.access;

import java.util.List;

public interface EntityMapper<D, E> { 

    public D toDto(E entity); 

    public E toEntity(D dto); 

    public List<D> toDtos(List<E> entities); 

    public List<E> toEntities(List<D> dtos);

}