package com.mAdmin.enumerator;


public enum AgeGroup {

    
    FIRST("5-6"),

    
    SECOND("7-8"),

    
    THIRD("9-10"),

    
    FOURTH("11-12");

    
    private final String value;

    
    AgeGroup(String value) {
        this.value = value;
    }

    
    public String getValue() {
        return value;
    }

    
    public static AgeGroup findByGroup(String group) {
        for (AgeGroup g : values()) {
            if (g.getValue().equalsIgnoreCase(group)) {
                return g;
            }
        }
        return null;
    }
}
