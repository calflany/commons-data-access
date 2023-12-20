package com.calflany.commons.data.access.repository;

import java.io.Serializable;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;


@NoRepositoryBean

public class EntityRepositoryImp<T, I extends Serializable> extends SimpleJpaRepository<T, I> implements EntityRepository<T, I> {

    public EntityRepositoryImp(JpaEntityInformation<T, I> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
   }

}