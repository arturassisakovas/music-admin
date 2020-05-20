package com.mAdmin.view.csv;

import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.ValidatorException;


@FacesValidator("custom.surnameValidator")
public class SurnameValidator extends NamesValidator {

    @Override
    public String getValidatorId() {
        return "custom.surnameValidator";
    }

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        if (value == null) {
            return;
        }

        if (!getPattern().matcher(value.toString()).matches()) {

            ResourceBundle bundle = context.getApplication().getResourceBundle(context, "label");
            String message = bundle.getString("user.wronginput.surname");

            throw new ValidatorException(
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", message));
        }

    }

}
