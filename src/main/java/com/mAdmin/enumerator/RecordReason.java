package com.mAdmin.enumerator;


public enum RecordReason {

    
    BAD_ATTENDANCE("BAD_ATTENDANCE"),

    
    MISSING_DOCUMENTS("MISSING_DOCUMENTS"),

    
    NEW_INFO_REQUIRED("NEW_INFO_REQUIRED"),

    
    FINDING_PLACE("FINDING_PLACE"),

    
    SOLVING_PROBLEM("SOLVING_PROBLEM"),

    
    ACCIDENT("ACCIDENT"),

    
    INVOICE_NOT_PAID("INVOICE_NOT_PAID");

    
    private final String description;

    
    RecordReason(String description) {
        this.description = description;
    }

    
    public String toString() {
        return this.description;
    }
}
