package com.mAdmin.model;

import java.util.List;
import java.util.Map;

import com.mAdmin.entity.Group;
import com.mAdmin.entity.Period;
import com.mAdmin.service.GroupService;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class GroupLazyDataModel extends LazyDataModel<Group> {

    
    private static final long serialVersionUID = 1L;

    
    private Period selectedPeriod = null;

    
    @Autowired
    private GroupService groupService;

    @Override
    public List<Group> load(int first, int pageSize, String sortField, SortOrder sortOrder,
            Map<String, Object> filters) {

        return groupService.loadForLazyDataModel(first, pageSize, sortField,
                sortOrder, filters, selectedPeriod);
    }

    
    public Period getSelectedPeriod() {
        return selectedPeriod;
    }

    
    public void setSelectedPeriod(Period selectedPeriod) {
        this.selectedPeriod = selectedPeriod;
    }

}
