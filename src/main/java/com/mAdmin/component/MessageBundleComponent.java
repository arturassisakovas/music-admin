package com.mAdmin.component;

import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import java.util.ResourceBundle;


@Component
public class MessageBundleComponent {

    
    public void generateMessage(Severity severity, String message, FacesContext context) {
        ResourceBundle msg = context.getApplication().getResourceBundle(context, "msg");

        context.addMessage(null, new FacesMessage(severity, "", msg.getString(message)));
    }
}
