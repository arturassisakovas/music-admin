package com.mAdmin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mAdmin.entity.Role;


public interface RoleRepository extends JpaRepository<Role, Long> {

    
    Role findByName(String name);

    
    List<Role> findByNameNotIn(String name);

}
