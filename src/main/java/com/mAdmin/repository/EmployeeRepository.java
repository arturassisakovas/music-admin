package com.mAdmin.repository;

import com.mAdmin.entity.Employee;
import com.mAdmin.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    
    Employee findByEmail(String email);

    
    List<Employee> findByIdNotIn(Long id);

    
    @Query("select e from Employee e JOIN e.groups g WHERE g.id = ?1")
    List<Employee> findByGroupId(Long id);

    
    @Query("select e from Employee e JOIN e.roles r WHERE r.id = ?1")
    List<Employee> findByRoleId(Long id);

    
    List<Employee> findByRolesContainingAndEnabledTrue(Role role);

    
    List<Employee> findByRoles(List<Role> roles);

    
    Page<Employee> findByEnabledEqualsOrderByCreatedAtDesc(boolean status, Pageable pageable);

    
    Page<Employee> findByEnabledEquals(boolean enabled, Pageable pageable);
}
