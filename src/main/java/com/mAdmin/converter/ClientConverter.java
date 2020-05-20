package com.mAdmin.converter;

import java.util.Optional;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mAdmin.entity.Client;
import com.mAdmin.service.ClientService;


@Component
public class ClientConverter implements Converter {

    
    @Autowired
    private ClientService clientService;

	
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if (value != null) {
            if (value.trim().length() > 0) {
                try {
                    Optional<Client> gettingClient = clientService.getClientById(Long.parseLong(value));
					return gettingClient.orElse(null);
                } catch (NumberFormatException e) {
                    throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Conversion Error", "Not a valid client."));
                }
            } else {
				return null;
			}
        } else {
            return null;
        }
    }

	
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
		if (object != null) {
			return String.valueOf(((Client) object).getId());
		} else {
			return null;
		}
	}
}
