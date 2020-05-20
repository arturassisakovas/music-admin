package com.mAdmin.view.csv;

import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.primefaces.validate.ClientValidator;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.mAdmin.repository.ClientRepository;


@Component
@FacesValidator("custom.uniqueEmailValidator")
public class UniqueEmailValidator implements Validator<Object>, ClientValidator, ApplicationContextAware {

    
    public UniqueEmailValidator() {
    }

    @Override
    public Map<String, Object> getMetadata() {
        
        return null;
    }

    @Override
    public String getValidatorId() {
        return "custom.uniqueEmailValidator";
    }

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        ResourceBundle msg = context.getApplication().getResourceBundle(context, "msg");


        ClientRepository clientRepository = getBean(ClientRepository.class);

        if (clientRepository.findByEmail(value.toString()) != null) {


            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", msg.getString("client.duplicate.email")));
        }

    }

    
    private static ApplicationContext ctx;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        ctx = applicationContext;

    }

    
    public static <T> T getBean(Class<T> beanClass) {
        return ctx.getBean(beanClass);
    }

}
