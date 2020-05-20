package com.mAdmin.model;

import java.util.List;
import java.util.Map;

import com.mAdmin.entity.Period;
import com.mAdmin.repository.PeriodRepository;
import com.mAdmin.util.LazyDataModelUtil;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;


@Component
public class SeasonPeriodLazyDataModel extends LazyDataModel<Period> {

    
    private static final long serialVersionUID = 1L;

    
    @Autowired
    private PeriodRepository periodRepository;

    @Override
    public List<Period> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        first /= pageSize;

        Page<Period> periodPage;

        if (sortField != null) {

            Direction direction = LazyDataModelUtil.getDirection(sortOrder);
            periodPage = periodRepository.findAll(PageRequest.of(first, pageSize, Sort.by(direction, sortField)));

        } else {

            periodPage = periodRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(first, pageSize));
        }
        if (periodPage.getContent().isEmpty() && first > 0) {
            periodPage = periodRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(first - 1, pageSize));
        }

        setRowCount((int) periodPage.getTotalElements());

        return periodPage.getContent();
    }

}
