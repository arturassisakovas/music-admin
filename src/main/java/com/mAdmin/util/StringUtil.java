package com.mAdmin.util;

import java.text.BreakIterator;

import org.apache.commons.text.WordUtils;


public final class StringUtil {

    
    public static String formatName(String value) {

        String formattedValue = value.trim().replaceAll("\\s+", " ");
        formattedValue = WordUtils.capitalizeFully(formattedValue);

        return formattedValue;
    }

    
    public static String truncateAfterWords(int n, String s) {

        if (s == null) {
            return null;
        }

        BreakIterator wb = BreakIterator.getWordInstance();
        wb.setText(s);
        int pos = 0;
        for (int i = 0; i < n && pos != BreakIterator.DONE && pos < s.length();) {
            if (Character.isLetter(s.codePointAt(pos))) {
                i++;
            }
            pos = wb.next();
        }

        if (pos == BreakIterator.DONE || pos >= s.length()) {
            return s;
        }

        return s.substring(0, pos);
    }

    
    private StringUtil() {

    }

}
