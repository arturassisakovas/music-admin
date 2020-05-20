package com.mAdmin.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mAdmin.repository.PoolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mAdmin.entity.Pool;


@Service
public class PoolServiceImpl implements PoolService {

    
    @Autowired
    private PoolRepository poolRepository;

    
    @Override
    public List<Pool> getAll() {

        return poolRepository.findAll();

    }

    
    @Override
    public Optional<Pool> getById(Long id) {

        return poolRepository.findById(id);

    }

    
    @Override
    public String getTimetables(Pool pool, LocalDate startDate, LocalDate endDate) {
        
        

        return null;

    }

    
    @Override
    public List<String> getCities(List<Pool> pools) {

        List<String> cities = new ArrayList<>();

        if (!pools.isEmpty()) {
            for (Pool pool : pools) {
                if (!cities.contains(pool.getCity())) {
                    cities.add(pool.getCity());
                }
            }
        }
        return cities;
    }

    
    @Override
    public List<Pool> getPoolsByCity(List<Pool> pools, String city) {
        List<Pool> poolsByCity = new ArrayList<Pool>();
        for (Pool pool : pools) {
            if (pool.getCity().equals(city)) {
                poolsByCity.add(pool);
            }
        }
        return poolsByCity;
    }

}
