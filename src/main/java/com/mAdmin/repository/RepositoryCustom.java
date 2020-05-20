package com.mAdmin.repository;


public interface RepositoryCustom<T> {

    
    void refresh(T entity);
}
