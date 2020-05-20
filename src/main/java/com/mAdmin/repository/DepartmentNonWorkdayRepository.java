package com.mAdmin.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mAdmin.entity.Pool;
import com.mAdmin.entity.PoolNonWorkday;
import com.mAdmin.entity.Season;


public interface DepartmentNonWorkdayRepository extends JpaRepository<PoolNonWorkday, Long> {

    
    List<PoolNonWorkday> findByPool(Pool pool);

    
    List<PoolNonWorkday> findByPoolAndSeason(Pool pool, Season season);

    
    Optional<PoolNonWorkday> findFirstBySeasonId(Long id);

    
    void deleteByPoolIdAndSeasonId(Long poolId, Long seasonId);

}
