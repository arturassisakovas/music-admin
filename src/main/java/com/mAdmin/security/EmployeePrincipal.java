package com.mAdmin.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.mAdmin.entity.Employee;
import com.mAdmin.entity.Role;


public class EmployeePrincipal extends UserPrincipal {

    private static final long serialVersionUID = 1L;

    
    private Employee employee;

    
    public EmployeePrincipal(Employee employee) {
        super(employee);
        this.setEmployee(employee);
    }

    
    public Employee getEmployee() {
        return employee;
    }

    
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<GrantedAuthority> authorities = new ArrayList<>();

        for (Role role : employee.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }

        return authorities;

    }

    
    public Long getEmployeeId() {
        return employee.getId();
    }

}
