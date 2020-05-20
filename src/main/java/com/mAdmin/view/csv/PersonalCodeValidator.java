package com.mAdmin.view.csv;

import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

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


@Component
@FacesValidator("custom.personalCodeValidator")
public class PersonalCodeValidator implements Validator, ClientValidator, ApplicationContextAware {

    
    private Pattern pattern;

    
    private static final String CODE_PATTERN = "^[0-9]{11}$";

    
    public PersonalCodeValidator() {
        pattern = Pattern.compile(CODE_PATTERN);
    }

    @Override
    public Map<String, Object> getMetadata() {
        return null;
    }

    @Override
    public String getValidatorId() {
        return null;
    }

    
    public Pattern getPattern() {
        return pattern;
    }

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msg");

        if (value == null) {
            return;
        }

        if (!pattern.matcher(value.toString()).matches()) {

            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "",
                    bundle.getString("validator.wronginput.personalcode")));
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
