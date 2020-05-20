package com.mAdmin.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Component
public class DatabaseSeeder {

    
    @Autowired
    private AttendeeSeeder attendeesSeeder;

    
    @Autowired
    private PoolsSeeder poolsSeeder;

    
    @Autowired
    private ContractsSeeder contractsSeeder;

    
    @EventListener
    public void seed(ContextRefreshedEvent event) {
        attendeesSeeder.seedAttendeeTable();
        poolsSeeder.seedPoolsTable();
        contractsSeeder.seedContractsTable();
    }
}
