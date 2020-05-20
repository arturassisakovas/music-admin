package com.mAdmin.controller;

import com.mAdmin.component.AuthenticationSuccessHandlerImpl;
import com.mAdmin.component.AuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Controller;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;


@Controller
@Scope(value = "request")
public class LoginController {

    
    @Autowired
    private AuthenticationFacade authenticationFacade;

    
    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    
    private String email, password;

    
    public void onLoad() {

        final Authentication authentication = authenticationFacade.getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {

            final FacesContext facesContext = FacesContext.getCurrentInstance();
            final ExternalContext externalContext = facesContext.getExternalContext();

            HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
            HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();

            try {
                authenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication);
            } catch (ServletException | IOException e) {
                e.printStackTrace();
            }

        }

    }

    
    public void redirectToRegistration() throws IOException {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        session.setAttribute("redirectUrlAfterLoginSuccess", "/registration/step-1");

        FacesContext.getCurrentInstance().getExternalContext().redirect("/login");
    }

    
    public void redirectToRegistrationStepOne() throws IOException {
        FacesContext.getCurrentInstance().getExternalContext().redirect("/registration/step-1");
    }

    
    public void clearSession() {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, Object> sessionMap = externalContext.getSessionMap();
        sessionMap.clear();
    }

    
    public String getEmail() {
        return email;
    }

    
    public void setEmail(String email) {
        this.email = email;
    }

    
    public String getPassword() {
        return password;
    }

    
    public void setPassword(String password) {
        this.password = password;
    }
}
