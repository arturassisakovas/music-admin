package com.mAdmin.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;


public class RepositoryCustomImpl implements RepositoryCustom<Object> {

    
    @PersistenceContext
    private EntityManager em;

    
    @Override
    @Transactional
    public void refresh(Object entity) {
        em.refresh(entity);

    }
}
