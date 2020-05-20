package com.mAdmin.repository;

import javax.transaction.Transactional;

import com.mAdmin.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.mAdmin.entity.EmployeePasswordResetToken;


@Repository
public interface EmployeePasswordResetTokenRepository extends JpaRepository<EmployeePasswordResetToken, Long> {

    
    EmployeePasswordResetToken findByToken(String token);

    
    @Transactional
    @Modifying
    void deleteByEmployee(Employee employee);
}
