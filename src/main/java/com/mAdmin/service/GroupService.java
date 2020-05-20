package com.mAdmin.service;

import com.mAdmin.enumerator.SwimmingLevel;
import com.mAdmin.repository.GroupRepository;
import com.mAdmin.entity.Group;
import com.mAdmin.entity.Period;
import com.mAdmin.entity.Pool;
import org.primefaces.model.SortOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public interface GroupService {

    
    boolean isAvailable();

    
    List<Group> getAllAvailable();

    
    List<Group> getAll();

    
    Optional<Group> getById(Long id);

    
    int getAvailableGroupSpaces(Group group);

    
    List<Group> filterByAgeGroup(List<Group> groups, LocalDate birthdate);

    
    List<Group> filterBySwimmingLevel(List<Group> groups, SwimmingLevel swimmingLvl);

    
    List<Group> filterByWorkoutsPerWeek(List<Group> groups, int workoutsPerWeek);

    
    List<Group> filterByPool(List<Group> groups, Pool pool);

    
    List<Group> filterByPool(List<Group> groups, Long poolId);

    
    BigDecimal getSingleWorkoutPrice(Group group);

    
    Long getGroupPeriodId(Group group);

    
    Period getGroupPeriod(Group group);

    
    List<Group> loadForLazyDataModel(int first, int pageSize, String sortField, SortOrder sortOrder,
                                     Map<String, Object> filters, LocalDate date, Period selectedPeriod);

    
    List<Group> loadForLazyDataModel(int first, int pageSize, String sortField, SortOrder sortOrder,
                                     Map<String, Object> filters, Period selectedPeriod);
}
