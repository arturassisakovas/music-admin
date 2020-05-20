package com.mAdmin.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;


@Entity
@Table(name = "employees")
public class Employee extends User {

    
    @Column(name = "address")
    private String address;

    
    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "employees")
    private Set<Group> groups = new HashSet<>();

    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "employee_roles",
    joinColumns = { @JoinColumn(name = "employee_id") },
    inverseJoinColumns = { @JoinColumn(name = "role_id") })
    private Collection<Role> roles = new ArrayList<>();

    
    public String getAddress() {
        return address;
    }

    
    public void setAddress(String address) {
        this.address = address;
    }

    
    public Collection<Role> getRoles() {
        return roles;
    }

    
    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    
    public Set<Group> getGroups() {
        return groups;
    }

    
    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    
    public Employee() {
        super();
    }

    
    public String getFullName() {
        return this.getName() + " " + this.getSurname();
    }

    
    public List<String> getAllRoles() {
        List<String> allRoles = new ArrayList<>();
        for (Role role:roles) {
           allRoles.add(role.getName());
        }
        allRoles.sort(Collections.reverseOrder());
        return allRoles;
    }
}
