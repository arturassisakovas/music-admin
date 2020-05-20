package com.mAdmin.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mAdmin.entity.Pool;
import com.mAdmin.entity.Season;


public interface PoolRepository extends JpaRepository<Pool, Long> {

    
    @Query("SELECT DISTINCT p.city FROM Pool p")
    List<String> findDistinctCity();

    
    List<Pool> findAllByName(String name);

    
    @Query("select p from Pool p JOIN p.seasons c WHERE c.id = :seasonId")
    List<Pool> findPoolsBySeason(@Param("seasonId") Long id);

    
    List<Pool> findBySeasonsIn(Set<Season> seasons);

    
    List<Pool> findAllByAbbreviation(String abbreviation);

}
