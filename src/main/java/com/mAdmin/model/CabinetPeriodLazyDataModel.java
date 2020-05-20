package com.mAdmin.model;

import java.util.List;
import java.util.Map;

import com.mAdmin.entity.Period;
import com.mAdmin.entity.TrackPeriod;
import com.mAdmin.repository.CabinetPeriodRepository;
import com.mAdmin.util.LazyDataModelUtil;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;


@Component
@Scope("session")
public class CabinetPeriodLazyDataModel extends LazyDataModel<TrackPeriod> {

    
    private static final long serialVersionUID = 1L;

    
    @Autowired
    private CabinetPeriodRepository cabinetPeriodRepository;

    
    private Period selectedPeriod;
    @Override
    public List<TrackPeriod> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        first /= pageSize;

        Page<TrackPeriod> trackPeriodPage;

        if (sortField != null) {

            Direction direction = LazyDataModelUtil.getDirection(sortOrder);
            if (selectedPeriod != null) {
                trackPeriodPage = cabinetPeriodRepository.findByStartDateBeforeAndEndDateAfter(
                        PageRequest.of(first, pageSize, Sort.by(
                                direction, sortField)), selectedPeriod.getEndDate(), selectedPeriod.getStartDate());
            } else {
                trackPeriodPage = cabinetPeriodRepository.findAll(
                        PageRequest.of(first, pageSize, Sort.by(direction, sortField)));
            }

        } else {
            if (selectedPeriod != null) {
                trackPeriodPage = cabinetPeriodRepository.findByStartDateBeforeAndEndDateAfterOrderByCreatedAtDesc(
                        PageRequest.of(first, pageSize), selectedPeriod.getEndDate(), selectedPeriod.getStartDate());
            } else {
                trackPeriodPage = cabinetPeriodRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(first, pageSize));
            }
        }

        setRowCount((int) trackPeriodPage.getTotalElements());

        return trackPeriodPage.getContent();
    }
    
    public Period getSelectedPeriod() {
        return selectedPeriod;
    }

    
    public void setSelectedPeriod(Period selectedPeriod) {
        this.selectedPeriod = selectedPeriod;
    }

}
