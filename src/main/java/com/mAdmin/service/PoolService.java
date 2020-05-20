package com.mAdmin.service;

import com.mAdmin.entity.Pool;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface PoolService {

    
    List<Pool> getAll();

    
    Optional<Pool> getById(Long id);

    
    String getTimetables(Pool pool, LocalDate startDate, LocalDate endDate);

    
    List<String> getCities(List<Pool> pools);
    
    
    public List<Pool> getPoolsByCity(List<Pool> pools, String city);

}
