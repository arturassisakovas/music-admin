package com.mAdmin.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;

import com.mAdmin.component.AuthenticationFacade;
import com.mAdmin.entity.User;
import com.mAdmin.security.UserPrincipal;


@Controller
@Scope(value = "session")
public class AuthenticationController {

    
    @Autowired
    private AuthenticationFacade authenticationFacade;

    
    public User getUser() {
        return getUserPrincipal().getUser();
    }

    
    public String getFullName() {

        return getUser().getName() + " " + getUser().getSurname();

    }

    
    private UserPrincipal getUserPrincipal() {
        return (UserPrincipal) authenticationFacade.getAuthentication().getPrincipal();
    }

    
    public Collection<? extends GrantedAuthority> getUserAuthorities() {
        return getUserPrincipal().getAuthorities();
    }

    
    public String getUserType() {
        if (getUserAuthorities().contains(new SimpleGrantedAuthority("ROLE_SADMIN"))
                || getUserAuthorities().contains(new SimpleGrantedAuthority("ROLE_MANAGER"))) {
            return "admin";
        } else if (getUserAuthorities().contains(new SimpleGrantedAuthority("ROLE_COACH"))) {
            return "coach";
        } else if (getUserAuthorities().contains(new SimpleGrantedAuthority("ROLE_CLIENT"))) {
            return "client";
        }
        return null;
    }

}
