package com.calflany.commons.data.access.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface EntityRepository<E, I extends Serializable> extends JpaRepositoryImplementation<E, I> {
}
