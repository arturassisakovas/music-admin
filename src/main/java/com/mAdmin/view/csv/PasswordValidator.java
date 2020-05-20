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


@FacesValidator("custom.passwordValidator")
public class PasswordValidator implements Validator, ClientValidator {

    
    private Pattern pattern;

    
    private static final String PASSWORD_PATTERN =
            "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?!.*[\\'\\\"\\<\\>\\|\\\\\\/\\{\\}\\[\\]\\:\\;\\,\\.\\?\\s]).*$";

    
    public PasswordValidator() {
        pattern = Pattern.compile(PASSWORD_PATTERN);
    }

    @Override
    public Map<String, Object> getMetadata() {
        return null;
    }

    @Override
    public String getValidatorId() {
        return "custom.passwordValidator";
    }

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        if (value == null) {
            return;
        }

        ResourceBundle msg = context.getApplication().getResourceBundle(context, "msg");

        if (!pattern.matcher(value.toString()).matches()) {
            throw new ValidatorException(
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "", msg.getString("password.notvalid")));
        }

    };



}
