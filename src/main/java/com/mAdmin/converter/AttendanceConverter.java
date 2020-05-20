package com.mAdmin.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;


@FacesConverter(value = "attendanceConverter")
public class AttendanceConverter implements Converter<Object> {

    
    private static final String ATTENDANCE_IS_EMPTY = "0";

    
    private static final String ATTENDANCE_IS_PRESENT = "1";

    
    private static final String ATTENDANCE_IS_ABSENT = "2";

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {

        Boolean presenceValue = null;
        switch (value) {
            case ATTENDANCE_IS_EMPTY:
                return presenceValue;
            case ATTENDANCE_IS_PRESENT:
                presenceValue = true;
                break;
            case ATTENDANCE_IS_ABSENT:
                presenceValue = false;
                break;
            default:
                break;
        }
        return presenceValue;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {

        String presenceString;
        Boolean presenceValue = (Boolean) value;
        if (presenceValue == null) {
            presenceString = ATTENDANCE_IS_EMPTY;
        } else if (presenceValue) {
            presenceString = ATTENDANCE_IS_PRESENT;
        } else {
            presenceString = ATTENDANCE_IS_ABSENT;
        }
        return presenceString;
    }

    
    public static String getAttendanceIsEmpty() {
        return ATTENDANCE_IS_EMPTY;
    }

    
    public static String getAttendanceIsPresent() {
        return ATTENDANCE_IS_PRESENT;
    }

    
    public static String getAttendanceIsAbsent() {
        return ATTENDANCE_IS_ABSENT;
    }
}