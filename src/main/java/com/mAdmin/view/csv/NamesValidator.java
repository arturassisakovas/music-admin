package com.mAdmin.view.csv;

import java.util.Map;
import java.util.regex.Pattern;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.primefaces.validate.ClientValidator;


public class NamesValidator implements Validator, ClientValidator {

    
    private Pattern pattern;

    
    private static final String NAME_PATTERN = "^(?=.*\\p{L})[\\p{L}'\\s]*$";

    
    public NamesValidator() {
        pattern = Pattern.compile(NAME_PATTERN);
    }

    @Override
    public Map<String, Object> getMetadata() {
        return null;
    }

    @Override
    public String getValidatorId() {
        return null;
    }

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
    }

    
    public Pattern getPattern() {
        return pattern;
    }

}
