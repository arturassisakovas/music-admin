package com.mAdmin.model;

import java.util.List;
import java.util.Map;

import com.mAdmin.entity.Attendee;
import com.mAdmin.repository.AttendeeRepository;
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
public class ReservedAttendeeLazyDataModel extends LazyDataModel<Attendee> {

    
    @Autowired
    private AttendeeRepository attendeeRepository;

    
    private static final long serialVersionUID = 1L;

    @Override
    public List<Attendee> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        first /= pageSize;

        Page<Attendee> reservedAttendeePage;

        if (sortField != null) {

            Direction direction = LazyDataModelUtil.getDirection(sortOrder);
            reservedAttendeePage = attendeeRepository.findByActiveFalse(PageRequest.of(
                    first, pageSize, Sort.by(direction, sortField)));

        } else {

            reservedAttendeePage = attendeeRepository
                    .findByActiveFalseOrderByCreatedAtDesc(PageRequest.of(first, pageSize));
        }
        if (reservedAttendeePage.getContent().isEmpty() && first > 0) {
            reservedAttendeePage = attendeeRepository.findByActiveFalseOrderByCreatedAtDesc(
                    PageRequest.of(first - 1, pageSize));
        }

        setRowCount((int) reservedAttendeePage.getTotalElements());

        return reservedAttendeePage.getContent();
    }

}
