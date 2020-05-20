package com.mAdmin.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;


@Entity
@Table(name = "employee_password_reset_tokens")
public class EmployeePasswordResetToken extends PasswordResetToken {

    
    @OneToOne(targetEntity = Employee.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "admin_id")
    private Employee employee;

    
    public Employee getEmployee() {
        return employee;
    }

    
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
