package com.mAdmin.enumerator;


public enum WorkoutsPerWeek {

    
    ONE(1),

    
    TWO(2);

    
    private final int number;

    
    WorkoutsPerWeek(int number) {
        this.number = number;
    }

    
    public int getValue() {
        return number;
    }

    
    public static WorkoutsPerWeek findByTimes(int value) {
        for (WorkoutsPerWeek v : values()) {
            if (v.getValue() == value) {
                return v;
            }
        }
        return null;
    }
}
