package com.mAdmin.util;

import java.time.Duration;
import java.time.LocalTime;


public final class TimeUtil {

    
   static final int ZERO = 0;

    
   static final int ONE = 1;

    
   static final int TWO = 2;

    
   static final int THREE = 3;

    
   static final int FOUR = 4;

    
   static final int FIVE = 5;

    
   static final int SIX = 6;

    
    public static String minutesToTimeConverter(int minutes) {
        String time = LocalTime.MIN.plus(Duration.ofMinutes(minutes)).toString();
        return time;
    }
    
    public static String dayOfWeekInRoman(int dayOrdinal) {
        switch (dayOrdinal) {
        case ZERO:
            return "I";
        case ONE:
            return "II";
        case TWO:
            return "III";
        case THREE:
            return "IV";
        case FOUR:
            return "V";
        case FIVE:
            return "VI";
        case SIX:
            return "VII";
        default :
            return "Unknown day";
        }
    }

    
    private TimeUtil() {

    }

}
