package com.mAdmin.converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;


@FacesConverter(value = "localDateTimeConverter")
public class LocalDateTimeConverter implements Converter<Object> {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null) {
            return LocalDate.parse(value);
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {

        if (value != null) {
            LocalDate dateValue = (LocalDate) value;

            return dateValue.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        return null;
    }

}
