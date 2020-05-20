package com.mAdmin.view.csv;

import org.primefaces.validate.ClientValidator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Pattern;


@FacesValidator("custom.emailValidator")
public class EmailValidator implements Validator, ClientValidator {

    
    private Pattern pattern;

    
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    
    public EmailValidator() {
        pattern = Pattern.compile(EMAIL_PATTERN);
    }

    @Override
    public Map<String, Object> getMetadata() {
        return null;
    }

    @Override
    public String getValidatorId() {
        return "custom.emailValidator";
    }

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msg");
        String message;

        if (value == null || value.toString().trim().equals("")) {
            message = bundle.getString("client.wronginput.email.empty");
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error",
                    MessageFormat.format(message, value)));
        }

        if (!pattern.matcher(value.toString()).matches()) {
            message = bundle.getString("client.wronginput.email");
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error",
                    MessageFormat.format(message, value)));
        }
    }

}
