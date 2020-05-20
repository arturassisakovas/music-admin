package com.mAdmin.enumerator;


public enum RecordStatus {

    
    NOT_STARTED("NOT_STARTED"),

    
    IN_PROGRESS("IN_PROGRESS"),

    
    COMPLETED("COMPLETED");

    
    private final String description;

    
    RecordStatus(String description) {
        this.description = description;
    }

    
    public String toString() {
        return this.description;
    }
}
