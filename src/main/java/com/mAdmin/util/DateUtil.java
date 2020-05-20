package com.mAdmin.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public final class DateUtil {

    
    public static boolean isBeforeOrEqual(LocalDate dateX, LocalDate dateY) {
        if (dateX.isBefore(dateY) || dateX.isEqual(dateY)) {
            return true;
        }

        return false;
    }

    
    public static boolean isAfterOrEqual(LocalDate dateX, LocalDate dateY) {
        return isBeforeOrEqual(dateY, dateX);
    }

    
    public static LocalDate convertLocalDateTime(LocalDateTime localDateTime) {
        return localDateTime.toLocalDate();
    }

    
    public static boolean todayIsHoliday(List<String> dynamicHolidays, List<String> staticHolidays) {
        LocalDate today = LocalDate.now().minusDays(1);
        StringBuilder str = new StringBuilder();
        str.append(Integer.toString(today.getMonthValue()));
        str.append("-");
        str.append(Integer.toString(today.getDayOfMonth()));

        return (dynamicHolidays.contains(LocalDate.now().toString()) || staticHolidays.contains(str.toString()));
    }

    
    private DateUtil() {

    }

}
