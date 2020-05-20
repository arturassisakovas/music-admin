package com.mAdmin.service;

import java.util.List;

import com.mAdmin.entity.Employee;


public interface EmployeeService extends UserService {

    
    List<Employee> getByRoleId(Long id);

}
