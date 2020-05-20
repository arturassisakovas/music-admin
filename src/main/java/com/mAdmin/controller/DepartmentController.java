package com.mAdmin.controller;

import com.mAdmin.enumerator.City;
import com.mAdmin.repository.GroupRepository;
import com.mAdmin.repository.DepartmentNonWorkdayRepository;
import com.mAdmin.repository.PoolRepository;
import com.mAdmin.repository.SeasonRepository;
import com.mAdmin.repository.CabinetRepository;
import com.mAdmin.component.MessageBundleComponent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.mAdmin.entity.Pool;
import com.mAdmin.entity.PoolNonWorkday;
import com.mAdmin.entity.Season;
import com.mAdmin.model.DepartmentLazyDataModel;
import com.mAdmin.util.DateUtil;
import com.mAdmin.util.PrimeFacesWrapper;


@Controller
@Scope(value = "view")
public class DepartmentController {

    
    @Autowired
    private DepartmentNonWorkdayRepository departmentNonWorkdayRepository;

    
    @Autowired
    private PoolRepository poolRepository;

    
    @Autowired
    private SeasonRepository seasonRepository;

    
    @Autowired
    private CabinetRepository cabinetRepository;

    
    @Autowired
    private MessageBundleComponent messageBundleComponent;

    
    @Autowired
    private DepartmentLazyDataModel model;

    
    @Autowired
    private GroupRepository groupRepository;

    
    @Autowired
    private PrimeFacesWrapper primeFacesWrapper;

    
    private List<Pool> pools;

    
    private List<Pool> availablePools;

    
    private List<Season> seasons;

    
    private List<Pool> seasonPools;

    
    private List<PoolNonWorkday> selectedPoolNonWorkdaysList = new ArrayList<>();

    
    private List<LocalDate> selectedPoolNonWorkdaysLocalDateList = new ArrayList<>();

    
    private List<Long> deletedNonWorkdayIdList;

    
    private Pool pool;

    
    private PoolNonWorkday selectedPoolNonWorkday;

    
    private Season selectedSeason;

    
    private Pool selectedPool;

    
    private int index;

    
    private boolean create;

    
    private boolean deleted;

    
    private boolean editableFields;

    
    @Value("${paginator.rows}")
    private int rowsPerPage;

    
    @Value("${paginator.rowsPerPageTemplate}")
    private String rowsPerPageTemplate;

    
    @PostConstruct
    public void init() {
        pools = poolRepository.findAll();
        pool = new Pool();
        selectedPoolNonWorkday = null;
        seasons = seasonRepository.findAll();
        seasons.sort((s1, s2) -> s2.getEndDate().compareTo(s1.getEndDate()));
        setSeasons(seasons);
    }

    
    public void onLoad() {
        LocalDate currentDate = LocalDate.now();
        if (!seasons.isEmpty()) {
            seasons.forEach(s -> {
                if (DateUtil.isBeforeOrEqual(currentDate, s.getEndDate())
                        && DateUtil.isAfterOrEqual(currentDate, s.getStartDate())) {
                    selectedSeason = s;
                }
            });
            if (selectedSeason == null) {
                selectedSeason = seasons.get(0);
            }
            seasonPools = selectedSeason.getPools();
            Collections.reverse(seasonPools);
            filterPools();
        }
    }

    
    public void save() {
        FacesContext context = FacesContext.getCurrentInstance();

        boolean duplicated = false;

        List<Pool> poolsByNameList = poolRepository.findAllByName(pool.getName());
        List<Pool> poolsByAbbreviationList = poolRepository.findAllByAbbreviation(pool.getAbbreviation());

        if (poolsByNameList != null && create) {
            for (Pool poolFromList : poolsByNameList) {
                if (poolFromList.getCity().equals(pool.getCity())) {
                    duplicated = true;
                    messageBundleComponent.generateMessage(FacesMessage.SEVERITY_ERROR,
                            "admin.pool.duplicated", context);
                    break;
                }
            }
        } else if (poolsByNameList != null) {
            for (Pool poolFromList : poolsByNameList) {
                if (poolFromList.getCity().equals(pool.getCity()) && !poolFromList.getId().equals(pool.getId())) {
                    duplicated = true;
                    messageBundleComponent.generateMessage(FacesMessage.SEVERITY_ERROR,
                            "admin.pool.duplicated", context);
                    break;
                }
            }
        }
        if (!poolsByAbbreviationList.isEmpty() && create) {
            duplicated = true;
            messageBundleComponent.generateMessage(FacesMessage.SEVERITY_ERROR, "admin.pool.Abbreviation", context);
        }

        if (!create && !duplicated) {
            pools.remove(index);
            pool = poolRepository.saveAndFlush(pool);
            pools.add(index, pool);
            messageBundleComponent.generateMessage(FacesMessage.SEVERITY_WARN, "admin.pool.updated.success", context);
        } else if (create && !duplicated) {
            pool = poolRepository.saveAndFlush(pool);
            pools.add(pool);
            messageBundleComponent.generateMessage(FacesMessage.SEVERITY_INFO, "admin.pool.created.success", context);
        }

        if (!duplicated) {
            PrimeFaces current = primeFacesWrapper.current();
            current.executeScript("PF('poolDialog').hide();");
        }

        filterPools();

    }

    
    public void saveNonWorkdays() {

        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle msg = context.getApplication().getResourceBundle(context, "msg");
        if (pool.getId() == null) {
            relateSeasonWithPool();
        }

        if (selectedPoolNonWorkday != null) {
            selectedPoolNonWorkdaysList.forEach(p -> {
                p.setPool(selectedPool);
                p.setSeason(selectedSeason);
            });
            departmentNonWorkdayRepository.saveAll(selectedPoolNonWorkdaysList);
        }

        if (deleted) {
            for (Long deletedNonWorkdayId : deletedNonWorkdayIdList) {
                departmentNonWorkdayRepository.deleteById(deletedNonWorkdayId);
            }
        }

        poolRepository.saveAndFlush(pool);
        filterPools();

        if (create) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success!",
                    msg.getString("admin.pool.season.created")));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success!",
                    msg.getString("admin.pool.season.edited")));
        }

    }

    
    public void filterPools() {
        if (selectedSeason != null) {
            seasonPools = poolRepository.findPoolsBySeason(selectedSeason.getId());
            availablePools = new ArrayList<>(pools);
            availablePools.removeAll(seasonPools);
            Collections.reverse(seasonPools);
        }
    }

    
    public void edit(Long id, int index) {
        this.index = index;
        poolRepository.findById(id).ifPresent(pool -> this.pool = pool);
        setCreate(false);
        setDeleted(false);

        selectedPool = this.pool;
        selectedPoolNonWorkdaysLocalDateList = new ArrayList<>();
        selectedPoolNonWorkdaysList = new ArrayList<>();
        deletedNonWorkdayIdList = new ArrayList<>();
        selectedPoolNonWorkdaysList = departmentNonWorkdayRepository.findByPoolAndSeason(pool, selectedSeason);

        for (PoolNonWorkday nonWorkday : selectedPoolNonWorkdaysList) {
            selectedPoolNonWorkdaysLocalDateList.add(nonWorkday.getDate());
        }

        selectedPoolNonWorkdaysLocalDateList.sort(null);
        setEditableFields(false);
    }

    
    public void add() {
        pool = new Pool();
        selectedPoolNonWorkdaysLocalDateList = new ArrayList<>();
        selectedPoolNonWorkdaysList = new ArrayList<>();
        deletedNonWorkdayIdList = new ArrayList<>();
        setCreate(true);
        setEditableFields(true);
    }

    
    public void delete(Long id, int index) {
        poolRepository.deleteById(id);
        pools.remove(index);
        filterPools();
        messageBundleComponent.generateMessage(FacesMessage.SEVERITY_INFO,
                "admin.pool.deleted.success", FacesContext.getCurrentInstance());
    }

    
    public boolean checkIfPoolHasTrack(Long id) {
        return cabinetRepository.findFirstByPoolId(id).isPresent();
    }

    
    @Transactional
    public void deleteDaysOfPool(Long id) {

        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle msg = context.getApplication().getResourceBundle(context, "msg");

        departmentNonWorkdayRepository.deleteByPoolIdAndSeasonId(id, selectedSeason.getId());
        filterPools();

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Success!", msg.getString("admin.pool.season.deleted")));

        poolRepository.findById(id).ifPresent(pool -> this.pool = pool);
        Set<Season> seasons = pool.getSeasons();
        seasons.removeIf(x -> (x.getId().equals(selectedSeason.getId())));
        pool.setSeasons(seasons);
        filterPools();

    }

    
    public void markNonWorkdays(SelectEvent e) {

        
        selectedPoolNonWorkday = new PoolNonWorkday();
        LocalDate selectedDate = (LocalDate) e.getObject();

        
        if (selectedPoolNonWorkdaysLocalDateList == null
                || !selectedPoolNonWorkdaysLocalDateList.contains(selectedDate)) {

            if (selectedPoolNonWorkdaysLocalDateList == null) {
                selectedPoolNonWorkdaysLocalDateList = new ArrayList<>();
            }

            selectedPoolNonWorkday.setDate(selectedDate);
            selectedPoolNonWorkdaysList.add(selectedPoolNonWorkday);
            selectedPoolNonWorkdaysLocalDateList.add(selectedDate);
            selectedPoolNonWorkdaysLocalDateList.sort(null);

        }

    }

    
    public void deleteNonWorkDays(UnselectEvent e) {

        String inputDate = e.getObject().toString();
        LocalDate unselectedDate = LocalDate.parse(inputDate);
        List<PoolNonWorkday> savedPoolNonWorkdaysList;

        if (!create) {
            savedPoolNonWorkdaysList = departmentNonWorkdayRepository.findByPool(pool);
            for (PoolNonWorkday workday : selectedPoolNonWorkdaysList) {
                if (workday.getDate().equals(unselectedDate)) {
                    selectedPoolNonWorkdaysList.remove(workday);
                    if (savedPoolNonWorkdaysList.contains(workday)) {
                        deletedNonWorkdayIdList.add(workday.getId());
                        setDeleted(true);
                        break;
                    }
                    break;

                }
            }
        } else {
            for (PoolNonWorkday workday : selectedPoolNonWorkdaysList) {
                if (workday.getDate().equals(unselectedDate)) {
                    selectedPoolNonWorkdaysList.remove(workday);
                    break;
                }

            }

        }

    }

    
    public List<LocalDate> showNonWorkdays(Pool pool) {
        List<LocalDate> savedNonWorkdays = new ArrayList<>();
        List<PoolNonWorkday> savedPoolNonWorkdaysList = departmentNonWorkdayRepository.findByPoolAndSeason(pool,
                selectedSeason);
        for (PoolNonWorkday nonWorkday : savedPoolNonWorkdaysList) {
            savedNonWorkdays.add(nonWorkday.getDate());
        }
        savedNonWorkdays.sort(null);
        return savedNonWorkdays;
    }

    
    public List<String> completeEnum(String query) {
        List<String> cities = new ArrayList<>(City.values().length);
        for (City city : City.values()) {
            cities.add(city.toString());
        }
        return cities.stream().filter(f -> StringUtils.containsIgnoreCase(f, query.trim()))
                .collect(Collectors.toList());
    }

    
    public void relateSeasonWithPool() {
        poolRepository.findById(selectedPool.getId()).ifPresent(pool -> this.pool = pool);
        Set<Season> seasons = pool.getSeasons();
        seasons.add(selectedSeason);
        pool.setSeasons(seasons);
}

    
    public boolean checkIfNonWorkDayCanBeModified(long id) {
        poolRepository.findById(id).ifPresent(pool -> this.pool = pool);
        return !groupRepository.findActiveGroupsByProvidedPool(
                pool, selectedSeason.getStartDate(), selectedSeason.getEndDate()).isEmpty();
    }

    
    public List<Pool> getPools() {
        return pools;
    }

    
    public void setPools(List<Pool> pools) {
        this.pools = pools;
    }

    
    public Pool getPool() {
        return pool;
    }

    
    public void setPool(Pool pool) {
        this.pool = pool;
    }

    
    public boolean isCreate() {
        return create;
    }

    
    public void setCreate(boolean create) {
        this.create = create;
    }

    
    public DepartmentLazyDataModel getModel() {
        return model;
    }

    
    public void setModel(DepartmentLazyDataModel model) {
        this.model = model;
    }

    
    public boolean isDeleted() {
        return deleted;
    }

    
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    
    public PoolNonWorkday getSelectedPoolNonWorkday() {
        return selectedPoolNonWorkday;
    }

    
    public void setSelectedPoolNonWorkday(PoolNonWorkday selectedPoolNonWorkday) {
        this.selectedPoolNonWorkday = selectedPoolNonWorkday;
    }

    
    public List<PoolNonWorkday> getSelectedPoolNonWorkdaysList() {
        return selectedPoolNonWorkdaysList;
    }

    
    public void setSelectedPoolNonWorkdaysList(List<PoolNonWorkday> selectedPoolNonWorkdaysList) {
        this.selectedPoolNonWorkdaysList = selectedPoolNonWorkdaysList;
    }

    
    public List<LocalDate> getSelectedPoolNonWorkdaysLocalDateList() {
        return selectedPoolNonWorkdaysLocalDateList;
    }

    
    public void setSelectedPoolNonWorkdaysLocalDateList(List<LocalDate> selectedPoolNonWorkdaysLocalDateList) {
        this.selectedPoolNonWorkdaysLocalDateList = selectedPoolNonWorkdaysLocalDateList;
    }

    
    public List<Long> getDeletedNonWorkdayIdList() {
        return deletedNonWorkdayIdList;
    }

    
    public void setDeletedNonWorkdayIdList(List<Long> deletedNonWorkdayIdList) {
        this.deletedNonWorkdayIdList = deletedNonWorkdayIdList;
    }

    
    public int getRowsPerPage() {
        return rowsPerPage;
    }

    
    public String getRowsPerPageTemplate() {
        return rowsPerPageTemplate;
    }

    
    public List<Season> getSeasons() {
        return seasons;
    }

    
    public void setSeasons(List<Season> seasons) {
        this.seasons = seasons;
    }

    
    public Season getSelectedSeason() {
        return selectedSeason;
    }

    
    public void setSelectedSeason(Season selectedSeason) {
        this.selectedSeason = selectedSeason;
    }

    
    public Pool getSelectedPool() {
        return selectedPool;
    }

    
    public void setSelectedPool(Pool selectedPool) {
        this.selectedPool = selectedPool;
    }

    
    public List<Pool> getSeasonPools() {
        return seasonPools;
    }

    
    public void setSeasonPools(List<Pool> seasonPools) {
        this.seasonPools = seasonPools;
    }

    
    public List<Pool> getAvailablePools() {
        return availablePools;
    }

    
    public void setAvailablePools(List<Pool> availablePools) {
        this.availablePools = availablePools;
    }

    
    public boolean isEditableFields() {
        return editableFields;
    }

    
    public void setEditableFields(boolean editableFields) {
        this.editableFields = editableFields;
    }
}
