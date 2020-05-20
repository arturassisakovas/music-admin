package com.mAdmin.controller;

import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;


@Controller
@Scope(value = "session")
public class LocaleController {

    
    private Locale locale;

    
    @PostConstruct
    public void init() {
        locale = FacesContext.getCurrentInstance().getApplication().getDefaultLocale();
    }

    
    public Locale getLocale() {
        return locale;
    }

    
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

}
