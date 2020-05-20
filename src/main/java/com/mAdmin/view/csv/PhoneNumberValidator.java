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


@FacesValidator("custom.phoneNumberValidator")
public class PhoneNumberValidator implements Validator, ClientValidator {

    
    private Pattern pattern;

    
    private static final String PHONE_NUMBER_PATTERN = "^[+]?[\\d]+$";

    
    public PhoneNumberValidator() {
        pattern = Pattern.compile(PHONE_NUMBER_PATTERN);
    }

    @Override
    public Map<String, Object> getMetadata() {
        return null;
    }

    @Override
    public String getValidatorId() {
        return "custom.phoneNumberValidator";
    }

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msg");
        String message;

        if (value == null || value.toString().trim().equals("")) {
            message = bundle.getString("client.wronginput.phone.empty");
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error",
                    MessageFormat.format(message, value)));
        }

        if (!pattern.matcher(value.toString()).matches()) {
            message = bundle.getString("client.wronginput.phone");
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error",
                    MessageFormat.format(message, value)));
        }

    }

}
