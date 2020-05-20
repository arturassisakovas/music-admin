package com.mAdmin.service;

import com.mAdmin.repository.ClientRepository;
import com.mAdmin.repository.EmployeeRepository;
import com.mAdmin.repository.RoleRepository;
import com.mAdmin.security.ClientPrincipal;
import com.mAdmin.security.EmployeePrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mAdmin.entity.Client;
import com.mAdmin.entity.Employee;
import com.mAdmin.entity.Role;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    
    private static final String ROLE_CLIENT = "ROLE_CLIENT";

    
    @Autowired
    private EmployeeRepository employeeRepository;

    
    @Autowired
    private ClientRepository clientRepository;

    
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return loadUserByEmail(username);

    }

    
    public UserDetails loadUserByEmail(String email) {

        Employee employee = employeeRepository.findByEmail(email);

        if (employee == null) {
            Client client = clientRepository.findByEmail(email);

            if (client == null) {
                throw new UsernameNotFoundException(email);
            }

            Role clientRole = roleRepository.findByName(ROLE_CLIENT);

            return new ClientPrincipal(client, clientRole);
        }

        return new EmployeePrincipal(employee);
    }

}
