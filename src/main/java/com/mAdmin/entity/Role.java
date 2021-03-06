package com.mAdmin.entity;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;


@Entity
@Table(name = "roles")
public class Role {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column(name = "name")
    private String name;

    
    @ManyToMany(mappedBy = "roles")
    private Collection<Employee> employees;

    
    public Role() {
        super();
    }

    
    public Role(String name) {
        super();
        this.name = name;
    }

    
    public Long getId() {
        return id;
    }

    
    public void setId(Long id) {
        this.id = id;
    }

    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    
    public Collection<Employee> getEmployees() {
        return employees;
    }

    
    public void setEmployees(Collection<Employee> employees) {
        this.employees = employees;
    }

}
