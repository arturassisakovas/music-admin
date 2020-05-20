package com.mAdmin.database;

import java.util.List;

import com.mAdmin.repository.ContractRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mAdmin.entity.Contract;
import com.mAdmin.entity.Pool;


@Component
public class ContractsSeeder {

    
    @Autowired
    private ContractRepository contractRepository;

    
    private final Logger logger = LoggerFactory.getLogger(ContractsSeeder.class);

    
    protected void seedContractsTable() {
        List<Contract> contracts = contractRepository.findAll();
        final String loggerText = Pool.class.getSimpleName() + " table seeding is not required";
        if (contracts.isEmpty()) {
            logger.info(loggerText);
            return;
        }
        contracts.forEach(c -> {
            if (c.getValidFrom() == null) {
                c.setValidFrom(c.getCreatedAt().toLocalDate());
            }
            if (c.getValidTo() == null) {
                c.setValidTo(c.getAttendee().getGroup().getEndDate());
            }
        });
        contractRepository.saveAll(contracts);
    }
}
