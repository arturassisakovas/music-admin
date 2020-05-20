package com.mAdmin.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.mAdmin.entity.Client;
import com.mAdmin.entity.Role;


public class ClientPrincipal extends UserPrincipal {

    private static final long serialVersionUID = 1L;

    
    private Client client;

    
    private Role role;

    
    public ClientPrincipal(Client client, Role role) {
        super(client);
        this.setClient(client);
        this.setRole(role);
    }

    
    public Client getClient() {
        return client;
    }

    
    public void setClient(Client client) {
        this.client = client;
    }

    
    public Role getRole() {
        return role;
    }

    
    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new SimpleGrantedAuthority(role.getName()));

        return authorities;
    }

    
    public Long getClientId() {
        return client.getId();
    }
}
