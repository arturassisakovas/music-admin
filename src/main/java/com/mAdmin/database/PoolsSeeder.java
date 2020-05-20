package com.mAdmin.database;

import java.util.List;

import com.mAdmin.repository.PoolRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mAdmin.entity.Pool;


@Component
public class PoolsSeeder {

    
    @Autowired
    private PoolRepository poolRepository;

    
    private final Logger logger = LoggerFactory.getLogger(PoolsSeeder.class);

    
     protected void seedPoolsTable() {

        List<Pool> pools = poolRepository.findAll();
        final String loggerText = Pool.class.getSimpleName() + " table seeding is not required";

        if (pools.isEmpty()) {
            logger.info(loggerText);
            return;
        }

        pools.forEach(i -> {
            if (i.getAbbreviation() == null) {
                i.setAbbreviation(i.getName());
            }
        });

        poolRepository.saveAll(pools);
     }
}
