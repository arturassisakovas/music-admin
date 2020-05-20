package com.mAdmin.component;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;


@Component
public class LocalDateTimeProvider {

    
    public LocalDateTime getCurrentDateTime() {
        return now();
    }
}
