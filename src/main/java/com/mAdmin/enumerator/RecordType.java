package com.mAdmin.enumerator;


public enum RecordType {

    
    PHONE_CALL("PHONE_CALL"),

    
    EMAIL("EMAIL"),

    
    MEETING("MEETING");

    
    private final String description;

    
    RecordType(String description) {
        this.description = description;
    }

    
    public String toString() {
        return this.description;
    }
}
