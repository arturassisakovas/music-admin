package com.mAdmin.service;

import java.util.List;

import com.mAdmin.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mAdmin.entity.Employee;
import com.mAdmin.entity.User;


@Service
public class EmployeeServiceImpl extends UserServiceImpl implements EmployeeService {

    
    @Autowired
    private PasswordEncoder passwordEncoder;

    
    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public boolean changePassword(User user, String rawPassword) {

        user.setPassword(passwordEncoder.encode(rawPassword));

        if (employeeRepository.save((Employee) user) == null) {
            return false;
        }

        return true;

    }

    
    @Override
    public List<Employee> getByRoleId(Long id) {
       return employeeRepository.findByRoleId(id);
    }

}
