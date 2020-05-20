package com.mAdmin.controller;

import com.mAdmin.repository.GroupRepository;
import com.mAdmin.repository.PeriodRepository;
import com.mAdmin.repository.DepartmentNonWorkdayRepository;
import com.mAdmin.repository.SeasonRepository;
import com.mAdmin.component.MessageBundleComponent;
import com.mAdmin.entity.Period;
import com.mAdmin.entity.PoolNonWorkday;
import com.mAdmin.entity.Season;
import com.mAdmin.model.SeasonLazyDataModel;
import com.mAdmin.service.SeasonService;
import com.mAdmin.util.PrimeFacesWrapper;
import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static java.time.temporal.TemporalAdjusters.lastDayOfYear;


@Controller
@Scope(value = "view")
public class SeasonController {

    
    private Season season;

    
    @Autowired
    private SeasonRepository seasonRepository;

    
    @Autowired
    private SeasonService seasonService;

    
    @Autowired
    private PeriodRepository periodRepository;

    
    @Autowired
    private DepartmentNonWorkdayRepository departmentNonWorkdayRepository;

    
    @Autowired
    private GroupRepository groupRepository;

    
    @Autowired
    private MessageBundleComponent messageBundleComponent;

    
    @Autowired
    private SeasonLazyDataModel model;

    
    @Autowired
    private PrimeFacesWrapper primeFacesWrapper;

    
    @Value("${paginator.rows}")
    private int rowsPerPage;

    
    @Value("${paginator.rowsPerPageTemplate}")
    private String rowsPerPageTemplate;

    
    private List<Season> seasons;

    
    private LocalDate startDate;

    
    private LocalDate endDate;

    
    private LocalDate minDate;

    
    private LocalDate maxDate;

    
    private BigDecimal smallGroupPrice, bigGroupPrice;

    
    private int index;

    
    private boolean create;

    
    @PostConstruct
    public void init() {
        seasons = seasonRepository.findAll();
        season = new Season();
    }

    
    public void save() {

        FacesContext context = FacesContext.getCurrentInstance();
        season.setName(season.getName());
        season.setStartDate(startDate);
        season.setEndDate(endDate);
        season.setSmallGroupPrice(smallGroupPrice);
        season.setBigGroupPrice(bigGroupPrice);

        boolean duplicated = false;

        List<Season> seasonsToCheck = seasonRepository.findAll();
        seasonsToCheck.removeIf(tp -> tp.getId().equals(season.getId()));

        if (seasonRepository.findByName(season.getName()) != null && create) {
            duplicated = true;
            messageBundleComponent.generateMessage(FacesMessage.SEVERITY_ERROR, "admin.season.duplicated", context);
        } else if (seasonRepository.findByName(season.getName()) != null && !create
                && !seasonRepository.findByName(season.getName()).getId().equals(season.getId())) {
            duplicated = true;
            messageBundleComponent.generateMessage(FacesMessage.SEVERITY_ERROR, "admin.season.duplicated", context);
        } else if (seasonService.isPeriodOverlapping(season, seasonsToCheck)) {
            duplicated = true;
            messageBundleComponent.generateMessage(FacesMessage.SEVERITY_ERROR, "season.overlapping", context);

        }

        if (!create && !duplicated) {
            seasons.remove(index);
            seasons.add(index, seasonRepository.saveAndFlush(season));
            messageBundleComponent.generateMessage(FacesMessage.SEVERITY_WARN, "season.updated.success", context);
        } else if (create && !duplicated) {
            seasons.add(seasonRepository.saveAndFlush(season));
            messageBundleComponent.generateMessage(FacesMessage.SEVERITY_INFO, "season.created.success", context);
        }

        if (!duplicated) {
            PrimeFaces current = primeFacesWrapper.current();
            current.executeScript("PF('seasonDialog').hide();");
        }
    }

    
    public void edit(Long id, int index) {
        this.index = index;
        seasonRepository.findById(id).ifPresent(season -> this.season = season);
        this.startDate = this.season.getStartDate();
        this.endDate = this.season.getEndDate();
        this.smallGroupPrice = this.season.getSmallGroupPrice();
        this.bigGroupPrice = this.season.getBigGroupPrice();
        minDate = this.season.getStartDate();
        setCreate(false);
    }

    
    public void add() {
        season = new Season();
        startDate = null;
        endDate = null;
        smallGroupPrice = BigDecimal.ZERO;
        bigGroupPrice = BigDecimal.ZERO;
        setCreate(true);
    }

    
    public void delete(Long id, int index) {
            seasonRepository.deleteById(id);
            seasons.remove(index);
            messageBundleComponent.generateMessage(FacesMessage.SEVERITY_INFO,
                    "season.deleted.success", FacesContext.getCurrentInstance());

    }

    
    public boolean disableDelete(Long id) {
        Optional<Period> isPeriodExist = periodRepository.findFirstBySeasonId(id);
        Optional<PoolNonWorkday> isPoolNonWorkdayExist = departmentNonWorkdayRepository.findFirstBySeasonId(id);
        return isPeriodExist.isPresent() && isPoolNonWorkdayExist.isPresent();
    }

    
    public boolean disableEdit(long seasonId) {
        Season season = seasonRepository.findById(seasonId).get();
        return !groupRepository.findByStartDateAndEndDateFitInPeriod(season.getStartDate(), season.getEndDate()).
                isEmpty();
    }

    
    public void setMinDate() {
        minDate = startDate;
        maxDate = startDate.plusYears(1).with(lastDayOfYear());
        if (endDate != null && startDate != null) {
            if (endDate.compareTo(startDate) < 0) {
                endDate = null;
            }
        }
    }

    
    public List<Season> getSeasons() {
        return seasons;
    }

    
    public void setSeasons(List<Season> seasons) {
        this.seasons = seasons;
    }

    
    public LocalDate getStartDate() {
        return startDate;
    }

    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    
    public LocalDate getMinDate() {
        return minDate;
    }

    
    public void setMinDate(LocalDate minDate) {
        this.minDate = minDate;
    }

    
    public LocalDate getEndDate() {
        return endDate;
    }

    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    
    public BigDecimal getSmallGroupPrice() {
        return smallGroupPrice;
    }

    
    public void setSmallGroupPrice(BigDecimal smallGroupPrice) {
        this.smallGroupPrice = smallGroupPrice;
    }

    
    public BigDecimal getBigGroupPrice() {
        return bigGroupPrice;
    }

    
    public void setBigGroupPrice(BigDecimal bigGroupPrice) {
        this.bigGroupPrice = bigGroupPrice;
    }

    
    public Season getSeason() {
        return season;
    }

    
    public void setSeason(Season season) {
        this.season = season;
    }

    
    public boolean isCreate() {
        return create;
    }

    
    public void setCreate(boolean create) {
        this.create = create;
    }

    
    public SeasonLazyDataModel getModel() {
        return model;
    }

    
    public void setModel(SeasonLazyDataModel model) {
        this.model = model;
    }

    
    public int getRowsPerPage() {
        return rowsPerPage;
    }

    
    public String getRowsPerPageTemplate() {
        return rowsPerPageTemplate;
    }

    
    public LocalDate getMaxDate() {
        return maxDate;
    }

    
    public void setMaxDate(LocalDate maxDate) {
        this.maxDate = maxDate;
    }

}
