package com.mAdmin.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public final class AmountInWordsUtil {

    
    private static final int MAX_INT_LENGTH = 7;

    
    private static final int TEN = 10;

    
    public static String amountInWords(BigDecimal amount) throws RuntimeException {

        boolean negativeNumber = false;

        if (amount.compareTo(BigDecimal.ZERO) == -1) {
            amount = amount.negate();
            negativeNumber = true;
        }

        int amountInt = amount.intValue();
        int amountIntLength = String.valueOf(amountInt).length();

        if (amountIntLength > MAX_INT_LENGTH) {

            throw new RuntimeException("Not implemented for amount " + amount);

        }

        List<String> words = new ArrayList<>();

        if (amountInt == 0) {
            words.add("nulis");
        }

        final String[] units = {"", "vienas", "du", "trys", "keturi", "penki", "šeši", "septyni", "aštuoni", "devyni"};
        final String[] teens = {
                "",
                "vienuolika",
                "dvylika",
                "trylika",
                "keturiolika",
                "penkiolika",
                "šešiolika",
                "septyniolika",
                "aštuoniolika",
                "devyniolika"
        };
        final String[] tens = {
                "",
                "dešimt",
                "dvidešimt",
                "trisdešimt",
                "keturiasdešimt",
                "penkiasdešimt",
                "šešiasdešimt",
                "septyniasdešimt",
                "aštuoniasdešimt",
                "devyniasdešimt"
        };
        final String[][] names = {
                {"milijonas", "milijonai", "milijonų"},
                {"tūkstantis", "tūkstančiai", "tūkstančių"},
        };
        final int ten = 10;
        final int twenty = 20;

        String amountString = String.format("%09d", amountInt);
        String[] amountParts = amountString.split("(?<=\\G.{3})");

        int i = 0;

        for (String triplet : amountParts) {

            int wordCase = 0;

            if (Character.getNumericValue(triplet.charAt(0)) > 0) {

                words.add(units[Character.getNumericValue(triplet.charAt(0))]);

                if (Character.getNumericValue(triplet.charAt(0)) > 1) {
                    words.add("šimtai");
                } else {
                    words.add("šimtas");
                }

            }

            String lastTwoTripletDigits = triplet.substring(1);
            int lastTwoTripletDigitsInt = Integer.parseInt(lastTwoTripletDigits);

            if (lastTwoTripletDigitsInt > ten && lastTwoTripletDigitsInt < twenty) {

                words.add(teens[Character.getNumericValue(lastTwoTripletDigits.charAt(1))]);
                wordCase = 2;

            } else {

                if (Character.getNumericValue(lastTwoTripletDigits.charAt(0)) > 0) {

                    words.add(tens[Character.getNumericValue(lastTwoTripletDigits.charAt(0))]);

                }

                if (Character.getNumericValue(lastTwoTripletDigits.charAt(1)) > 0) {

                    words.add(units[Character.getNumericValue(lastTwoTripletDigits.charAt(1))]);

                    if (Character.getNumericValue(lastTwoTripletDigits.charAt(1)) > 1) {
                        wordCase = 1;
                    } else {
                        wordCase = 0;
                    }

                } else {
                    wordCase = 2;
                }

            }

            if (i < names.length && !triplet.equals("000")) {
                words.add(names[i][wordCase]);
            }

            i++;

        }

        String firstWord = words.get(0);
        firstWord = firstWord.substring(0, 1).toUpperCase() + firstWord.substring(1);

        words.set(0, firstWord);

        String sentence =  String.join(" ", words);
        sentence = sentence.concat(" € ");

        String cents = String.valueOf(amount).split("\\.")[1];

        if (cents.length() == 1) {
            cents = cents.concat("0");
        }

        if (Integer.parseInt(cents) < TEN) {
            cents = cents.substring(1);
        }

        sentence = sentence.concat(cents);
        sentence = sentence.concat(" ct");

        if (negativeNumber) {
            sentence = "- " + sentence;
        }

        return sentence;
    }

    
    private AmountInWordsUtil() {

    }

}
