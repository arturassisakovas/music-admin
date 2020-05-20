package com.mAdmin.converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;


@FacesConverter(value = "localDateTimeConverterWithoutSeconds")
public class LocalDateTimeConverterWithoutSeconds implements Converter<Object> {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {

        LocalDateTime dateValue = (LocalDateTime) value;

        return dateValue.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

}
