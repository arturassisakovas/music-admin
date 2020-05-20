package com.mAdmin.service;

import com.mAdmin.enumerator.SwimmingLevel;
import com.mAdmin.repository.GroupRepository;
import com.mAdmin.repository.CabinetWeekdayRepository;
import com.mAdmin.repository.CabinetWorkingHourRepository;
import com.mAdmin.entity.Group;
import com.mAdmin.entity.Period;
import com.mAdmin.entity.Pool;
import com.mAdmin.entity.Season;
import com.mAdmin.model.GroupLazyDataModel;
import com.mAdmin.model.OperatingGroupLazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.CollectionUtils;
import org.springframework.cglib.core.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class GroupServiceImpl implements GroupService {

    
    @Autowired
    private GroupRepository groupRepository;

    
    @Autowired
    private CabinetWorkingHourRepository cabinetWorkingHourRepository;

    
    @Autowired
    private CabinetWeekdayRepository cabinetWeekdayRepository;

    
    @Autowired
    private SeasonService seasonService;

    
    @Autowired
    private PeriodService periodService;

    
    @Value("${group.small.numofattendees}")
    private int smallGroupNumOfAttendees;

    
    @Value("${group.big.numofattendees}")
    private int bigGroupNumOfAttendees;

    
    @Autowired
    private GroupLazyDataModel groupLazyDataModel;

    
    @Autowired
    private OperatingGroupLazyDataModel operatingGroupLazyDataModel;

    
    @Override
    public boolean isAvailable() {
        LocalDate currentDate = LocalDate.now();
        return !groupRepository.findByEndDateAfterOrEqual(currentDate).isEmpty();
    }

    @Override
    public List<Group> getAllAvailable() {

        LocalDate currentDate = LocalDate.now();
        List<Group> groups = new ArrayList<Group>();
        if (!groupRepository.findByEndDateAfterOrEqual(currentDate).isEmpty()) {
            groups = groupRepository.findByEndDateAfterOrEqual(currentDate).stream().filter(g ->
            g.getIsPublic()).collect(Collectors.toList());

            CollectionUtils.filter(groups, new Predicate() {

                @Override
                public boolean evaluate(Object o) {
                    return ((Group) o).getAttendees().size()
                            + ((Group) o).getNewAttendees().size() < ((Group) o).getNumOfAttendees();
                }
            });
        }
        return groups;
    }

    
    @Override
    public List<Group> getAll() {
        return groupRepository.findAll();
    }

    
    @Override
    public Optional<Group> getById(Long id) {
        return groupRepository.findById(id);
    }

    
    @Override
    public int getAvailableGroupSpaces(Group group) {
        return group.getNumOfAttendees() - group.getAttendees().size() + group.getNewAttendees().size();
    }

    
    @Override
    public List<Group> filterByAgeGroup(List<Group> groups, LocalDate birthdate) {

        int age = java.time.Period.between(birthdate, LocalDate.now()).getYears();
        List<Group> filteredList = new ArrayList<Group>();
        for (Group group : groups) {
            int ageFrom = Integer.parseInt(group.getAgeGroup().getValue().split("-")[0]);
            int ageTo = Integer.parseInt(group.getAgeGroup().getValue().split("-")[1]);
            if (ageFrom <= age && age <= ageTo) {
                filteredList.add(group);
            }
        }
        return filteredList;
    }

    
    @Override
    public List<Group> filterBySwimmingLevel(List<Group> groups, SwimmingLevel swimmingLvl) {

        List<Group> filteredList = new ArrayList<Group>();
        for (Group group : groups) {
            if (group.getSwimmingLevel() == swimmingLvl) {
                filteredList.add(group);
            }
        }
        return filteredList;
    }

    
    @Override
    public List<Group> filterByWorkoutsPerWeek(List<Group> groups, int workoutsPerWeek) {

        List<Group> filteredList = new ArrayList<Group>();
        for (Group group : groups) {
            if (group.getWorkoutsPerWeek().getValue() == workoutsPerWeek) {
                filteredList.add(group);
            }
        }
        return filteredList;
    }

    
    @Override
    public List<Group> filterByPool(List<Group> groups, Pool pool) {
        List<Group> filteredList = new ArrayList<Group>();

        for (Group group : groups) {
              if (group.getPool() == pool) {
                filteredList.add(group);
            }
        }
        return filteredList;
    }

    
    @Override
    public List<Group> filterByPool(List<Group> groups, Long poolId) {

        List<Group> filteredList = new ArrayList<Group>();
        for (Group group : groups) {
            if (group.getPool().getId() == poolId) {
                filteredList.add(group);
            }
        }
        return filteredList;
    }

    
    @Override
    public BigDecimal getSingleWorkoutPrice(Group group) {
        BigDecimal singleWorkoutPrice = BigDecimal.ZERO;
        for (Season season : seasonService.getAllAvailable()) {
            if (seasonService.checkIfDatesFitIntoSeason(season, group.getStartDate(), group.getEndDate())) {
                if (group.getNumOfAttendees() == smallGroupNumOfAttendees) {
                    singleWorkoutPrice = season.getSmallGroupPrice();
                } else if (group.getNumOfAttendees() == bigGroupNumOfAttendees) {
                    singleWorkoutPrice = season.getBigGroupPrice();
                }
            }
        }
        return singleWorkoutPrice;
    }

    
    @Override
    public Long getGroupPeriodId(Group group) {
        Long periodId = null;
        for (Period period : periodService.getAllAvailable()) {
            if (periodService.checkIfDatesFitIntoPeriod(period, group.getStartDate(), group.getEndDate())) {
                periodId = period.getId();
            }
        }
        return periodId;
    }

    @Override
    public Period getGroupPeriod(Group group) {
        Period period = null;
        for (Period p : periodService.getAllAvailable()) {
            if (periodService.checkIfDatesFitIntoPeriod(p, group.getStartDate(), group.getEndDate())) {
                period = p;
            }
        }
        return period;
    }

    @Override
    public List<Group> loadForLazyDataModel(int first, int pageSize, String sortField,
                                            SortOrder sortOrder, Map<String, Object> filters,
                                            LocalDate date, Period selectedPeriod) {
        first /= pageSize;
        Page<Group> groupPage;
        String globalFilter = (String) filters.get("globalFilter");

        if (globalFilter != null && !globalFilter.isEmpty()) {

            List<Group> groups;
            String query = globalFilter.toLowerCase();

            if (selectedPeriod == null) {
                groups = groupRepository.findByIsPublicTrueAndNameContainingOrdered(query);
            } else {
                groups = groupRepository.findByIsPublicTrueAndNameContainingOrdered(
                        selectedPeriod.getStartDate(), selectedPeriod.getEndDate(), query);
            }

            List<Group> groupsListPage = new ArrayList<>();

            for (int i = first; i < groups.size(); i++) {
                groupsListPage.add(groups.get(i));
            }

            if (date == null) {
                groupLazyDataModel.setRowCount(groups.size());
            } else {
                operatingGroupLazyDataModel.setRowCount(groups.size());
            }

            return groupsListPage;
        }

        if (sortField != null) {
            Sort.Direction direction;
            if (sortOrder.ordinal() == 0) {
                direction = Sort.Direction.ASC;
            } else {
                direction = Sort.Direction.DESC;
            }

            if (selectedPeriod == null) {
                if (date == null) {
                    groupPage = groupRepository.findByIsPublicTrue(
                            PageRequest.of(first, pageSize, Sort.by(direction, sortField)));
                } else {
                    groupPage = groupRepository.findByIsPublicTrueAndIntoFuture(date,
                            PageRequest.of(first, pageSize, Sort.by(direction, sortField)));
                }
            } else {
                groupPage = groupRepository.findByIsPublicTrue(
                        selectedPeriod.getStartDate(), selectedPeriod.getEndDate(),
                        PageRequest.of(first, pageSize, Sort.by(direction, sortField)));
            }
        } else {
            if (selectedPeriod == null) {
                if (date == null) {
                    groupPage = groupRepository.findByIsPublicTrue(PageRequest.of(
                            first, pageSize, Sort.by(Sort.Direction.ASC, "totalNumOfAttendeesRatio")));
                } else {
                    groupPage = groupRepository.findByIsPublicTrueAndIntoFuture(date,
                        PageRequest.of(first, pageSize, Sort.by(Sort.Direction.ASC, "totalNumOfAttendeesRatio")));
                }
            } else {
                groupPage = groupRepository.findByIsPublicTrue(selectedPeriod.getStartDate(),
                        selectedPeriod.getEndDate(),
                        PageRequest.of(first, pageSize, Sort.by(Sort.Direction.ASC, "totalNumOfAttendeesRatio")));
            }
        }

        if (groupPage.getContent().isEmpty() && first > 0) {
            if (selectedPeriod == null) {
                if (date == null) {
                    groupPage = groupRepository.findByIsPublicTrue(PageRequest.of(
                            first - 1, pageSize, Sort.by(
                                    Sort.Direction.ASC, "totalNumOfAttendeesRatio")));
                } else {
                    groupPage = groupRepository.findByIsPublicTrueAndIntoFuture(date,
                            PageRequest.of(first - 1, pageSize, Sort.by(
                                    Sort.Direction.ASC, "totalNumOfAttendeesRatio")));
                }
            } else {
                groupPage = groupRepository.findByIsPublicTrue(selectedPeriod.getStartDate(),
                        selectedPeriod.getEndDate(), PageRequest.of(
                                first - 1, pageSize, Sort.by(
                                        Sort.Direction.ASC, "totalNumOfAttendeesRatio")));
            }
        }

        if (date == null) {
            groupLazyDataModel.setRowCount((int) groupPage.getTotalElements());
        } else {
            operatingGroupLazyDataModel.setRowCount((int) groupPage.getTotalElements());
        }
        return groupPage.getContent();
    }

    @Override
    public List<Group> loadForLazyDataModel(int first, int pageSize, String sortField,
                                            SortOrder sortOrder, Map<String, Object> filters,
                                            Period selectedPeriod) {
        return loadForLazyDataModel(first, pageSize, sortField,
                sortOrder, filters, null, selectedPeriod);
    }

}
