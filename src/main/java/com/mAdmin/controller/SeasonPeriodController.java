package com.mAdmin.controller;

import com.mAdmin.enumerator.PeriodType;
import com.mAdmin.repository.DiscountRepository;
import com.mAdmin.repository.PeriodRepository;
import com.mAdmin.repository.PoolRepository;
import com.mAdmin.repository.SeasonRepository;
import com.mAdmin.repository.CabinetPeriodRepository;
import com.mAdmin.component.MessageBundleComponent;
import com.mAdmin.entity.Discount;
import com.mAdmin.entity.Period;
import com.mAdmin.entity.Season;
import com.mAdmin.model.SeasonPeriodLazyDataModel;
import com.mAdmin.service.DiscountService;
import com.mAdmin.service.PeriodService;
import com.mAdmin.util.PrimeFacesWrapper;
import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;


@Controller
@Scope(value = "view")
public class SeasonPeriodController {

    
    private Period period;

    
    private Season season;

    
    @Autowired
    private CabinetPeriodRepository cabinetPeriodRepository;

    
    @Autowired
    private PeriodRepository periodRepository;

    
    @Autowired
    private SeasonRepository seasonRepository;

    
    @Autowired
    private DiscountRepository discountRepository;

    
    @Autowired
    private PoolRepository poolRepository;

    
    @Autowired
    private DiscountService discountService;

    
    @Autowired
    private PeriodService periodService;

    
    @Autowired
    private MessageBundleComponent messageBundleComponent;

    
    @Autowired
    private PrimeFacesWrapper primeFacesWrapper;

    
    @Autowired
    private SeasonPeriodLazyDataModel model;

    
    @Value("${paginator.rows}")
    private int rowsPerPage;

    
    @Value("${paginator.rowsPerPageTemplate}")
    private String rowsPerPageTemplate;

    
    private List<Period> periods;

    
    private List<Season> seasons;

    
    private List<Discount> periodDiscounts;

    
    private List<Discount> periodDiscountsBefore;

    
    private LocalDate startDate;

    
    private LocalDate endDate;

    
    private LocalDate minDate;

    
    private PeriodType periodType;

    
    private int index;

    
    private boolean create;

    
    @PostConstruct
    public void init() {
        periods = periodRepository.findAll();
        seasons = seasonRepository.findAll();
        period = new Period();
    }

    
    @Transactional
    public void save() {
        FacesContext context = FacesContext.getCurrentInstance();
        period.setName(period.getName());
        period.setStartDate(startDate);
        period.setEndDate(endDate);
        period.setSeason(season);
        period.setPeriodType(periodType);

        boolean duplicated = false;

        List<Period> periodsByNameList = periodRepository.findAllByName(period.getName());

        List<Period> periodsToCheck = periodRepository.findBySeasonId(season.getId());
        periodsToCheck.removeIf(tp -> tp.getId().equals(period.getId()));

        if (periodsByNameList != null && create) {
            for (Period periodFromList : periodsByNameList) {
                if (periodFromList.getSeason().getId().equals(season.getId())) {
                    duplicated = true;
                    messageBundleComponent.generateMessage(FacesMessage.SEVERITY_ERROR,
                            "admin.period.duplicated", context);
                    break;
                }
            }
        } else if (periodsByNameList != null) {
            for (Period periodFromList : periodsByNameList) {
                if (periodFromList.getSeason().getId().equals(season.getId())
                        && !periodFromList.getId().equals(period.getId())) {
                    duplicated = true;
                    messageBundleComponent.generateMessage(FacesMessage.SEVERITY_ERROR,
                            "admin.period.duplicated", context);
                    break;
                }
            }
        }

        if (periodService.isPeriodOverlapping(period, periodsToCheck)) {
            duplicated = true;
            messageBundleComponent.generateMessage(FacesMessage.SEVERITY_ERROR, "seasonPeriod.overlapping", context);

        }

        if (!create && !duplicated) {
            periods.remove(index);
            periods.add(index, periodRepository.saveAndFlush(period));
            for (Discount periodDiscount : periodDiscounts) {
                periodDiscount.setPeriod(period);
            }
            discountRepository.deleteByPeriodId(period.getId());
            discountRepository.saveAll(periodDiscounts);
            messageBundleComponent.generateMessage(FacesMessage.SEVERITY_WARN, "admin.period.updated.success", context);
        } else if (create && !duplicated) {
            Period currentPeriod = periodRepository.saveAndFlush(period);
            periods.add(currentPeriod);
            for (Discount periodDiscount : periodDiscounts) {
                periodDiscount.setPeriod(currentPeriod);
            }
            discountRepository.deleteByPeriodId(currentPeriod.getId());
            discountRepository.saveAll(periodDiscounts);
            messageBundleComponent.generateMessage(FacesMessage.SEVERITY_INFO, "admin.period.created.success", context);
        }

        if (!duplicated) {
            PrimeFaces current = primeFacesWrapper.current();
            current.executeScript("PF('periodDialog').hide();");
        }
    }

    
    public void edit(Long id, int index) {
        this.index = index;
        periodRepository.findById(id).ifPresent(period -> this.period = period);
        this.startDate = this.period.getStartDate();
        this.endDate = this.period.getEndDate();
        this.periodType = this.period.getPeriodType();
        minDate = this.period.getStartDate();
        this.season = this.period.getSeason();
        periodDiscounts = discountRepository.findByPeriod(period);
        periodDiscountsBefore = periodDiscounts;
        setCreate(false);

    }

    
    public void add() {
        period = new Period();
        startDate = null;
        endDate = null;
        season = null;
        periodType = null;
        setCreate(true);
    }

    
    public void showDiscounts() {
        if (create) {
            periodDiscounts = discountService.prepareDefaultDiscountsList(periodType);
        } else if (!period.getPeriodType().equals(periodType)) {
            periodDiscountsBefore = periodDiscounts;
            periodDiscounts = discountService.prepareDefaultDiscountsList(periodType);
        } else {
            periodDiscounts = periodDiscountsBefore;
        }
    }

    
    @Transactional
    public void delete(Period period) {
        FacesContext context = FacesContext.getCurrentInstance();

                discountRepository.deleteByPeriod(period);
                periodRepository.delete(period);

                messageBundleComponent.generateMessage(FacesMessage.SEVERITY_INFO,
                        "seasonPeriod.delete.success", context);
        }

    
    public boolean checkIfSeasonPeriodCannotBeDeleted(Period period) {
        return !cabinetPeriodRepository.findAllByStartDateBeforeAndEndDateAfter(
                period.getEndDate(), period.getStartDate()).isEmpty();
    }

    
    public void setMinDate() {
        minDate = startDate;
        if (endDate != null && startDate != null) {
            if (endDate.compareTo(startDate) < 0) {
                endDate = null;
            }
        }
    }

    
    public List<Discount> discountsByPeriod(Period period) {
        return discountRepository.findByPeriod(period);
    }

    
    public List<Period> getPeriods() {
        return periods;
    }

    
    public void setPeriods(List<Period> periods) {
        this.periods = periods;
    }

    
    public LocalDate getStartDate() {
        return startDate;
    }

    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    
    public LocalDate getEndDate() {
        return endDate;
    }

    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    
    public LocalDate getMinDate() {
        return minDate;
    }

    
    public void setMinDate(LocalDate minDate) {
        this.minDate = minDate;
    }

    
    public PeriodType getPeriodType() {
        return periodType;
    }

    
    public void setPeriodType(PeriodType periodType) {
        this.periodType = periodType;
    }

    
    public Period getPeriod() {
        return period;
    }

    
    public void setPeriod(Period period) {
        this.period = period;
    }

    
    public boolean isCreate() {
        return create;
    }

    
    public void setCreate(boolean create) {
        this.create = create;
    }

    
    public List<Season> getSeasons() {
        return seasons;
    }

    
    public void setSeasons(List<Season> seasons) {
        this.seasons = seasons;
    }

    
    public Season getSeason() {
        return season;
    }

    
    public void setSeason(Season season) {
        this.season = season;
    }

    
    public List<Discount> getPeriodDiscounts() {
        return periodDiscounts;
    }

    
    public void setPeriodDiscounts(List<Discount> periodDiscounts) {
        this.periodDiscounts = periodDiscounts;
    }

    
    public SeasonPeriodLazyDataModel getModel() {
        return model;
    }

    
    public void setModel(SeasonPeriodLazyDataModel model) {
        this.model = model;
    }

    
    public int getRowsPerPage() {
        return rowsPerPage;
    }

    
    public String getRowsPerPageTemplate() {
        return rowsPerPageTemplate;
    }

}
